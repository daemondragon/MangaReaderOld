package vikings.mangareader.MangaFox;

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

        return (chapter);
    }
}
