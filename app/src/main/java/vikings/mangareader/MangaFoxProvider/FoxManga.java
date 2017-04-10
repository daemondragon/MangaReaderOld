package vikings.mangareader.MangaFoxProvider;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.MangaProvider.Chapter;
import vikings.mangareader.MangaProvider.Manga;

class FoxManga implements Manga
{
    private String url = null;
    private String name = null;
    private String authors = null;
    private String summary = null;
    private static Drawable cover = null;
    private List<String> genres = new ArrayList<>();
    private ArrayList<Chapter> chapters = new ArrayList<>();

    private boolean loaded = false;

    FoxManga(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    public void load(final @Nullable Runnable success, final @Nullable Runnable error)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler(Looper.getMainLooper());
                if (parseMangaInfo(Utils.InputStreamToString(Utils.getInputStreamFromURL(url))))
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            loaded = true;
                            success.run();
                        }
                    });
                else
                    handler.post(error);
            }
        }).start();
    }

    public void unload()
    {
        chapters.clear();
        summary = null;
        cover = null;

        loaded = false;
    }

    public boolean isLoaded()
    {
        return (loaded);
    }

    public String name()
    {
        return (name);
    }

    public String authors()
    {
        return (authors);
    }

    public String summary()
    {
        return (summary);
    }

    public List<String> genres() { return (genres); }

    public Drawable cover()
    {
        return (cover);
    }

    public List<Chapter> chapters()
    {
        return (chapters);
    }

    private boolean parseMangaInfo(String html)
    {
        if (html == null || "".equals(html))
            return (false);

        summary = Utils.fromHtmlString(Utils.parseUnique(html, "<p class=\"summary\">", ">", "</p>"));
        authors = Utils.parseUnique(html, "href=\"/search/artist/", ">", "<");
        genres = Utils.parseMultiple(html, "<a href=\"http://mangafox.me/search/genres/", ">", "<");

        return (parseCover(html) && parseChapters(html));
    }

    private boolean parseCover(String html)
    {
        String cover_url = Utils.parseUnique(html, "<meta property=\"og:image\"", "content=\"", "\"");
        if (!"".equals(cover_url))
        {
            InputStream in = Utils.getInputStreamFromURL(cover_url);
            if (in != null)
            {
                cover = Drawable.createFromStream(in, "cover");
                return (true);
            }
            else
                Log.d("parseCover", "can't get input stream from url: " + cover_url);
        }
        else
            Log.d("parseCover", "url not found");

        return (false);
    }

    private boolean parseChapters(String html)
    {
        //<h3> are not used because some manga also use <h4>
        for (String str : Utils.parseMultiple(html, "<a class=\"edit\" href=\"", "<h", "</h"))
        {
            String url = Utils.parseUnique(str, "<a href=\"", "\"", "\"");
            String name = Utils.parseUnique(str, "<span class=\"title nowrap\">", ">", "<");
            if (name == null || "".equals(name))
                name = Utils.parseUnique(str, "<a href=\"", ">", "<");
            chapters.add(new FoxChapter(name, url));
        }
        //Don't forget to add this line or bad thing will happened (meteorite and laser T-Rex etc..)
        linkChaptersTogether();

        return (true);
    }

    private void linkChaptersTogether()
    {
        for (int i = 0; i < chapters.size(); ++i)
        {
            if (i != 0)
                ((FoxChapter)chapters.get(i)).next_chapter = chapters.get(i - 1);
            if (i + 1 != chapters.size())
                ((FoxChapter)chapters.get(i)).previous_chapter = chapters.get(i + 1);
        }
    }
}
