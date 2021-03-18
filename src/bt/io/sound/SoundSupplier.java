package bt.io.sound;

import bt.io.sound.philfrei.audiocue.AudioCue;
import bt.types.Killable;
import bt.utils.StringID;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
     * @param concurrentPlays
     *             The number of sounds from this supplier that can be played concurrently.
     */
    public SoundSupplier(File file, int concurrentPlays) throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        this(file.toURI().toURL(), concurrentPlays);
    }

    /**
     * Creates a new instance and loads the audio from the given URL.
     *
     * @param url
     *            The sound url that should be used.
     * @param concurrentPlays
     *            The number of sounds from this supplier that can be played concurrently.
     */
    public SoundSupplier(URL url, int concurrentPlays) throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        Sound.createVolumeCategoryIfNotExist(Sound.MASTER_VOLUME);
        this.audioCue = AudioCue.makeStereoCue(url, concurrentPlays);
        this.audioCue.open();
    }

    /**
     * Creates a new instance and loads the audio from the given stream.
     *
     * @param ais
     *            The sound stream that should be used.
     * @param concurrentPlays
     *            The number of sounds from this supplier that can be played concurrently.
     */
    public SoundSupplier(AudioInputStream ais, int concurrentPlays) throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        this(ais, StringID.uniqueID(), concurrentPlays);
    }

    /**
     * Creates a new instance and loads the audio from the given stream.
     *
     * @param ais
     *            The sound stream that should be used.
     * @param name
     *            The name that will be used for the underlying AduiCue instance.
     * @param concurrentPlays
     *            The number of sounds from this supplier that can be played concurrently.
     */
    public SoundSupplier(AudioInputStream ais, String name, int concurrentPlays) throws IOException, UnsupportedAudioFileException, LineUnavailableException
    {
        Sound.createVolumeCategoryIfNotExist(Sound.MASTER_VOLUME);
        this.audioCue = AudioCue.makeStereoCue(ais, StringID.uniqueID(), concurrentPlays);
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

    /**
     * Sets the volume category of this sound.
     *
     * Each sound will be affected by the master volume category by default. A category set via this
     * method will be taken into account additionally.
     *
     * @param volumeCategory
     */
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