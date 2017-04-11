package vikings.mangareader.MangaProvider;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Is used to navigate or search between mangas
 */
public interface MangaProvider extends Loadable
{
    /**
     * Get a list of mangas.
     * @return all mangas, or null if an error occurred.
     */
    List<Manga> mangas();

    /**
     * Get all supported genres used for the search function.
     * @return all genres supported.
     */
    List<String>    getAllGenres();

    /**
     * Search the manga in the given genre.
     * @param manga_name the manga name.
     * @param in_genre all desired genre. If null is provided, no genre is specified.
     * @param success will be called is the search happened correctly.
     * @param error will be called if an error occured.
     */
    void search(String manga_name, @Nullable List<String> in_genre, Runnable success, Runnable error);
}
