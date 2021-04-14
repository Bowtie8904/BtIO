package bt.io.sound;

import javax.sound.sampled.Clip;

import bt.scheduler.Threads;
import bt.utils.Exceptions;
import bt.utils.NumberUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author &#8904
 */
public class Sound implements LineStopListener
{
    public static final String MASTER_CATEGORY = "master";
    protected static Map<String, SoundCategory> soundCategories = new ConcurrentHashMap<>();
    private SoundSupplier supplier;
    private float volume = 1;
    private int instanceHandle = -1;
    private boolean running = false;
    private boolean isPaused = false;
    private Object lock = new Object();

    public static synchronized void pauseAll()
    {
        pauseAll(Sound.MASTER_CATEGORY);
    }

    public static synchronized void pauseAll(String soundCategory)
    {
        soundCategories.get(soundCategory).pauseAll();
    }

    public static synchronized void resumeAll()
    {
        resumeAll(Sound.MASTER_CATEGORY);
    }

    public static synchronized void resumeAll(String soundCategory)
    {
        soundCategories.get(soundCategory).resumeAll();
    }

    public static synchronized void createSoundCategoryIfNotExist(String name)
    {
        name = name.toLowerCase();
        var found = soundCategories.get(name);

        if (found == null)
        {
            SoundCategory category = new SoundCategory(name);
            soundCategories.put(name, category);
        }
    }

    /**
     * Sets the volume of the master volume category.
     *
     * This is a convinience method for the call for {@link #setVolume(String, float) setVolume} for the {@link Sound.MASTER_VOLUME} category.
     *
     * @param volume
     */
    public static void setMasterVolume(float volume)
    {
        setVolume(Sound.MASTER_CATEGORY, volume);
    }

    /**
     * Sets the volume of the given volume category.
     *
     * If the given categoryName does not map to an existing category then it will be created.
     *
     * @param categoryName
     * @param volume
     */
    public static void setVolume(String categoryName, float volume)
    {
        categoryName= categoryName.toLowerCase();
        createSoundCategoryIfNotExist(categoryName);
        var category = soundCategories.get(categoryName);
        category.applyVolume(volume);
    }

    /**
     * Creates a new instance.
     *
     * @param supplier
     *            The sound supplier that will offer a new clip whenever this sound is played.
     */
    public Sound(SoundSupplier supplier)
    {
        this.supplier = supplier;
    }

    /**
     * Sets the volume of the sound.
     *
     * <p>
     * The given volume will be multiplied with the {@link #getMasterVolume() master volume}.
     * </p>
     *
     * @param volume
     *            A volume value between 0 (no volume) and 1 (highest volume). Values that are below 0 or above 1 will
     *            be clamped to their clostest bound, i. e. -5 becomes 0 and 14 becomes 1.
     */
    public void setVolume(float volume)
    {
        volume = NumberUtils.clamp(volume, 0, 1);
        this.volume = volume;

        float actualVolume = volume;

        if (this.supplier.getSoundCategory() != null)
        {
            actualVolume *= Sound.soundCategories.get(this.supplier.getSoundCategory()).getVolume();
        }

        actualVolume *= Sound.soundCategories.get(Sound.MASTER_CATEGORY).getVolume();

        if (this.instanceHandle != -1)
        {
            this.supplier.getAudioCue().setVolume(this.instanceHandle, actualVolume);
        }
    }

    /**
     * Gets the volume of this sound.
     *
     * @return A volume value between 0 (no volume) and 1 (highest volume).
     */
    public float getVolume()
    {
        return this.volume;
    }

    /**
     * Sets up a new clip and sets its volume by calling {@link #setVolume(float)}.
     *
     * <p>
     * If an old clip exists it will be stopped.
     * </p>
     */
    private void setupClip()
    {
        this.isPaused = false;
        stop();

        this.instanceHandle = this.supplier.getAudioCue().obtainInstance();
        setVolume(this.volume);
    }

    /**
     * Plays the sound once.
     */
    public void start()
    {
        setupClip();

        this.supplier.getAudioCue().addAudioCueListener(this);
        this.supplier.getAudioCue().start(this.instanceHandle);
        this.running = true;
    }

    /**
     * Plays the sound once.
     *
     * <p>
     * This method will not return until the sound has ended.
     * </p>
     */
    public void startAndWait()
    {
        start();

        synchronized (lock)
        {
            Exceptions.uncheck(() -> lock.wait());
        }
    }

