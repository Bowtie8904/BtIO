package bt.io.sound;

import javax.sound.sampled.Clip;

import bt.scheduler.Threads;
import bt.types.number.MutableInt;
import bt.utils.Exceptions;
import bt.utils.NumberUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author &#8904
 */
public class Sound implements LineStopListener
{
    public static final String MASTER_VOLUME = "master";
    protected static Map<String, VolumeCategory> volumeCategories = new ConcurrentHashMap<>();
    private SoundSupplier supplier;
    private float volume = 1;
    private int instanceHandle = -1;
    private boolean running = false;
    private Object lock = new Object();

    public static synchronized void createVolumeCategoryIfNotExist(String name)
    {
        name = name.toLowerCase();
        var found = volumeCategories.get(name);

        if (found == null)
        {
            VolumeCategory category = new VolumeCategory(name);
            volumeCategories.put(name, category);
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
        setVolume(Sound.MASTER_VOLUME, volume);
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
        createVolumeCategoryIfNotExist(categoryName);
        var category = volumeCategories.get(categoryName);
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

        if (this.supplier.getVolumeCategory() != null)
        {
            actualVolume *= Sound.volumeCategories.get(this.supplier.getVolumeCategory()).getVolume();
        }

        actualVolume *= Sound.volumeCategories.get(Sound.MASTER_VOLUME).getVolume();

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
        setupClip();

        this.supplier.getAudioCue().addAudioCueListener(this);
        this.supplier.getAudioCue().start(this.instanceHandle);
        this.running = true;

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
            this.supplier.getAudioCue().stop(this.instanceHandle);
        }
    }

    /**
     * Fades the sound out over the given span of milliseconds.
     *
     * @param fadeTime
     */
    public void fadeOut(long fadeTime)
    {
        fadeOut(fadeTime, false);
    }

    public void fadeOutAndWait(long fadeTime)
    {
        fadeOut(fadeTime, true);
    }

    private void fadeOut(long fadeTime, boolean wait)
    {
        Object lock = new Object();
        int fadeTicks = 10;
        long fadeIntervall = fadeTime  / fadeTicks;
        MutableInt count = new MutableInt(0);
        float fadeOutVolume = this.volume / fadeTicks;

        Threads.get().executeCachedDaemon(() -> {
            for (int i = 0; i < fadeTicks; i++)
            {
                setVolume(this.volume - fadeOutVolume);
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

    @Override
    public void onStop(int instanceHandle)
    {
        if (this.instanceHandle == instanceHandle)
        {
            synchronized (this.lock)
            {
                this.lock.notifyAll();
            }

            Sound.volumeCategories.get(Sound.MASTER_VOLUME).removeSound(this);

            if (this.supplier.getVolumeCategory() != null)
            {
                Sound.volumeCategories.get(this.supplier.getVolumeCategory()).removeSound(this);
            }

            this.supplier.getAudioCue().releaseInstance(this.instanceHandle);
            this.supplier.getAudioCue().removeAudioCueListener(this);

            this.instanceHandle = -1;
            this.running = false;
        }
    }
}