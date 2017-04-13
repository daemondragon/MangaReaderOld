package vikings.mangareader.MangaProvider;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Contain all information about the manga.
 * Only the name is supposed to be non null before load is called.
 */
public abstract class Manga implements Loadable
{
    protected String name;
    protected String authors = null;
    protected String summary = null;
    protected String status = null;

    protected float rating = 0.f;
    protected Drawable cover = null;

    protected List<String> genres = null;
    protected List<Chapter> chapters = null;

    /**
     * Create the manga with the given name, since it's is the only thing
     * that is supposed to be existing before load.
     * @param name the name of the manga.
     */
    public Manga(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the manga.
     * @return the name, or null if none is found.
     */
    public String name()
    {
        return (name);
    }

    /**
     * Get the authors of the manga.
     * @return the authors, or null if none is found.
     */
    public String authors()
    {
        return (authors);
    }

    /**
     * Get the summary of the manga.
     * @return the summary, or null if none is provided.
     */
    public String summary()
    {
        return (summary);
    }

    /**
     * Get the status of the manga.
     * @return the status or null if none is provided.
     */
    public String status()
    {
        return (status);
    }

    /**
     * Get the rating of the manga, between 0 (worst mark) and 1 (best mark).
     * @return the rating of the manga.
     */
    public float rating()
    {
        return (rating);
    }

    /**
     * Get a list of all genres.
     * @return a list of genre. An empty list is returned if no genre is provided.
     */
    public List<String> genres()
    {
        return (genres);
    }

    /**
     * Get the cover of the manga.
     * @return the cover, or null if none is found.
     */
    public Drawable cover()
    {
        return (cover);
    }

    /**
     * Get all chapters of the manga. Only important data might be loaded at this time, so
     * some data might be missing. The chapters must be ordered by release: the most recent one
     * has to be the first in the list.
     * @return a list of all chapters. If an error occurred, null is returned. If the current manga doesn't have any chapter
     * an empty list is returned.
     */
    public List<Chapter> chapters()
    {
        return (chapters);
    }
}
