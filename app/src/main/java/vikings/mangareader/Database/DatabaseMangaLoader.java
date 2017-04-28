package vikings.mangareader.Database;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

class DatabaseMangaLoader extends Loader<Manga>
{
    private String manga_path;

    DatabaseMangaLoader(@NonNull String manga_path)
    {
        super(manga_path.substring(manga_path.lastIndexOf("/") + 1));
        this.manga_path = manga_path;
    }

    public Manga load()
    {
        File manga_dir = new File(manga_path);
        if (!manga_dir.isDirectory())
            return (null);

        Manga manga = new Manga(name());

        List<String> chapters = new ArrayList<>();
        for (File chapter : manga_dir.listFiles())
            if (chapter.isDirectory())
                chapters.add(chapter.getAbsolutePath());

        Collections.sort(chapters);
        manga.chapters = new ArrayList<>();
        for (String chapter : chapters)
            manga.chapters.add(new DatabaseChapterLoader(chapter));

        return (manga);
    }
}
