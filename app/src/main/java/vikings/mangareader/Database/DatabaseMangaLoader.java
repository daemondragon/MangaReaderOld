package vikings.mangareader.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class DatabaseMangaLoader extends Loader<Manga>
{
    private SQLiteDatabase db;

    DatabaseMangaLoader(@NonNull SQLiteDatabase db, @NonNull String manga_name)
    {
        super(manga_name);
        this.db = db;
    }

    public Manga load()
    {
        Cursor cursor = db.query(DatabaseOpenHelper.MANGA_TABLE, null,
                DatabaseOpenHelper.MANGA_NAME + "=" + name(), null, null, null, null);

        Manga manga = null;
        if (cursor.moveToFirst())
        {
            manga = new Manga(name());
            manga.authors = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.MANGA_AUTHORS));
            manga.genres = Arrays.asList(cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.MANGA_GENRES))
                    .split(DatabaseOpenHelper.LIST_SEPARATOR));
            manga.cover = null;
            manga.summary = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.MANGA_SUMMARY));
            manga.rating = cursor.getFloat(cursor.getColumnIndex(DatabaseOpenHelper.MANGA_RATING));
            manga.status = null;

            manga.chapters = new ArrayList<>();
            for (String chapter_name : cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.MANGA_CHAPTERS))
                    .split(DatabaseOpenHelper.LIST_SEPARATOR))
                manga.chapters.add(new DatabaseChapterLoader(db, name(), chapter_name));
        }

        cursor.close();
        return (manga);
    }
}
