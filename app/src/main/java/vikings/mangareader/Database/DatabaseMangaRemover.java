package vikings.mangareader.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.List;

import vikings.mangareader.Manga.AsyncRunner;
import vikings.mangareader.R;

/**
 * Remove the chapters of a given manga
 * If all chapters of the manga are  removed,
 * the manga information are as well removed from the database.
 */
public class DatabaseMangaRemover implements AsyncRunner.Runnable
{
    private Context context;
    private String manga_name;
    private List<String> chapters_name;

    DatabaseMangaRemover(Context context, String manga_name, List<String> chapters_name)
    {
        this.context = context;
        this.manga_name = manga_name;
        this.chapters_name = chapters_name;
    }

    public boolean run()
    {
        File manga_dir = new File(context.getFilesDir(), manga_name);
        if (!manga_dir.exists())
            return (false);

        for (String chapter_name : chapters_name)
        {
            File chapter = new File(manga_dir, chapter_name);
            if (chapter.exists())
                chapter.delete();
        }

        if (manga_dir.listFiles().length == 0)
        {
            SQLiteDatabase db = (new DatabaseOpenHelper(context())).getWritableDatabase();
            if (db.isOpen())
            {
                String[] args = {manga_name};
                db.delete(DatabaseOpenHelper.MANGA_TABLE, DatabaseOpenHelper.NAME + "=?", args);
            }
            else
                return (false);
        }
        return (true);
    }

    public String errorDescription()
    {
        return (context().getResources().getString(R.string.manga_removing_error));
    }

    public boolean retry()
    {
        return (true);
    }

    public void onSuccess()
    {
    }

    public void onError()
    {
    }

    public Context context()
    {
        return (context);
    }
}
