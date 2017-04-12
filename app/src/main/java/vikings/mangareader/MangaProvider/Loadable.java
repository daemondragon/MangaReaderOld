package vikings.mangareader.MangaProvider;

/**
 * Used to save memory
 */
interface Loadable
{
    /**
     * Load the page to have all information about it.
     * @param success will be called if the loading is a success
     * @param error will be called if an error occurred.
     */
    void load(Runnable success, Runnable error);

    /**
     * Unload non important information to save memory space
     */
    void unload();
}
