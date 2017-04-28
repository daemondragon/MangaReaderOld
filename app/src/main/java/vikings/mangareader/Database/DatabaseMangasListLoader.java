package vikings.mangareader.Database;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class DatabaseMangasListLoader extends Loader<List<Loader<Manga>>>
{
    private File manga_list_dir;

    public DatabaseMangasListLoader(Context context)
    {
        manga_list_dir = context.getFilesDir();
    }

    public List<Loader<Manga>> load()
    {
        List<Loader<Manga>> manga_list = new ArrayList<>();

        for (File chapter : manga_list_dir.listFiles())
            if (chapter.isDirectory())
                manga_list.add(new DatabaseMangaLoader(chapter.getAbsolutePath()));

        return (manga_list);
    }
}
