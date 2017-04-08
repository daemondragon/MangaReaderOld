package vikings.mangareader.MangaProvider;


/**
 * Is used to handle a variable number of page.
 */
public interface Chapter
{
    /**
     * Get the name of the chapter.
     * @return the name, or null if nothing could be found.
     */
    String name();

    /**
     * Get the release date of the chapter
     * @return the release date, or null if none is provided.
     */
    String release();

    /**
     * Get the first page of the chapter
     * @return the first page, or null is an error occurred
     */
    Page getFirstPage();
}
