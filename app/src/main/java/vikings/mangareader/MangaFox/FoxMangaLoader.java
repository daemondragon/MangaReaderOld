package vikings.mangareader.MangaFox;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;
import vikings.mangareader.Utils;

class FoxMangaLoader extends Loader<Manga>
{
    private String url;

    FoxMangaLoader(String name, String url)
    {
        super(name);
        this.url = url;
    }

    public Manga load()
    {
        String html = Utils.InputStreamToString(Utils.getInputStreamFromURL(url));
        if (html == null || "".equals(html))
            return (null);

        Manga manga = new Manga(name());

        manga.summary = Utils.fromHtmlString(Utils.parseUnique(html, "<p class=\"summary\">", ">", "</p>"));
        manga.authors = Utils.parseUnique(html, "href=\"/search/artist/", ">", "<");
        List<String> temp = Utils.parseMultiple(html, "<a href=\"http://mangafox.me/search/genres/", ">", "<");
        if (!temp.isEmpty())
        {
            manga.genres = temp.get(0);
            for (int i = 1; i < temp.size(); ++i)
                manga.genres += ", " + temp.get(i);
        }

        parseStatusAndRating(html, manga);
        parseCover(html, manga);
        parseChapters(html, manga);

        return (manga);
    }

    private void parseStatusAndRating(String html, Manga manga) {
        manga.status = Utils.parseUnique(html, "<h5>Status", "<span>", "<i>");
        if (manga.status != null) {
            int comma = manga.status.indexOf(",");
            if (comma != -1)
                manga.status = manga.status.substring(0, comma);

            manga.status = manga.status.trim();
        }

        String rating_str = Utils.parseUnique(html, "<h5>Rating:</h5>", "<span>", "</span>");
        if (rating_str != null) {
            int start = rating_str.indexOf(" ") + 1;
            int end = rating_str.indexOf(" ", start);
            manga.rating = Float.valueOf(rating_str.substring(start, end)) / 5.f;
        }
    }

    private void parseCover(String html, Manga manga)
    {
        String cover_url = Utils.parseUnique(html, "<meta property=\"og:image\"", "content=\"", "\"");
        if (!"".equals(cover_url))
        {
            InputStream in = Utils.getInputStreamFromURL(cover_url);
            if (in != null)
                manga.cover = Drawable.createFromStream(in, "cover");
            else
                Log.d("parseCover", "can't get input stream from url: " + cover_url);
        }
        else
            Log.d("parseCover", "url not found");
    }

    private void parseChapters(String html, Manga manga)
    {
        if (manga.chapters != null)
            manga.chapters.clear();
        else
            manga.chapters = new ArrayList<>();

        //<h3> are not used because some mangas also use <h4>
        for (String str : Utils.parseMultiple(html, "<a class=\"edit\" href=\"", "<h", "</h"))
        {
            String url = Utils.parseUnique(str, "<a href=\"", "\"", "\"");

            String chapter_number =  Utils.parseUnique(str, "<a href=\"", ">", "<").trim();
            chapter_number = "Chapter" + chapter_number.substring(chapter_number.lastIndexOf(" "));

            String name = Utils.parseUnique(str, "<span class=\"title nowrap\">", ">", "<");
            manga.chapters.add(new FoxChapterLoader(name == null ? chapter_number : chapter_number + " " + name, url));
        }
    }
}
