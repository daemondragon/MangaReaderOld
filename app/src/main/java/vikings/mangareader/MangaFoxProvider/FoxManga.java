package vikings.mangareader.MangaFoxProvider;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

import vikings.mangareader.MangaProvider.Manga;

class FoxManga extends Manga
{
    private String url = null;

    FoxManga(String name, String url)
    {
        super(name);
        this.url = url;

        genres = new ArrayList<>();
        chapters = new ArrayList<>();
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
                    handler.post(success);
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
    }

    private boolean parseMangaInfo(String html)
    {
        if (html == null || "".equals(html))
            return (false);

        summary = Utils.fromHtmlString(Utils.parseUnique(html, "<p class=\"summary\">", ">", "</p>"));
        authors = Utils.parseUnique(html, "href=\"/search/artist/", ">", "<");
        genres = Utils.parseMultiple(html, "<a href=\"http://mangafox.me/search/genres/", ">", "<");

        parseStatusAndRating(html);

        return (parseCover(html) && parseChapters(html));
    }

    private void parseStatusAndRating(String html)
    {
        status = Utils.parseUnique(html, "<h5>Status", "<span>", "<i>");
        if (status != null)
        {
            int comma = status.indexOf(",");
            if (comma != -1)
                status = status.substring(0, comma);

            status = status.trim();
        }

        String rating_str = Utils.parseUnique(html, "<h5>Rating:</h5>", "<span>", "</span>");
        if (rating_str != null)
        {
            int start = rating_str.indexOf(" ") + 1;
            int end = rating_str.indexOf(" ", start);
            rating = Float.valueOf(rating_str.substring(start, end)) / 5.f;
        }
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

            String chapter_number =  Utils.parseUnique(str, "<a href=\"", ">", "<").trim();
            chapter_number = "Chapter" + chapter_number.substring(chapter_number.lastIndexOf(" "));

            String name = Utils.parseUnique(str, "<span class=\"title nowrap\">", ">", "<");
            chapters.add(new FoxChapter(name == null ? chapter_number : chapter_number + " " + name, url));
        }
        return (true);
    }
}
