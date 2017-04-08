package vikings.mangareader.MangaProvider;

import android.graphics.drawable.Drawable;

/**
 * Contain only a page as well as previous and next one.
 */
interface Page
{
    /**
     * Check if page has a previous one
     * @return true if there is a previous page, false otherwise
     */
    boolean hasPrevious();

    /**
     * Get the next previous, hasPrevious() might not be called before this function.
     * @return the previous page if it could be loaded, null otherwise
     */
    Page previous();

    /**
     * Check if page has a next one
     * @return true if there is a next page, false otherwise
     */
    boolean hasNext();

    /**
     * Get the next page, hasNext() might not be called before this function.
     * @return the next page if it could be loaded, null otherwise
     */
    Page next();

    /**
     * Get the picture of the page.
     * @return the picture of the page, or null if it couldn't be loaded.
     */
    Drawable getPicture();
}
