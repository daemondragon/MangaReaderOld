package vikings.mangareader.MangaProvider;

import java.util.List;

/**
 * Is used to navigate or search between mangas
 */
public interface MangaProvider
{
    /**
     * Get a list of new mangas.
     * @return all new mangas, or null if an error occurred.
     */
    List<Manga> getNewMangas();

    //Search option will be added later, as well as login etc...
}
