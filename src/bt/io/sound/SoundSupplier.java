package bt.io.sound;

import bt.io.sound.philfrei.audiocue.AudioCue;
import bt.types.Killable;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

/**
 * @author &#8904
 */
public class SoundSupplier implements Killable
{
    private float volume = 1;
    private String volumeCategory;
    private AudioCue audioCue;

    /**
     * Creates a new instance and loads the audio from the given file.
     *
     * @param file
     *            The sound file that should be used.
     */
    public SoundSupplier(File file, int concurrentPlays) throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        Sound.createVolumeCategoryIfNotExist(Sound.MASTER_VOLUME);
        this.audioCue = AudioCue.makeStereoCue(file.toURI().toURL(), concurrentPlays);
        this.audioCue.open();
    }

    public void setName(String name)
    {
        this.audioCue.setName(name);
    }

    public AudioCue getAudioCue()
    {
        return this.audioCue;
    }

    public float getVolume()
    {
        return volume;
    }

    public void setVolumeCategory(String volumeCategory)
    {
        this.volumeCategory = volumeCategory.toLowerCase();
        Sound.createVolumeCategoryIfNotExist(this.volumeCategory);
    }

    public String getVolumeCategory()
    {
        return this.volumeCategory;
    }

    /**
     * Sets the volume that will be given to every created sound instance.
     *
     * @param volume
     */
    public void setVolume(float volume)
    {
        this.volume = volume;
    }

    /**
     * Gets a new {@link Sound} instance which will use this supplier.
     *
     * @return The new sound.
     */
    public Sound getSound()
    {
        Sound sound = new Sound(this);
        sound.setVolume(this.volume);
        Sound.volumeCategories.get(Sound.MASTER_VOLUME).addSound(sound);


        if (this.volumeCategory != null)
        {
            Sound.volumeCategories.get(this.volumeCategory).addSound(sound);
        }

        return sound;
    }

    @Override
    public void kill()
    {
        this.audioCue.close();
    }
}