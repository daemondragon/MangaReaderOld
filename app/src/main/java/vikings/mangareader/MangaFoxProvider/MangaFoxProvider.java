package vikings.mangareader.MangaFoxProvider;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.MangaProvider.Manga;
import vikings.mangareader.MangaProvider.MangaProvider;

public class MangaFoxProvider implements MangaProvider
{
    private ArrayList<Manga> mangas_list = new ArrayList<>();
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

    public List<Manga> getNewMangas()
    {
        return (mangas_list);
    }

    private boolean parseNewMangas(String html)
    {
        if (html == null || "".equals(html))
            return (false);

        mangas_list.clear();

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

                mangas_list.add(new FoxManga(html.substring(start_name, end_name), html.substring(start_url, end_url)));

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
}
