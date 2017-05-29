package vikings.mangareader.Manga;

import android.content.Intent;

/**
 * All children class will load and unload Type.
 * @param <Type> which type have to be loaded.
 */
public abstract class Loader<Type>
{
    public String name;

    /**
     * Create a loader without name.
     */
    public Loader()
    {
        this(null);
    }

    /**
     * Create a loader with a given name.
     * @param name the loader's name
     */
    public Loader(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the chapter.
     * @return the name of the chapter.
     */
    public String name()
    {
        return (name);
    }

    /**
     * Load the Type. Must be overwritten.
     * @return the loaded Type.
     */
    public abstract Type load();
}
