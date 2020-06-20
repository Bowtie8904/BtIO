package bt.io.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.runtime.evnt.Dispatcher;
import bt.types.files.evnt.FileCreateEvent;
import bt.types.files.evnt.FileDeleteEvent;
import bt.types.files.evnt.FileModifyEvent;
import bt.types.files.evnt.FileObserverEvent;
import bt.utils.log.Logger;
import bt.utils.nulls.Null;
import bt.utils.thread.Threads;

/**
 * A simple wrapper for a {@link WatchService} to allow listening to single files, subscribe to specific events specify
 * regex patterns for files that should trigger an event.
 *
 * @author &#8904
 * @param <T>
 */
public class FileObserver implements Killable
{
    protected WatchService watchService;
    protected boolean observe;
    protected long pollingDelay = 0;
    protected Map<WatchKey, Path> paths;
    protected Map<WatchKey, Pattern[]> filters;
    protected Map<WatchKey, File> observedFiles;
    protected Dispatcher eventDispatcher;

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
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     */
    public FileObserver()
    {
        try
        {
            this.watchService = FileSystems.getDefault().newWatchService();
            InstanceKiller.killOnShutdown(this);
        }
        catch (IOException e)
        {
            Logger.global().print(e);
        }

        this.eventDispatcher = new Dispatcher();
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
     * This instance will observe the given file or the files inside the given directory (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     *
     * @param path
     *            The path to the file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     */
    public FileObserver(Path path, String... regex) throws IOException
    {
        this();
        register(path, regex);
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
     * This instance will observe the given file or the files inside the given directory (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     *
     * @param file
     *            The file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     */
    public FileObserver(File file, String... regex) throws IOException
    {
        this();
        register(file, regex);
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
     * This instance will observe the given file or the files inside the given directory (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     *
     * @param filePath
     *            The path to the file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     */
    public FileObserver(String filePath, String... regex) throws IOException
    {
        this();
        register(filePath, regex);
    }

    /**
     * Sets the delay for each iteration before the events of a registered path are polled.
     *
     * <p>
     * A delay can prevent the firing of multiple events in short succession. Usually 2 events would be fired for the
     * content change of a file, one for the content and one for the timestamp. A delay will cause these events to be
     * combined into one. {@link FileObserverEvent#count()} will tell whether it is a combined event (count > 1) or not.
     * </p>
     *
     * @param millis
     *            The delay in milliseconds.
     */
    public void setPollingDelay(long millis)
    {
        this.pollingDelay = millis;
    }

    protected void setFile(WatchKey key, File file)
    {
        if (!file.isDirectory())
        {
            this.observedFiles = Null.nullValue(this.observedFiles, new HashMap<WatchKey, File>());
            this.observedFiles.put(key, file);
        }
    }

    /**
     * Registers the file or directory for the given path to the {@link WatchService}.
     *
     * <p>
     * Regular expressions can be passed to limit the files that will trigger events.
     * </p>
     *
     * <p>
     * This instance will observe the given file or the files inside the given directory (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     *
     * @param path
     *            The path to the file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     * @return The {@link WatchKey} for the registered path.
     * @throws IOException
     */
    public WatchKey register(Path path, String... regex) throws IOException
    {
        // cant register if watchservice creation failed
        if (this.watchService == null)
        {
            throw new NullPointerException("No WatchService was created.");
        }

        // determining directory path to observe
        // watchservices can only observe directories, not files
        Path observePath = null;
        File pathFile = path.toFile();

        if (pathFile.isDirectory())
        {
            observePath = path;
        }
        else
        {
            observePath = pathFile.getParentFile().toPath();
        }

        // register directory to observe
        WatchKey key = observePath.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

        this.paths = Null.nullValue(this.paths, new HashMap<WatchKey, Path>());
        this.paths.put(key, observePath);

        // save single file if the desired observe object is not a directory
        setFile(key, pathFile);

        // compile regex patterns
        if (regex != null && regex.length != 0)
        {
            this.filters = Null.nullValue(this.filters, new HashMap<WatchKey, Pattern[]>());
            this.filters.put(key, Stream.of(regex)
                                        .map(Pattern::compile)
                                        .toArray(Pattern[]::new));
        }

        return key;
    }

    /**
     * Registers the file or directory to the {@link WatchService}.
     *
     * <p>
     * Regular expressions can be passed to limit the files that will trigger events.
     * </p>
     *
     * <p>
     * This instance will observe the given file or the files inside the given directory (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     *
     * @param file
     *            The file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     * @return The {@link WatchKey} for the registered path.
     * @throws IOException
     */
    public WatchKey register(File file, String... regex) throws IOException
    {
        return register(Path.of(file.getAbsolutePath()), regex);
    }

    /**
     * Registers the file or directory for the given path to the {@link WatchService}.
     *
     * <p>
     * Regular expressions can be passed to limit the files that will trigger events.
     * </p>
     *
     * <p>
     * This instance will observe the given file or the files inside the given directory (contents inside
     * sub-directories are not observed). Events will be fired for file creations, file deletions and file
     * modifications.
     * </p>
     *
     * @param filePath
     *            The path to the file or directory that should be observed.
     * @param regex
     *            An array of regular expressions of which at least one has to match the simple file name + file
     *            extension ('example.txt') so that an event will be fired for that file.
     * @return The {@link WatchKey} for the registered path.
     * @throws IOException
     */
    public WatchKey register(String filePath, String... regex) throws IOException
    {
        return register(Path.of(filePath), regex);
    }

    /**
     * Starts observing the registered paths.
     *
     * <p>
     * This method blocks until {@link #kill()} is called.
     * </p>
     */
    public void observeBlocking()
    {
        this.observe = true;

        while (this.observe)
        {
            try
            {
                WatchKey key;
                while ((key = this.watchService.take()) != null)
                {
                    if (this.pollingDelay > 0)
                    {
                        Thread.sleep(this.pollingDelay);
                    }

                    for (WatchEvent event : key.pollEvents())
                    {
                        boolean dispatch = true;
                        File concernedFile = resolveFile(event, this.paths.get(key));

                        // check if the file matches any of the files that should be observed
                        if (this.observedFiles != null)
                        {
                            File keyFile = this.observedFiles.get(key);

                            if (keyFile != null)
                            {
                                if (!keyFile.getAbsolutePath().equals(concernedFile.getAbsolutePath()))
                                {
                                    dispatch = false;
                                }
                            }
                        }

                        // checks if the file name matches at least one of the defined regular expressions
                        if (this.filters != null && dispatch)
                        {
                            Pattern[] regexArr = this.filters.get(key);

                            if (regexArr != null)
                            {
                                String fileName = concernedFile.getName();

                                for (Pattern regex : regexArr)
                                {
                                    Matcher matcher = regex.matcher(fileName);

                                    if (matcher.matches())
                                    {
                                        dispatch = true;
                                        break;
                                    }
                                    else
                                    {
                                        dispatch = false;
                                    }
                                }
                            }
                        }

                        // dispatches an event based on the observed change
                        if (dispatch)
                        {
                            if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
                            {
                                dispatchEvent(new FileCreateEvent(event, this.paths.get(key)));
                            }
                            else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))
                            {
                                dispatchEvent(new FileDeleteEvent(event, this.paths.get(key)));
                            }
                            else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
                            {
                                dispatchEvent(new FileModifyEvent(event, this.paths.get(key)));
                            }
                        }
                    }

                    key.reset();
                }
            }
            catch (InterruptedException | ClosedWatchServiceException e)
            {
                this.observe = false;
            }
        }
    }

    private File resolveFile(WatchEvent e, Path observedPath)
    {
        return Paths.get(observedPath.toString(), e.context().toString()).toFile();
    }

    /**
     * Starts observing the registered paths.
     *
     * <p>
     * This method calls {@link #observeBlocking()} within a new non-deamon thread.
     * </p>
     */
    public void observeNonBlocking()
    {
        Threads.get().execute(this::observeBlocking);
    }

    /**
     * Causes this instance to stop observing registered paths.
     *
     * <p>
     * The created {@link WatchService} insttance will be closed.
     * </p>
     */
    @Override
    public void kill()
    {
        Logger.global().print("Killing FileObserver.");
        this.observe = false;
        Null.checkClose(this.watchService);
    }

    /**
     * Subscribes to ALL events fired by this instance.
     *
     * @param consumer
     *            The {@link Consumer} that will receive fired events.
     */
    public void subscribe(Consumer<FileObserverEvent> consumer)
    {
        this.eventDispatcher.subscribeTo(FileObserverEvent.class, consumer);
    }

    /**
     * Subscribes to create events fired by this instance.
     *
     * @param consumer
     *            The {@link Consumer} that will receive fired events.
     */
    public void subscribeCreate(Consumer<FileCreateEvent> consumer)
    {
        this.eventDispatcher.subscribeTo(FileCreateEvent.class, consumer);
    }

    /**
     * Subscribes to delete events fired by this instance.
     *
     * @param consumer
     *            The {@link Consumer} that will receive fired events.
     */
    public void subscribeDelete(Consumer<FileDeleteEvent> consumer)
    {
        this.eventDispatcher.subscribeTo(FileDeleteEvent.class, consumer);
    }

    /**
     * Subscribes to modify events fired by this instance.
     *
     * @param consumer
     *            The {@link Consumer} that will receive fired events.
     */
    public void subscribeModify(Consumer<FileModifyEvent> consumer)
    {
        this.eventDispatcher.subscribeTo(FileModifyEvent.class, consumer);
    }

    /**
     * Dispatches the given event to subscribed consumers.
     *
     * @param e
     *            The event to dispatch.
     */
    protected void dispatchEvent(FileObserverEvent e)
    {
        this.eventDispatcher.dispatch(e);
    }
}