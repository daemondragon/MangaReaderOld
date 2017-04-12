package vikings.mangareader.MangaProvider;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Is used to navigate or search between mangas.
 * No fields are supposed to be non null before load is called.
 */
public abstract class MangaProvider implements Loadable
{
    protected List<Manga> mangas = null;
    protected List<String> genres_supported = null;

    /**
     * Get a list of mangas.
     * @return all mangas, or null if an error occurred.
     */
    public List<Manga> mangas()
    {
        return (mangas);
    }

    /**
     * Get all supported genres used for the search function.
     * @return all genres supported.
     */
    public List<String> getAllGenres()
    {
        return (genres_supported);
    }

    /**
     * Search the manga in the given genre.
     * @param manga_name the manga name.
     * @param in_genre all desired genre. If null is provided, no genre is specified.
     * @param success will be called is the search happened correctly.
     * @param error will be called if an error occured.
     */
    public abstract void search(String manga_name, @Nullable List<String> in_genre, Runnable success, Runnable error);
}
