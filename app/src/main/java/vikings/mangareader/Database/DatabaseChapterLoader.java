package vikings.mangareader.Database;

import android.support.annotation.NonNull;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;

class DatabaseChapterLoader extends Loader<Chapter>
{
    private String chapter_path;

    DatabaseChapterLoader(@NonNull String chapter_path)
    {
        super(chapter_path.substring(chapter_path.lastIndexOf("/") + 1));
        this.chapter_path = chapter_path;
    }

    public Chapter load()
    {
        File chapter_dir = new File(chapter_path);
        if (!chapter_dir.isDirectory())
            return (null);

        List<String> pages = new ArrayList<>();
        for (File page : chapter_dir.listFiles())
            if (page.isFile())
                pages.add(page.getAbsolutePath());

        Collections.sort(pages);

        Chapter chapter = new Chapter(name());
        if (!pages.isEmpty())
        {
            chapter.first_page = new DatabasePageLoader(pages, 0);
            chapter.last_page = new DatabasePageLoader(pages, pages.size() - 1);
        }
        return (chapter);
    }
}
