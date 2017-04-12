package vikings.mangareader.MangaFoxProvider;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vikings.mangareader.MangaProvider.MangaProvider;

public class MangaFoxProvider extends MangaProvider
{
    public MangaFoxProvider()
    {
        mangas = new ArrayList<>();
        genres_supported = new ArrayList<>(
                Arrays.asList("Action", "Adult", "Adventure", "Comedy", "Doujinshi", "Drama", "Ecchi", "Fantasy", "Gender Bender", "Harem",
                        "Historical", "Horror", "Josei", "Martial Arts", "Mature", "Mecha", "Mystery", "One Shot", "Psychological",
                        "Romance", "School Life", "Sci-fi", "Seinen", "Shoujo", "Shoujo Ai", "Shounen", "Shounen Ai", "Slice of Life",
                        "Smut", "Sports", "Supernatural", "Tragedy", "Webtoons", "Yaoi", "Yuri"));
    }

    public void load(final @Nullable Runnable success, final @Nullable Runnable error)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler(Looper.getMainLooper());
                if (parseNewMangas(Utils.InputStreamToString(Utils.getInputStreamFromURL("http://mangafox.me/releases/"))))
                    handler.post(success);
                else
                    handler.post(error);
            }
        }).start();
    }

    public void unload()
    {
        mangas.clear();
    }

    private boolean parseNewMangas(String html)
    {
        if (html == null || "".equals(html))
            return (false);

        mangas.clear();

        try
        {
            int start_manga = html.indexOf("<h3 class=\"title\">");
            int end_manga = -1;
            if (start_manga != -1)
                end_manga = html.indexOf("</h3>", start_manga);

            while (start_manga != -1)
            {
                int start_url = html.indexOf("<a href=\"", start_manga) + "<a href=\"".length();
                int end_url = html.indexOf("\"", start_url) - 1;
                int start_name = html.indexOf(">", end_url) + 1;
                int end_name = html.indexOf("</a>", start_name);

                mangas.add(new FoxManga(html.substring(start_name, end_name), html.substring(start_url, end_url)));

                start_manga = html.indexOf("<h3 class=\"title\">", end_manga);
                if (start_manga != -1)
                    end_manga = html.indexOf("</h3>", start_manga);
            }
            return (true);
        }
        catch (Exception e)
        {
            return (false);
        }
    }

    public void search(final String manga_name, final @Nullable List<String> in_genre, final Runnable success, final Runnable error)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler(Looper.getMainLooper());
                if (parseSearch(Utils.InputStreamToString(Utils.getInputStreamFromURL(createUrlForSerach(manga_name, in_genre)))))
                    handler.post(success);
                else
                    handler.post(error);
            }
        }).start();
    }

    private String createUrlForSerach(String manga_name, @Nullable List<String> in_genre)
    {
        if (manga_name == null)
            return (null);

        manga_name = manga_name.replaceAll("[^a-zA-Z0-9 _]+","");
        if (manga_name.trim().isEmpty())
            return (null);

        String url = "http://mangafox.me/search.php?name_method=cw&name=" + manga_name.replaceAll(" ", "+") + "&type=&author_method=cw&author=&artist_method=cw&artist=";
        for (String genre : genres_supported)
            url += "&genres[" + genre.replaceAll(" ", "+") + "]=" + (in_genre != null && in_genre.contains(genre) ? "1" : "0");

        return (url + "&released_method=eq&released=&rating_method=eq&rating=&is_completed=&advopts=1");
    }

    private boolean parseSearch(String html)
    {
        if (html == null)
            return (false);

        mangas.clear();
        for (String str : Utils.parseMultiple(html, "<div class=\"manga_text\">", "href=\"", "<"))
        {
            mangas.add(new FoxManga(str.substring(str.indexOf(">") + 1), str.substring(0, str.indexOf("\""))));
        }
        return (true);
    }
}
