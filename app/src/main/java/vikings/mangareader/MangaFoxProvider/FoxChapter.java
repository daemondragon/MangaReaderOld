package vikings.mangareader.MangaFoxProvider;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import vikings.mangareader.MangaProvider.Chapter;

class FoxChapter extends Chapter
{
    private String url;

    FoxChapter(String name, String url)
    {
        super(name);
        this.url = url;
    }

    public void load(@Nullable final Runnable success, @Nullable final Runnable error)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler(Looper.getMainLooper());
                if (parseChapter())
                    handler.post(success);
                else
                    handler.post(error);
            }
        }).start();
    }

    public void unload()
    {
        first_page = null;
        last_page = null;
    }

    private boolean parseChapter()
    {
        first_page = new FoxPage(url);
        return (true);
    }
}
