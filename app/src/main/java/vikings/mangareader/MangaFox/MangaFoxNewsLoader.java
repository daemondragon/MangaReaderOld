package vikings.mangareader.MangaFox;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Manga.MangaLoader;
import vikings.mangareader.Utils;

public class MangaFoxNewsLoader extends AsyncTaskLoader<List<MangaLoader>>
{
    public MangaFoxNewsLoader(Context context)
    {
        super(context);
    }

    public List<MangaLoader> loadInBackground()
    {
        return (parseNewMangas(Utils.InputStreamToString(Utils.getInputStreamFromURL("http://mangafox.me/releases/"))));
    }

    private List<MangaLoader> parseNewMangas(String html)
    {
        if (html == null || "".equals(html))
            return (null);

        ArrayList<MangaLoader> new_mangas = new ArrayList<>();
        for (String to_parse : Utils.parseMultiple(html, "<h3 class=\"title\">", ">", "</h3>"))
        {
            String url = Utils.parseUnique(to_parse, "<a href=\"", "\"", "\"");
            String name = Utils.parseUnique(to_parse, "<a href=\"", ">", "</a>");
            new_mangas.add(new FoxMangaLoader(getContext(), name, url));
        }

        return (new_mangas);
    }
}
