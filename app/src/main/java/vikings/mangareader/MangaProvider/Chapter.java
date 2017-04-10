package vikings.mangareader.MangaProvider;

/**
 * Is used to handle a variable number of page.
 */
public interface Chapter extends Loadable
{
    /**
     * Get the name of the chapter.
     * @return the name, or null if nothing could be found.
     */
    String name();

    /**
     * Get the release date of the chapter.
     * @return the release date, or null if none is provided.
     */
    String release();

    /**
     * Get the first page of the chapter.
     * @return the first page, or null if an error occurred.
     */
    Page getFirstPage();

    /**
     * Get the last page of the chapter.
     * @return the last page, or null if an error occurred.
     */
    Page getLastPage();

    /**
     * Get the next chapter.
     * @return the next chapter, or null if none is provided.
     */
    Chapter getNextChapter();

    /**
     * Get the previous chapter.
     * @return the previous chapter, or null if none is provided.
     */
    Chapter getPreviousChapter();
}
