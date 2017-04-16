package vikings.mangareader.MangaFox;

import android.content.Context;

import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.ChapterLoader;
import vikings.mangareader.Utils;

class FoxChapterLoader extends ChapterLoader
{
    private String url;

    FoxChapterLoader(Context context, String name, String url)
    {
        super(context, name);
        this.url = url;
    }

    public Chapter loadInBackground()
    {
        return (parseChapter(Utils.InputStreamToString(Utils.getInputStreamFromURL(url))));
    }

    private Chapter parseChapter(String html)
    {
        if (html == null || "".equals(html))
            return (null);

        Chapter chapter = new Chapter(name());
        chapter.first_page = new FoxPageLoader(getContext(), url);

        return (chapter);
    }
}
