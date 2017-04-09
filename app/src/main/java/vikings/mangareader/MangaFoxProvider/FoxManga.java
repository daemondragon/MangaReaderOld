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

public class FoxManga implements Manga
{
    private String url = null;
    private String name = null;
    private String authors = null;
    private String summary = null;
    private Drawable cover = null;
    private ArrayList<Chapter> chapters = new ArrayList<>();

    public FoxManga(String name, String url)
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
                if (parseMangaInfo(Utils.InputStreamToString(Utils.getInputStreamFromURL("http://mangafox.me/releases/"))))
                    handler.post(success);
                else
                    handler.post(error);
            }
        }).start();
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

        summary = Utils.fromHtmlString(Utils.parse(html, "<p class=\"summary\">", ">", "</p>"));
        authors = Utils.parse(html, "href=\"/search/artist/", ">", "<");

        return (parseCover(html) && parseChapters(html));
    }

    private boolean parseCover(String html)
    {
        String cover_url = Utils.parse(html, "<meta property=\"og:image\"", "content=\"", "\"");
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
        return (true);
    }
}
