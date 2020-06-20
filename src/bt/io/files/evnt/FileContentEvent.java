package bt.io.files.evnt;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author &#8904
 *
 */
public class FileContentEvent extends FileObserverEvent
{
    private long oldSize;
    private long newSize;
    private String addedContent;

    public FileContentEvent(WatchEvent<Path> e)
    {
        super(e);
    }

    public FileContentEvent(WatchEvent<Path> e, Path observerPath)
    {
        super(e, observerPath);
    }

    /**
     * Gets the added content.
     *
     * <p>
     * This value is only accurate if the content was added to the very end of the file.
     * </p>
     *
     * @return the addedContent or null if no content was added.
     */
    public String getAddedContent()
    {
        return this.addedContent;
    }

    /**
     * Sets the added content.
     *
     * @param addedContent
     *            the addedContent to set
     */
    public void setAddedContent(String addedContent)
    {
        this.addedContent = addedContent;
    }

    /**
     * Gets the old file content size.
     *
     * @return the oldSize
     */
    public long getOldSize()
    {
        return this.oldSize;
    }

    /**
     * Sets the old file content size.
     *
     * @param oldSize
     *            the oldSize to set
     */
    public void setOldSize(long oldSize)
    {
        this.oldSize = oldSize;
    }

    /**
     * Gets the new file content size.
     *
     * @return the newSize
     */
    public long getNewSize()
    {
        return this.newSize;
    }

    /**
     * Sets the new file content size.
     *
     * @param newSize
     *            the newSize to set
     */
    public void setNewSize(long newSize)
    {
        this.newSize = newSize;
    }

}