    /**
     * Plays the sound in a continous loop.
     */
    public void loop()
    {
        loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Plays the sound <i>count + 1</i> times.
     *
     * @param count
     */
    public void loop(int count)
    {
        setupClip();

        this.supplier.getAudioCue().addAudioCueListener(this);
        this.supplier.getAudioCue().setLooping(this.instanceHandle, count);
        this.supplier.getAudioCue().start(this.instanceHandle);
        this.running = true;
    }

    /**
     * Plays the sound <i>count + 1</i> times.
     *
     * <p>
     * This method will not return until the loop has finished playing the sound <i>count + 1</i> times.
     * </p>
     */
    public void loopAndWait(int count)
    {
        setupClip();

        this.supplier.getAudioCue().addAudioCueListener(this);
        this.supplier.getAudioCue().setLooping(this.instanceHandle, count);
        this.supplier.getAudioCue().start(this.instanceHandle);
        this.running = true;

        synchronized (this.lock)
        {
            Exceptions.uncheck(() -> this.lock.wait());
        }
    }

    /**
     * Stops the current clip if one exists.
     */
    public void stop()
    {
        if (this.instanceHandle != -1)
        {
            this.isPaused = false;
            this.supplier.getAudioCue().stop(this.instanceHandle);
        }
    }

    /**
     * Fades the sound out over the given span of milliseconds.
     *
     * @param fadeTime time until completely faded in milliseconds.
     */
    public void fadeOut(long fadeTime)
    {
        fadeOut(fadeTime, false);
    }

    /**
     * Fades the sound out over the given span of milliseconds.
     *
     * This method blocks until the fading is complete.
     *
     * @param fadeTime time until completely faded in milliseconds.
     */
    public void fadeOutAndWait(long fadeTime)
    {
        fadeOut(fadeTime, true);
    }

    /**
     * Fades the sound out over the given span of milliseconds.
     *
     * @param fadeTime time until completely faded in milliseconds.
     * @param wait true if this method should block.
     */
    private void fadeOut(long fadeTime, boolean wait)
    {
        Object lock = new Object();
        int fadeTicks = 10;
        long fadeIntervall = fadeTime / fadeTicks;
        float fadeOutVolume = this.volume / fadeTicks;

        Threads.get().executeCachedDaemon(() -> {
            for (int i = 0; i < fadeTicks; i++)
            {
                if (!this.isPaused)
                {
                    setVolume(this.volume - fadeOutVolume);
                }
                else
                {
                    i--;
                }

                Exceptions.uncheck(Thread::sleep, fadeIntervall);
            }

            stop();

            if (wait)
            {
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        });

        if (wait)
        {
            synchronized (lock)
            {
                Exceptions.uncheck(() -> lock.wait());
            }
        }
    }

    /**
     * Fades the sound in over the given span of milliseconds.
     *
     * @param fadeTime time until completely faded in milliseconds.
     */
    public void fadeIn(long fadeTime)
    {
        fadeIn(fadeTime, false);
    }

    /**
     * Fades the sound in over the given span of milliseconds.
     *
     * This method blocks until the fading is complete.
     *
     * @param fadeTime time until completely faded in milliseconds.
     */
    public void fadeInAndWait(long fadeTime)
    {
        fadeIn(fadeTime, true);
    }

    /**
     * Fades the sound in over the given span of milliseconds.
     *
     * @param fadeTime time until completely faded in milliseconds.
     * @param wait true if this method should block.
     */
    private void fadeIn(long fadeTime, boolean wait)
    {
        Object lock = new Object();
        int fadeTicks = 10;
        long fadeIntervall = fadeTime / fadeTicks;
        float endVolume = this.volume;
        float fadeInVolume = this.volume / fadeTicks;
        setVolume(0);

        Threads.get().executeCachedDaemon(() -> {
            float currentVolume = 0f;

            for (int i = 0; i < fadeTicks; i++)
            {
                if (!this.isPaused)
                {
                    currentVolume += fadeInVolume;
                    setVolume(currentVolume);
                }
                else
                {
                    i--;
                }

                Exceptions.uncheck(Thread::sleep, fadeIntervall);
            }

            setVolume(endVolume);

            if (wait)
            {
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        });

        if (wait)
        {
            synchronized (lock)
            {
                Exceptions.uncheck(() -> lock.wait());
            }
        }
    }

    public void pause()
    {
        this.isPaused = true;

        if (this.running)
        {
            this.supplier.getAudioCue().stop(this.instanceHandle);
            this.running = false;
        }
    }

    public void resume()
    {
        this.isPaused = false;

        if (!this.running)
        {
            this.supplier.getAudioCue().start(this.instanceHandle);
            this.running = true;
        }
    }

    @Override
    public void onStop(int instanceHandle)
    {
        if (this.instanceHandle == instanceHandle && !this.isPaused)
        {
            synchronized (this.lock)
            {
                this.lock.notifyAll();
            }

            Sound.soundCategories.get(Sound.MASTER_CATEGORY).removeSound(this);

            if (this.supplier.getSoundCategory() != null)
            {
                Sound.soundCategories.get(this.supplier.getSoundCategory()).removeSound(this);
            }

            this.supplier.getAudioCue().releaseInstance(this.instanceHandle);
            this.supplier.getAudioCue().removeAudioCueListener(this);

            this.instanceHandle = -1;
            this.running = false;
        }
    }
}