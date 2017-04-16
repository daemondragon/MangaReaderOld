package vikings.mangareader.Manga;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Contain all information about the manga.
 * Only the name is supposed to be non null before the manga is loaded.
 */
public class Manga
{
    public String name;
    public String authors = null;
    public String summary = null;
    public String status = null;

    public float rating = 0.f;
    public Drawable cover = null;

    public List<String> genres = null;
    public List<ChapterLoader> chapters = null;

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
}
