package vikings.mangareader.MangaFox;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;
import vikings.mangareader.Utils;

public class MangaFoxSearchLoader extends Loader<List<Loader<Manga>>>
{
    private List<String> genres_supported = new ArrayList<>(
            Arrays.asList("Action", "Adult", "Adventure", "Comedy", "Doujinshi", "Drama", "Ecchi", "Fantasy", "Gender Bender", "Harem",
                    "Historical", "Horror", "Josei", "Martial Arts", "Mature", "Mecha", "Mystery", "One Shot", "Psychological",
                    "Romance", "School Life", "Sci-fi", "Seinen", "Shoujo", "Shoujo Ai", "Shounen", "Shounen Ai", "Slice of Life",
                    "Smut", "Sports", "Supernatural", "Tragedy", "Webtoons", "Yaoi", "Yuri"));

    private String manga_to_search;

    public MangaFoxSearchLoader()
    {
        manga_to_search = null;
    }

    public MangaFoxSearchLoader(String manga_name_to_search)
    {
        manga_to_search = manga_name_to_search;
    }

    public List<Loader<Manga>> load()
    {
        return (parseSearch(Utils.InputStreamToString(Utils.getInputStreamFromURL(createUrlForSerach(null)))));
    }

    private String createUrlForSerach(@Nullable List<String> in_genre)
    {
        if (manga_to_search == null)
            return (null);

        manga_to_search = manga_to_search.replaceAll("[^a-zA-Z0-9 _]+","");
        if (manga_to_search.trim().isEmpty())
            return (null);

        String url = "http://mangafox.me/search.php?name_method=cw&name=" + manga_to_search.replaceAll(" ", "+") + "&type=&author_method=cw&author=&artist_method=cw&artist=";
        for (String genre : genres_supported)
            url += "&genres[" + genre.replaceAll(" ", "+") + "]=" + (in_genre != null && in_genre.contains(genre) ? "1" : "0");

        return (url + "&released_method=eq&released=&rating_method=eq&rating=&is_completed=&advopts=1");
    }

    private List<Loader<Manga>> parseSearch(String html)
    {
        if (html == null || "".equals(html))
            return (null);

        ArrayList<Loader<Manga>> searched_mangas = new ArrayList<>();
        for (String to_parse : Utils.parseMultiple(html, "<div class=\"manga_text\">", "href=\"", "<"))
        {
            searched_mangas.add(new FoxMangaLoader(to_parse.substring(to_parse.indexOf(">") + 1), to_parse.substring(0, to_parse.indexOf("\"")) ));
        }

        return (searched_mangas);
    }
}
