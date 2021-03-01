package bt.io.sound;

import bt.utils.Exceptions;

import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.*;

/**
 * An audio data holder that supplies {@link Clips}s without having to reload any resources.
 *
 * @author &#8904
 */
public class SoundSupplier
{
    private AudioFormat af;
    private int size;
    private byte[] audio;
    private DataLine.Info info;

    /**
     * Creates a new instance and loads the audio from the given file.
     *
     * @param file
     *            The sound file that should be used.
     */
    public SoundSupplier(File file)
    {
        try
        {
            setSoundData(AudioSystem.getAudioInputStream(file));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new instance and loads the audio from the given file.
     *
     * @param stream
     *            The stream of the sound file that should be used.
     */
    public SoundSupplier(InputStream stream)
    {
        try
        {
            setSoundData(AudioSystem.getAudioInputStream(stream));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public SoundSupplier(AudioInputStream audioInputStream)
    {
        setSoundData(audioInputStream);
    }

    private void setSoundData(AudioInputStream audioInputStream)
    {
        try
        {
            this.af = audioInputStream.getFormat();
            this.size = (int)(this.af.getFrameSize() * audioInputStream.getFrameLength());
            this.audio = new byte[this.size];
            this.info = new DataLine.Info(Clip.class,
                                          this.af,
                                          this.size);
            audioInputStream.read(this.audio,
                                  0,
                                  this.size);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Exceptions.uncheck(() -> audioInputStream.close());
        }
    }

    /**
     * Gets a new {@link Sound} instance which will use this supplier.
     *
     * @return The new sound.
     */
    public Sound getSound()
    {
        return new Sound(this);
    }

    /**
     * Creates a new {@link Clip} from the contained audio data.
     *
     * <p>
     * The clip will be configured to automatically close its resources once its stopped.
     * </p>
     *
     * @return The clip.
     */
    public Clip getClip()
    {
        Clip clip = null;
        try
        {
            clip = (Clip)AudioSystem.getLine(this.info);
            clip.open(this.af,
                      this.audio,
                      0,
                      this.size);
            clip.addLineListener((e) ->
            {
                if (e.getType().equals(LineEvent.Type.STOP))
                {
                    Line soundClip = e.getLine();
                    soundClip.close();
                }
            });
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
        }

        return clip;
    }
}