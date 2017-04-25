package vikings.mangareader.Manga;

import android.graphics.drawable.Drawable;

/**
 * Contain only a page as well as previous and next one.
 * No field are supposed to be non null before the page is loaded.
 */
public class Page
{
    public Loader<Page> previous = null;
    public Loader<Page> next = null;
    public Drawable picture = null;
    /**
     * Check if page has a previous one
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPrevious()
    {
        return (previous != null);
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
     * Get the picture of the page.
     * @return the picture of the page, or null if it couldn't be loaded.
     */
    public Drawable getPicture()
    {
        return (picture);
    }
}
