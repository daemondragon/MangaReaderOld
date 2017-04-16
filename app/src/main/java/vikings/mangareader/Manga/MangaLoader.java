package vikings.mangareader.Manga;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class MangaLoader extends AsyncTaskLoader<Manga>
{
    private String name;

    public MangaLoader(Context context, String name)
    {
        super(context);
        this.name = name;
    }

    public String name()
    {
        return (name);
    }
}
