package vikings.mangareader.MangaFoxProvider;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import vikings.mangareader.MangaProvider.Chapter;
import vikings.mangareader.MangaProvider.Page;

class FoxChapter implements Chapter
{
    private String url;
    private String name;
    private String release;

    private Page first_page = null;
    private Page last_page = null;

    Chapter previous_chapter = null;
    Chapter next_chapter = null;

    FoxChapter(String name, String url)
    {
        this.name = name;
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

    public String name()
    {
        return (name);
    }

    public String release()
    {
        return (release);
    }

    public Page getFirstPage()
    {
        return (first_page);
    }

    public Page getLastPage()
    {
        return (last_page);
    }

    public Chapter getNextChapter()
    {
        return (next_chapter);
    }

    public Chapter getPreviousChapter()
    {
        return (previous_chapter);
    }
}
