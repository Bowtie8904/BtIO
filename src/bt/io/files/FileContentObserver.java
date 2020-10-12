package bt.io.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import bt.io.files.evnt.FileContentEvent;
import bt.io.files.evnt.FileCreateEvent;
import bt.io.files.evnt.FileDeleteEvent;
import bt.io.files.evnt.FileModifyEvent;
import bt.io.files.evnt.FileObserverEvent;
import bt.runtime.InstanceKiller;
import bt.types.Killable;
import bt.utils.Null;

/**
 * An extension of {@link FileObserver} which offers additional functions to receive events for changes in file sizes
 * plus the changed textual content of files.
 *
 * <p>
 * Determination of changed textual content only works if the content was added to the very end of the file as this
 * class does only compare file sizes and creates a substring instead of comparing the true text values.
 * </p>
 *
 * @author &#8904
 */
public class FileContentObserver extends FileObserver
{
    private Map<File, Long> fileSizes;

    /**
     * Creates a new instance.
     *
     * <p>
     * Creates a new {@link WatchService}. Adds this instance to the {@link InstanceKiller} via
     * {@link InstanceKiller#killOnShutdown(Killable)}.
     * </p>
     *
     * <p>
     * This instance will observe registered files or the files inside registered directories (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions, file modifications
     * and for increassed file content size.
     * </p>
     *
     * <p>
     * Determination of changed textual content only works if the content was added to the very end of the file as this
     * class does only compare file sizes and creates a substring instead of comparing the true text values.
     * </p>
     */
    public FileContentObserver()
    {
        super();
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * Creates a new {@link WatchService}. Adds this instance to the {@link InstanceKiller} via
     * {@link InstanceKiller#killOnShutdown(Killable)}.
     * </p>
     *
     * <p>
     * Registers the file or directory for the given path to the {@link WatchService}.
     * </p>
     *
     * <p>
     * Regular expressions can be passed to limit the files that will trigger events.
     * </p>
     *
     * <p>
     * This instance will observe the given file or the files inside the given directories (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions, file modifications
     * and for increassed file content size.
     * </p>
     *
     * <p>
     * Determination of changed textual content only works if the content was added to the very end of the file as this
     * class does only compare file sizes and creates a substring instead of comparing the true text values.
     * </p>
     *
     * @param path
     *            The path to the file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     */
    public FileContentObserver(Path path, String... regex) throws IOException
    {
        super(path, regex);
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * Creates a new {@link WatchService}. Adds this instance to the {@link InstanceKiller} via
     * {@link InstanceKiller#killOnShutdown(Killable)}.
     * </p>
     *
     * <p>
     * Registers the file or directory to the {@link WatchService}.
     * </p>
     *
     * <p>
     * Regular expressions can be passed to limit the files that will trigger events.
     * </p>
     *
     * <p>
     * This instance will observe the given file or the files inside the given directories (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions, file modifications
     * and for increassed file content size.
     * </p>
     *
     * <p>
     * Determination of changed textual content only works if the content was added to the very end of the file as this
     * class does only compare file sizes and creates a substring instead of comparing the true text values.
     * </p>
     *
     * @param file
     *            The file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     */
    public FileContentObserver(File file, String... regex) throws IOException
    {
        super(file, regex);
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * Creates a new {@link WatchService}. Adds this instance to the {@link InstanceKiller} via
     * {@link InstanceKiller#killOnShutdown(Killable)}.
     * </p>
     *
     * <p>
     * Registers the file or directory for the given path to the {@link WatchService}.
     * </p>
     *
     * <p>
     * Regular expressions can be passed to limit the files that will trigger events.
     * </p>
     *
     * <p>
     * This instance will observe the given file or the files inside the given directories (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions, file modifications
     * and for increassed file content size.
     * </p>
     *
     * <p>
     * Determination of changed textual content only works if the content was added to the very end of the file as this
     * class does only compare file sizes and creates a substring instead of comparing the true text values.
     * </p>
     *
     * @param filePath
     *            The path to the file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     */
    public FileContentObserver(String filePath, String... regex) throws IOException
    {
        super(filePath, regex);
    }

    /**
     * Subscribes to content change events fired by this instance.
     *
     * <p>
     * Events of this type are fired additionally to {@link FileModifyEvent}s for text based files whichs size
     * increased. The added text can only be correctly determined if it was added to the very end of the file, because
     * this class does only compare file sizes and creates a substring instead of comparing the true text values.
     * </p>
     *
     * <p>
     * This event is NOT fired if the file was modified but the size of the content remains the same.
     * </p>
     *
     * @param consumer
     *            The {@link Consumer} that will receive fired events.
     */
    public void subscribeContent(Consumer<FileContentEvent> consumer)
    {
        this.eventDispatcher.subscribeTo(FileContentEvent.class, consumer);
    }

    @Override
    protected void setFile(WatchKey key, File file)
    {
        super.setFile(key, file);

        if (file.isDirectory())
        {
            Stream.of(file.listFiles())
                  .filter(File::isFile)
                  .forEach(f ->
                  {
                      addFile(f);
                  });
        }
        else
        {
            addFile(file);
        }
    }

    private void addFile(File file)
    {
        long length = 0;
        try
        {
            length = Files.readString(file.toPath()).length();

        }
        catch (IOException e)
        {
            // non text based files cant be read to string
            try
            {
                length = Files.size(file.toPath());
            }
            catch (IOException e1)
            {
                e.printStackTrace();
            }
        }

        this.fileSizes = Null.nullValue(this.fileSizes, new HashMap<File, Long>());
        this.fileSizes.put(file, length);
    }

    @Override
    protected void dispatchEvent(FileObserverEvent e)
    {
        super.dispatchEvent(e);

        File file = e.fullContext().toFile();

        if (e instanceof FileCreateEvent)
        {
            addFile(file);
        }
        else if (e instanceof FileDeleteEvent)
        {
            this.fileSizes.remove(file);
        }
        else if (e instanceof FileModifyEvent)
        {
            String content = null;
            long nonTextLength = 0;

            try
            {
                content = Files.readString(e.fullContext());
            }
            catch (IOException e1)
            {
                // non text based files cant be read to string
                try
                {
                    nonTextLength = Files.size(e.fullContext());
                }
                catch (IOException e2)
                {
                    e1.printStackTrace();
                    e2.printStackTrace();
                }
            }

            long oldSize = this.fileSizes.get(file);
            long newSize = content != null ? content.length() : nonTextLength;
            this.fileSizes.put(file, newSize);

            if (oldSize != newSize)
            {
                var event = new FileContentEvent(e, e.observedPath());
                event.setOldSize(oldSize);
                event.setNewSize(newSize);

                if (content != null && newSize > oldSize)
                {
                    event.setAddedContent(content.substring(Math.toIntExact(oldSize)));
                }

                this.eventDispatcher.dispatch(event);
            }
        }
    }
}