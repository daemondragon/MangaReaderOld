package vikings.mangareader.MangaFoxProvider;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.MangaProvider.Manga;
import vikings.mangareader.Utils;

public class MangaFoxSearchLoader extends AsyncTaskLoader<List<Manga>>
{

    public MangaFoxSearchLoader(Context context, String manga_name_to_search)
    {
        super(context);
    }

    public List<Manga> loadInBackground()
    {
        return (parseNewMangas(Utils.InputStreamToString(Utils.getInputStreamFromURL("http://mangafox.me/releases/"))));
    }

    private List<Manga> parseNewMangas(String html)
    {
        if (html == null || "".equals(html))
            return (null);

        ArrayList<Manga> new_mangas = new ArrayList<>();
        for (String to_parse : Utils.parseMultiple(html, "<h3 class=\"title\">", ">", "</h3>"))
        {
            String url = Utils.parseUnique(to_parse, "<a href=\"", "\"", "\"");
            String name = Utils.parseUnique(to_parse, "<a href=\"", ">", "</a>");
            new_mangas.add(new FoxManga(name, url));
        }

        return (new_mangas);
    }
}
