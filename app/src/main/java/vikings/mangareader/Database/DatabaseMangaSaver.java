package vikings.mangareader.Database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
    private AsyncRunner saver = new AsyncRunner();

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
        List<Loader<Chapter>> to_save;

        MangaSaver(Manga manga, List<Loader<Chapter>> to_save)
        {
            this.manga = manga;
            this.to_save = to_save;
        }

        public boolean run()
        {
            Log.d("Manga", "Argh");
            manga_dir = new File(context.getFilesDir(), manga.name());

            return (manga_dir.exists() || manga_dir.mkdir());
        }

        public void onSuccess()
        {
            Log.d("Manga", "Success");
            for (Loader<Chapter> chapter : to_save)
                saver.process(new ChapterSaver(manga_dir, chapter));
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
    }

    private class ChapterSaver implements AsyncRunner.Runnable
    {
        File manga_dir;
        Loader<Chapter> to_save;
        Chapter to_use;

        ChapterSaver(File manga_dir, Loader<Chapter> to_save)
        {
            this.manga_dir = manga_dir;
            this.to_save = to_save;
        }

        public boolean run()
        {
            File chapter_dir = new File(manga_dir, to_save.name());
            to_use = to_save.load();

            boolean dir_exits = true;
            if (!chapter_dir.exists())
                dir_exits = chapter_dir.mkdir();

            Log.d("Chapter", "Argh " + dir_exits + ";" + (to_use != null));

            return (dir_exits && to_use != null);
        }

        public void onSuccess()
        {
            Log.d("Chapter", "Success");
            saver.process(new PageSaver(new File(manga_dir, to_save.name()), to_use.first_page, 0));
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
    }

    private class PageSaver implements AsyncRunner.Runnable {
        File chapter_dir;
        Loader<Page> to_save;
        Page result;
        int index;

        PageSaver(File chapter_dir, Loader<Page> to_save, int index)
        {
            this.chapter_dir = chapter_dir;
            this.to_save = to_save;
            this.index = index;
        }

        public boolean run()
        {
            Log.d("Page", "Argh");
            result = to_save.load();
            if (result == null || result.getPicture() == null)
                return (false);

            try {
                FileOutputStream file = new FileOutputStream(new File(chapter_dir, Integer.toString(index) + ".png"));
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
            Log.d("Page", "Success");
            if (result.hasNext())
                saver.process(new PageSaver(chapter_dir, result.next, index + 1));
        }

        public void onError() {

        }

        public boolean retry() {
            return (true);
        }

        public String errorDescription() {
            return (context().getResources().getString(R.string.chapter_loading_error));
        }

        public Context context() {
            return (context);
        }
    }
}
