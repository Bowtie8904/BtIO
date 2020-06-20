package bt.io.files.evnt;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author &#8904
 *
 */
public class FileDeleteEvent extends FileObserverEvent
{
    public FileDeleteEvent(WatchEvent<Path> e)
    {
        super(e);
    }

    public FileDeleteEvent(WatchEvent<Path> e, Path observerPath)
    {
        super(e, observerPath);
    }
}