package bt.io.sound;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

import bt.utils.log.Logger;
import bt.utils.nulls.Null;
import bt.utils.num.NumberUtils;

/**
 * A class wrapping a repeatable {@link Clip}.
 *
 * @author &#8904
 */
public class Sound
{
    private static float masterVolume = 1;
    private SoundSupplier supplier;
    private Clip clip;
    private float volume = 1;

    /**
     * Sets the master volume for all sounds.
     *
     * @param volume
     *            A volume value between 0 (no volume) and 1 (highest volume). Values that are below 0 or above 1 will
     *            be clamped to their clostest bound, i. e. -5 becomes 0 and 14 becomes 1.
     */
    public static void setMasterVolume(float volume)
    {
        volume = NumberUtils.clamp(volume,
                                   0,
                                   1);
        masterVolume = volume;
    }

    /**
     * Gets the currently set master volume for all sounds.
     *
     * @return A volume value between 0 (no volume) and 1 (highest volume).
     */
    public static float getMasterVolume()
    {
        return masterVolume;
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
        volume = NumberUtils.clamp(volume,
                                   0,
                                   1);
        this.volume = volume * masterVolume;

        if (this.clip != null)
        {
            FloatControl masterGain = (FloatControl)this.clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = masterGain.getMaximum() - masterGain.getMinimum();
            float gain = (range * volume) + masterGain.getMinimum();
            masterGain.setValue(gain);
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
        this.clip = this.supplier.getClip();
        setVolume(this.volume);
    }

    /**
     * Plays the sound once.
     */
    public void start()
    {
        setupClip();
        this.clip.start();
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
        Object lock = new Object();
        setupClip();

        this.clip.addLineListener((e) ->
        {
            if (e.getType().equals(LineEvent.Type.STOP))
            {
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        });

        this.clip.start();

        synchronized (lock)
        {
            try
            {
                lock.wait();
            }
            catch (InterruptedException e1)
            {}
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
        this.clip.loop(count);
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
        Object lock = new Object();
        setupClip();

        this.clip.addLineListener((e) ->
        {
            if (e.getType().equals(LineEvent.Type.STOP))
            {
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        });

        this.clip.loop(count);

        synchronized (lock)
        {
            try
            {
                lock.wait();
            }
            catch (InterruptedException e1)
            {
                Logger.global().print(e1);
            }
        }
    }

    /**
     * Stops the current clip if one exists.
     */
    public void stop()
    {
        Null.checkRun(this.clip, this.clip::stop);
    }

    /**
     * Indicates whether this sound is currently playing.
     *
     * @return True if this sound is currently playing.
     */
    public boolean isRunning()
    {
        return this.clip != null && this.clip.isRunning();
    }
}