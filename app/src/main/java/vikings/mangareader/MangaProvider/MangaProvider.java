package vikings.mangareader.MangaProvider;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Is used to navigate or search between mangas
 */
public interface MangaProvider
{
    /**
     * Load the manga provider to have all starting information.
     * @param success will be called if the loading is a success
     * @param error will be called if an error occurred.
     */
    void load(@Nullable Runnable success, @Nullable Runnable error);

    /**
     * Get a list of new mangas.
     * @return all new mangas, or null if an error occurred.
     */
    List<Manga> getNewMangas();

    //Search option will be added later, as well as login etc...
}
