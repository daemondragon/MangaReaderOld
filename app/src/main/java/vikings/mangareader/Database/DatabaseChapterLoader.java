package vikings.mangareader.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;

class DatabaseChapterLoader extends Loader<Chapter>
{
    private SQLiteDatabase db;
    private String manga_name;

    DatabaseChapterLoader(@NonNull SQLiteDatabase db, @NonNull String manga_name,@NonNull String chapter_name)
    {
        super(chapter_name);
        this.db = db;
        this.manga_name = manga_name;
    }

    public Chapter load()
    {
        String[] columns = { DatabaseOpenHelper.CHAPTER_PAGES };
        Cursor cursor = db.query(DatabaseOpenHelper.CHAPTER_TABLE,
                columns,
                DatabaseOpenHelper.MANGA_NAME + "=" + manga_name + " AND " + DatabaseOpenHelper.CHAPTER_NAME + "=" + name(),
                null, null, null, null);

        Chapter chapter = null;
        if (cursor.moveToFirst())
        {
            chapter = new Chapter(name());
            List<String> list = Arrays.asList(cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.CHAPTER_PAGES))
                                                    .split(DatabaseOpenHelper.LIST_SEPARATOR));

            chapter.first_page = new DatabasePageLoader(list, 0);
            chapter.last_page = new DatabasePageLoader(list, list.size() - 1);
        }

        cursor.close();
        return (chapter);
    }
}
