package vikings.mangareader.Manga;

/**
 * Is used to handle a variable number of page.
 * Only the name is supposed to be non null before the chapter is loaded.
 */
public class Chapter
{
    public String name;
    public String release = null;

    public PageLoader first_page = null;
    public PageLoader last_page = null;

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
}
