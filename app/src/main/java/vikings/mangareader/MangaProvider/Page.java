package vikings.mangareader.MangaProvider;

import android.graphics.drawable.Drawable;

/**
 * Contain only a page as well as previous and next one.
 * No field are supposed to be non null before load is called.
 */
public abstract class Page implements Loadable
{
    protected Page previous = null;
    protected Page next = null;
    protected Drawable picture = null;
    /**
     * Check if page has a previous one
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPrevious()
    {
        return (previous != null);
    }

    /**
     * Get the next previous, hasPrevious() might not be called before this function.
     * @return the previous page if it could be loaded, null otherwise
     */
    public Page previous()
    {
        return (previous);
    }

    /**
     * Check if page has a next one
     * @return true if there is a next page, false otherwise
     */
    public boolean hasNext()
    {
        return (next != null);
    }

    /**
     * Get the next page, hasNext() might not be called before this function.
     * @return the next page if it could be loaded, null otherwise
     */
    public Page next()
    {
        return (next);
    }

    /**
     * Get the picture of the page.
     * @return the picture of the page, or null if it couldn't be loaded.
     */
    public Drawable getPicture()
    {
        return (picture);
    }
}
