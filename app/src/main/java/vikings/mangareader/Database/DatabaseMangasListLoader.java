package vikings.mangareader.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class DatabaseMangasListLoader extends Loader<List<Loader<Manga>>>
{
    private SQLiteDatabase db;

    public DatabaseMangasListLoader(Context context)
    {
        db = new DatabaseOpenHelper(context).getReadableDatabase();
    }

    public List<Loader<Manga>> load()
    {
        List<Loader<Manga>> manga_list = new ArrayList<>();

        String[] columns = { DatabaseOpenHelper.MANGA_NAME };
        Cursor cursor = db.query(DatabaseOpenHelper.MANGA_TABLE, columns, null, null, null, null, DatabaseOpenHelper.MANGA_NAME + " COLLATE NOCASE");
        while (cursor.moveToNext())
        {
            manga_list.add(new DatabaseMangaLoader(db, cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.MANGA_NAME))));
        }
        cursor.close();
        return (manga_list);
    }
}
