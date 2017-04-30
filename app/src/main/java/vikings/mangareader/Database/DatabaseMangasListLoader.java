package vikings.mangareader.Database;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class DatabaseMangasListLoader extends Loader<List<Loader<Manga>>>
{
    private File manga_list_dir;
    private Context context;

    public DatabaseMangasListLoader(Context context)
    {
        this.context = context;
        manga_list_dir = context.getFilesDir();
    }

    public List<Loader<Manga>> load()
    {
        List<Loader<Manga>> manga_list = new ArrayList<>();

        for (File chapter : manga_list_dir.listFiles())
            if (chapter.isDirectory())
                manga_list.add(new DatabaseMangaLoader(context, chapter.getAbsolutePath()));

        Collections.sort(manga_list, new Comparator<Loader<Manga>>() {
            @Override
            public int compare(Loader<Manga> o1, Loader<Manga> o2) {
                return o1.name().compareToIgnoreCase(o2.name());
            }
        });


        return (manga_list);
    }
}
