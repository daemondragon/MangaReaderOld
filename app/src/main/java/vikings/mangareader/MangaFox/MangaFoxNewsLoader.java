package vikings.mangareader.MangaFox;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;
import vikings.mangareader.Utils;

public class MangaFoxNewsLoader extends Loader<List<Loader<Manga>>>
{
    public List<Loader<Manga>> load()
    {
        return (parseNewMangas(Utils.InputStreamToString(Utils.getInputStreamFromURL("http://mangafox.me/releases/"))));
    }

    private List<Loader<Manga>> parseNewMangas(String html)
    {
        if (html == null || "".equals(html))
            return (null);

        ArrayList<Loader<Manga>> new_mangas = new ArrayList<>();
        for (String to_parse : Utils.parseMultiple(html, "<h3 class=\"title\">", ">", "</h3>"))
        {
            String url = Utils.parseUnique(to_parse, "<a href=\"", "\"", "\"");
            String name = Utils.parseUnique(to_parse, "<a href=\"", ">", "</a>");
            new_mangas.add(new FoxMangaLoader(name, url));
        }

        return (new_mangas);
    }
}
