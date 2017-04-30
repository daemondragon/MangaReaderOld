package vikings.mangareader.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseOpenHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "manga.db";
    private static final int    DB_VERSION = 1;

    static final String MANGA_TABLE  = "MANGA";
    static final String NAME         = "NAME";
    static final String AUTHORS      = "AUTHORS";
    static final String SUMMARY      = "SUMMARY";
    static final String STATUS       = "STATUS";
    static final String RATING       = "RATING";
    static final String GENRES       = "GENRES";

    DatabaseOpenHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MANGA_TABLE + " (" +
                NAME + " TEXT NOT NULL," +
                AUTHORS + " TEXT," +
                SUMMARY + " TEXT," +
                STATUS + " TEXT," +
                RATING + " REAL," +
                GENRES + " TEXT" +
        ")");
    }

    private void onDelete(SQLiteDatabase db)
    {
        db.execSQL("DELETE TABLE " + MANGA_TABLE + " IF EXISTS");
    }

    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version)
    {
        onDelete(db);
        onCreate(db);
    }
}
