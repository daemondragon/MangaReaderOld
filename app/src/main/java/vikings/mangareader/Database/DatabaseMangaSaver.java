package vikings.mangareader.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import vikings.mangareader.Manga.AsyncRunner;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;
import vikings.mangareader.Manga.Page;
import vikings.mangareader.R;

public class DatabaseMangaSaver
{
    private Context context;
    private static AsyncRunner saver = new AsyncRunner();

    public DatabaseMangaSaver(Context context)
    {
        this.context = context;
    }

    public void save(Manga manga, List<Loader<Chapter>> to_save)
    {
        saver.process(new MangaSaver(manga, to_save));
    }

    private class MangaSaver implements AsyncRunner.Runnable
    {
        File manga_dir;
        Manga manga;
        List<Loader<Chapter>> chapters;
        int chapter_index;

        MangaSaver(Manga manga, List<Loader<Chapter>> to_save)
        {
            this.manga = manga;
            this.chapters = to_save;
            chapter_index = 0;
        }

        public boolean run()
        {
            (Toast.makeText(context(), "saving " + manga.name() + "..." ,Toast.LENGTH_SHORT)).show();

            manga_dir = new File(context.getFilesDir(), manga.name());
            if (!manga_dir.exists() && !manga_dir.mkdir())
                return (false);

            SQLiteDatabase db = (new DatabaseOpenHelper(context())).getWritableDatabase();
            if (db == null || !db.isOpen())
                return (false);

            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.NAME, manga.name());
            values.put(DatabaseOpenHelper.AUTHORS, manga.authors());
            values.put(DatabaseOpenHelper.SUMMARY, manga.summary());
            values.put(DatabaseOpenHelper.STATUS, manga.status());
            values.put(DatabaseOpenHelper.RATING, manga.rating());
            values.put(DatabaseOpenHelper.GENRES, manga.genres());


            String[] columns = { DatabaseOpenHelper.NAME };
            String[] args = { manga.name() };
            Cursor cursor = db.query(DatabaseOpenHelper.MANGA_TABLE, columns, DatabaseOpenHelper.NAME + "=?", args, null, null, null);
            long answer;
            if (cursor.moveToFirst())
                answer = db.update(DatabaseOpenHelper.MANGA_TABLE, values, DatabaseOpenHelper.NAME + "=?", args) - 1;
            else
                answer = db.insert(DatabaseOpenHelper.MANGA_TABLE, null, values);

            cursor.close();
            db.close();

            if (manga.cover() != null)
            {
                try {
                    FileOutputStream file = new FileOutputStream(new File(manga_dir, "cover.png"));
                    Bitmap bmp = ((BitmapDrawable) manga.cover()).getBitmap();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, file);
                    file.flush();
                    file.close();
                }
                catch (Exception e)
                {
                    Log.d("MangaSaver", e.toString());
                }
            }

            return (answer >= 0);
        }

        public void onSuccess()
        {
            if (!chapters.isEmpty())
                saver.process(new ChapterSaver());
        }

        public void onError()
        {
        }

        public boolean retry()
        {
            return (true);
        }

        public String errorDescription()
        {
            return (context().getResources().getString(R.string.manga_loading_error));
        }

        public Context context()
        {
            return (context);
        }


        private class ChapterSaver implements AsyncRunner.Runnable
        {
            File chapter_dir;
            Chapter chapter;

            ChapterSaver()
            {
            }

            public boolean run()
            {
                (Toast.makeText(context(), "saving chapters " + chapters.get(chapter_index).name() + "..." ,Toast.LENGTH_SHORT)).show();
                if (chapter_index < 0 || chapter_index >= chapters.size())
                    return (true);

                chapter_dir = new File(manga_dir, chapters.get(chapter_index).name());
                chapter = chapters.get(chapter_index).load();
                boolean dir_exits = true;
                if (!chapter_dir.exists())
                    dir_exits = chapter_dir.mkdir();

                return (dir_exits && chapter != null);
            }

            public void onSuccess()
            {
                if (chapter_index >= 0 && chapter_index < chapters.size())
                    saver.forceProcess(new PageSaver(chapter.first_page, 0));
            }

            public void onError()
            {
            }

            public boolean retry()
            {
                return (true);
            }

            public String errorDescription()
            {
                return (context().getResources().getString(R.string.chapter_loading_error));
            }

            public Context context()
            {
                return (context);
            }


            private class PageSaver implements AsyncRunner.Runnable
            {
                Loader<Page> to_load;
                Page result;
                int page_index;

                PageSaver(Loader<Page> to_load, int page_index)
                {
                    this.to_load = to_load;
                    this.page_index = page_index;
                }

                public boolean run()
                {
                    if (to_load == null)
                        return (true);

                    result = to_load.load();
                    if (result == null || result.getPicture() == null)
                        return (false);

                    try {
                        FileOutputStream file = new FileOutputStream(new File(chapter_dir, Integer.toString(page_index) + ".png"));
                        Bitmap bmp = ((BitmapDrawable) result.getPicture()).getBitmap();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, file);
                        file.flush();
                        file.close();

                        return (true);
                    } catch (Exception e) {
                        Log.d("PageSaver.run", e.toString());
                        return (false);
                    }
                }

                public void onSuccess()
                {
                    if (result.hasNext())
                        saver.forceProcess(new PageSaver(result.next, page_index + 1));
                    else {
                        chapter_index++;
                        saver.forceProcess(new ChapterSaver());
                    }
                }

                public void onError() {

                }

                public boolean retry() {
                    return (true);
                }

                public String errorDescription() {
                    return (context().getResources().getString(R.string.page_loading_error));
                }

                public Context context() {
                    return (context);
                }
            }
        }
    }
}
