package vikings.mangareader.MangaProvider;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Contain all information about the manga.
 */
public interface Manga extends Loadable
{
    /**
     * Get the name of the manga.
     * @return the name, or null if none is found.
     */
    String name();

    /**
     * Get the authors of the manga.
     * @return the authors, or null if none is found.
     */
    String authors();

    /**
     * Get the summary of the manga.
     * @return the summary, or null if none is provided.
     */
    String summary();

    /**
     * Get the status of the manga.
     * @return the status or null if none is provided.
     */
    String status();

    /**
     * Get the rating of the manga, between 0 (worst mark) and 1 (best mark)
     * @return
     */
    float rating();

    /**
     * Get a list of all genres.
     * @return a list of genre. An empty list is returned if no genre is provided.
     */
    List<String> genres();

    /**
     * Get the cover of the manga.
     * @return the cover, or null if none is found.
     */
    Drawable cover();

    /**
     * Get all chapters of the manga. Only important data might be loaded at this time, so
     * some data might be missing.
     * @return a list of all chapters. If an error occurred, null is returned. If the current manga doesn't have any chapter
     * an empty list is returned.
     */
    List<Chapter> chapters();
}
