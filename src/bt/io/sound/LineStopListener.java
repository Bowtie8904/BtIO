package bt.io.sound;

import bt.io.sound.philfrei.audiocue.AudioCue;
import bt.io.sound.philfrei.audiocue.AudioCueInstanceEvent;
import bt.io.sound.philfrei.audiocue.AudioCueListener;

public interface LineStopListener extends AudioCueListener
{
    @Override
    public default void audioCueOpened(long now, int threadPriority, int bufferSize, AudioCue source)
    {

    }

    @Override
    public default void audioCueClosed(long now, AudioCue source)
    {

    }

    @Override
    public default void instanceEventOccurred(AudioCueInstanceEvent event)
    {
        if (event.type.equals(AudioCueInstanceEvent.Type.STOP_INSTANCE))
        {
            onStop(event.instanceID);
        }
    }

    public void onStop(int instanceHandle);
}