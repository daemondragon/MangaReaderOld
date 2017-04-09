package vikings.mangareader.MangaProvider;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * Contain only a page as well as previous and next one.
 */
public interface Page
{
    /**
     * Load the page to have all information about it.
     * @param success will be called if the loading is a success
     * @param error will be called if an error occurred.
     */
    void load(@Nullable Runnable success, @Nullable Runnable error);

    /**
     * Unload non important information to save memory space
     */
    void unload();

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
