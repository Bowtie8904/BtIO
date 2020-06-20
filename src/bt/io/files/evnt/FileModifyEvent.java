package bt.io.files.evnt;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author &#8904
 *
 */
public class FileModifyEvent extends FileObserverEvent
{
    public FileModifyEvent(WatchEvent<Path> e)
    {
        super(e);
    }

    public FileModifyEvent(WatchEvent<Path> e, Path observerPath)
    {
        super(e, observerPath);
    }
}