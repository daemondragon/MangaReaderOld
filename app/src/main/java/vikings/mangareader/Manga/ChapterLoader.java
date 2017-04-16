package vikings.mangareader.Manga;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class ChapterLoader extends AsyncTaskLoader<Chapter>
{
    private String name;

    public ChapterLoader(Context context, String name)
    {
        super(context);
        this.name = name;
    }

    public String name()
    {
        return (name);
    }
}
