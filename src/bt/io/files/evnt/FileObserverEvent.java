package bt.io.files.evnt;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import bt.types.files.FileObserver;

/**
 * A {@link WatchEvent} implementation to be used with {@link FileChangeObserver}.
 *
 * @author &#8904
 */
public class FileObserverEvent implements WatchEvent<Path>
{
    protected Kind kind;
    protected int count;
    protected Path path;
    protected Path relativePath;
    protected Path observedPath;

    public FileObserverEvent(WatchEvent<Path> e, Path observerPath)
    {
        this.kind = e.kind();
        this.count = e.count();

        if (observerPath != null)
        {
            this.path = Paths.get(observerPath.toString(), e.context().toString());
        }
        else
        {
            this.path = e.context();
        }

        this.relativePath = e.context();
        this.observedPath = observerPath;
    }

    public FileObserverEvent(WatchEvent<Path> e)
    {
        this(e, null);
    }

    /**
     * @see java.nio.file.WatchEvent#kind()
     */
    @Override
    public Kind kind()
    {
        return this.kind;
    }

    /**
     * @see java.nio.file.WatchEvent#count()
     */
    @Override
    public int count()
    {
        return this.count;
    }

    /**
     * @see java.nio.file.WatchEvent#context()
     */
    @Override
    public Path context()
    {
        return this.relativePath;
    }

    /**
     * Returns the full context for the event.
     *
     * <p>
     * In the case of {@link StandardWatchEventKinds#ENTRY_CREATE ENTRY_CREATE},
     * {@link StandardWatchEventKinds#ENTRY_DELETE ENTRY_DELETE}, and {@link StandardWatchEventKinds#ENTRY_MODIFY
     * ENTRY_MODIFY} events the context is a full {@code Path} to the concerned file.
     * <p>
     *
     * @return the event context; may be {@code null}
     */
    public Path fullContext()
    {
        return this.path;
    }

    /**
     * Gets the path that has been observed by the {@link FileObserver}.
     *
     * @return
     */
    public Path observedPath()
    {
        return this.observedPath;
    }
}