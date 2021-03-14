package bt.io.sound;

import bt.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class VolumeCategory
{
    private String name;
    private float volume;
    private List<Sound> sounds;

    public VolumeCategory(String name)
    {
        this.name = name;
        this.volume = 1;
        this.sounds = new ArrayList<>();
    }

    public synchronized void addSound(Sound sound)
    {
        this.sounds.add(sound);
    }

    public synchronized void removeSound(Sound sound)
    {
        this.sounds.remove(sound);
    }

    public String getName()
    {
        return name;
    }

    public void applyVolume(float volume)
    {
        this.volume = NumberUtils.clamp(volume, 0, 1);

        for (var sound : this.sounds)
        {
            // apply volume change by making the sound recalculating its own volume
            sound.setVolume(sound.getVolume());
        }
    }

    public float getVolume()
    {
        return volume;
    }

    public List<Sound> getSounds()
    {
        return sounds;
    }
}