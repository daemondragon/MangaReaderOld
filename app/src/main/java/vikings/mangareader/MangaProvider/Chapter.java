package vikings.mangareader.MangaProvider;

/**
 * Is used to handle a variable number of page.
 * Only the name is supposed to be non null before load is called.
 */
public abstract class Chapter implements Loadable
{
    protected String name;
    protected String release = null;

    protected Page first_page = null;
    protected Page last_page = null;

    protected Chapter previous = null;
    protected Chapter next = null;

    /**
     * Create the chapter with the given name, since it's is the only thing
     * that is supposed to be existing before load.
     * @param name the name of the chapter.
     */
    public Chapter(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the chapter.
     * @return the name, or null if nothing could be found.
     */
    public String name()
    {
        return (name);
    }

    /**
     * Get the release date of the chapter.
     * @return the release date, or null if none is provided.
     */
    public String release()
    {
        return (release);
    }

    /**
     * Get the first page of the chapter.
     * @return the first page, or null if an error occurred.
     */
    public Page getFirstPage()
    {
        return (first_page);
    }

    /**
     * Get the last page of the chapter.
     * @return the last page, or null if an error occurred.
     */
    public Page getLastPage()
    {
        return (last_page);
    }

    /**
     * Get the next chapter.
     * @return the next chapter, or null if none is provided.
     */
    public Chapter getNextChapter()
    {
        return (next);
    }

    /**
     * Get the previous chapter.
     * @return the previous chapter, or null if none is provided.
     */
    public Chapter getPreviousChapter()
    {
        return (previous);
    }
}
