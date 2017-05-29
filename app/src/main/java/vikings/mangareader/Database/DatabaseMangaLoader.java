package vikings.mangareader.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class DatabaseMangaLoader extends Loader<Manga>
{
    private Context context;
    public String manga_path;

    public DatabaseMangaLoader(Context context, @NonNull String manga_path)
    {
        super(manga_path.substring(manga_path.lastIndexOf("/") + 1));
        this.manga_path = manga_path;
        this.context = context;
    }

    public Manga load()
    {
        File manga_dir = new File(manga_path);
        if (!manga_dir.isDirectory())
            return (null);

        Manga manga = new Manga(name());
        SQLiteDatabase db = (new DatabaseOpenHelper(context)).getWritableDatabase();
        if (db != null && db.isOpen())
        {
            String[] args = { manga.name() };
            Cursor cursor = db.query(DatabaseOpenHelper.MANGA_TABLE, null, DatabaseOpenHelper.NAME + "=?", args, null, null, null);
            if (cursor.moveToFirst())
            {
                manga.name = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.NAME));
                manga.authors = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.AUTHORS));
                manga.summary = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.SUMMARY));
                manga.status = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.STATUS));
                manga.rating = cursor.getFloat(cursor.getColumnIndex(DatabaseOpenHelper.RATING));
                manga.genres = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.GENRES));
            }
            cursor.close();
        }

        manga.cover = Drawable.createFromPath(manga_path + "/cover.png");

        List<String> chapters = new ArrayList<>();
        for (File chapter : manga_dir.listFiles())
            if (chapter.isDirectory())
                chapters.add(chapter.getAbsolutePath());

        Collections.sort(chapters, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                o1 = o1.substring(o1.indexOf(" ") + 1);
                o2= o2.substring(o2.indexOf(" ") + 1);

                int e1 = o1.indexOf(" ");
                if (e1 != -1)
                    o1 = o1.substring(0, e1);
                int e2 = o2.indexOf(" ");
                if (e2 != -1)
                    o2 = o2.substring(0, e1);

                return (Integer.getInteger(o1, 0) - Integer.getInteger(o2, 0));
            }
        });

        manga.chapters = new ArrayList<>();
        for (String chapter : chapters)
            manga.chapters.add(new DatabaseChapterLoader(chapter));

        return (manga);
    }
}
