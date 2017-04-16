package vikings.mangareader.Manga;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

//just to be consistent
public abstract class PageLoader extends AsyncTaskLoader<Page>
{
    public PageLoader(Context context)
    {
        super(context);
    }
}
