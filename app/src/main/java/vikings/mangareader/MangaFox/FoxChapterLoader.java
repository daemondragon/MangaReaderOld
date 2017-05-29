package vikings.mangareader.MangaFox;

import android.content.Intent;

import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;

class FoxChapterLoader extends Loader<Chapter>
{
    private String url;

    FoxChapterLoader(String name, String url)
    {
        super(name);
        this.url = url;
    }

    public Chapter load()
    {
        Chapter chapter = new Chapter(name());
        chapter.first_page = new FoxPageLoader(url);

        int index = url.lastIndexOf("/");
        if (index != -1)//MangaFox technique used to get last page
            chapter.last_page = new FoxPageLoader(url.substring(0, index) + "/999.html");

        return (chapter);
    }
}
