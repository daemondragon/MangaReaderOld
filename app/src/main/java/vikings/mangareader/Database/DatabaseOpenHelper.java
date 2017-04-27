package vikings.mangareader.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseOpenHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME     = "manga.db";
    private static final int    DB_VERSION  = 1;

    static final String LIST_SEPARATOR  = "-|-";

    static final String  MANGA_TABLE     = "MANGA";
    static final String  MANGA_NAME      = "MANGA_NAME";
    static final String  MANGA_AUTHORS   = "MANGA_AUTHORS";
    static final String  MANGA_GENRES    = "MANGA_GENRES";
    static final String  MANGA_RATING    = "MANGA_RATING";
    static final String  MANGA_SUMMARY   = "MANGA_SUMMARY";
    static final String  MANGA_CHAPTERS  = "MANGA_CHAPTERS";

    static final String  CHAPTER_TABLE   = "CHAPTER";
    static final String  CHAPTER_NAME    = "CHAPTER_NAME";
    static final String  CHAPTER_PAGES   = "CHAPTER_PAGES";

    public DatabaseOpenHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + MANGA_TABLE + " IF NOT EXISTS(" +
                MANGA_NAME + " TEXT NOT NULL," +
                MANGA_AUTHORS + " TEXT," +
                MANGA_GENRES + " TEXT," +
                MANGA_RATING + " REAL," +
                MANGA_SUMMARY + " TEXT," +
                MANGA_CHAPTERS + " TEXT" +
                ")");
        db.execSQL("CREATE TABLE " + CHAPTER_TABLE + " IF NOT EXISTS(" +
                MANGA_NAME + " TEXT NOT NULL," +
                CHAPTER_NAME + " TEXT NOT NULL," +
                CHAPTER_PAGES + " TEXT" +
                ")");
    }

    private void onDelete(SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE " + MANGA_TABLE + " IF EXISTS");
        db.execSQL("DROP TABLE " + CHAPTER_TABLE + " IF EXISTS");
    }

    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version)
    {
        onDelete(db);
        onCreate(db);
    }
}
