package vikings.mangareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.List;

import vikings.mangareader.Manga.AsyncRunner;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Page;

public class PageActivity extends AppCompatActivity
{
    private static List<Loader<Chapter>> chapters;
    private static int chapter_index = -1;

    static private Page current_page = null;
    static private Chapter current_chapter = null;
    private GestureDetector detector;

    private AsyncRunner loader = new AsyncRunner();

    public static void start(Context context, List<Loader<Chapter>> chapters, int chapter_index)
    {
        if (chapters == null || chapter_index < 0 || chapter_index >= chapters.size())
            return;

        PageActivity.chapters = chapters;
        PageActivity.chapter_index = chapter_index;

        context.startActivity(new Intent(context, PageActivity.class));
    }

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.page_layout);

        init();
    }

    public boolean dispatchTouchEvent(MotionEvent e)
    {
        detector.onTouchEvent(e);
        return (super.dispatchTouchEvent(e));
    }



    public void init()
    {
        loader.process(new ChapterLoader(chapters.get(chapter_index), true));

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
        {
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                final float offsetX = event1.getX() - event2.getX();
                float offsetY = event1.getY() - event2.getY();

                findViewById(R.id.loading_tag).setVisibility(View.INVISIBLE);

                if (Math.abs(offsetX) > Math.abs(offsetY) * 2)
                {
                    if (offsetX < 0)
                    {//previous
                        if (current_page != null)
                        {
                            if (current_page.hasPrevious())
                                loader.process(new PageLoader(current_page.previous));
                            else if (chapter_index + 1 < chapters.size())
                                loader.process(new ChapterLoader(chapters.get(++chapter_index), false));
                            else
                                finish();
                        }
                        else
                            finish();
                    }
                    else
                    {
                        if (current_page != null)
                        {
                            if (current_page.hasNext())
                                loader.process(new PageLoader(current_page.next));
                            else if (chapter_index != 0)
                                loader.process(new ChapterLoader(chapters.get(--chapter_index), true));
                            else
                                finish();
                        }
                        else
                            finish();
                    }
                }
                return true;
            }

        });
    }

    private class ChapterLoader implements AsyncRunner.Runnable
    {
        private Loader<Chapter> to_load;
        private boolean first_page;

        ChapterLoader(Loader<Chapter> loader, boolean first_page)
        {
            to_load = loader;
            this.first_page = first_page;
        }

        public boolean run()
        {
            PageActivity.current_chapter = to_load.load();
            return (PageActivity.current_chapter != null);
        }

        public void onSuccess()
        {
            if (first_page)
                loader.process(new PageLoader(PageActivity.current_chapter.first_page));
            else
                loader.process(new PageLoader(PageActivity.current_chapter.last_page));
        }

        public void onError()
        {
            finish();
        }

        public boolean retry()
        {
            return (true);
        }

        public Context context()
        {
            return (PageActivity.this);
        }

        public String errorDescription()
        {
            return (getResources().getString(R.string.chapter_loading_error));
        }
    }

    private class PageLoader implements AsyncRunner.Runnable
    {
        private Loader<Page> to_load;

        PageLoader(Loader<Page> loader)
        {
            to_load = loader;
            findViewById(R.id.loading_tag).setVisibility(View.VISIBLE);
        }

        public boolean run()
        {
            PageActivity.current_page = to_load.load();
            return (PageActivity.current_page != null);
        }

        public void onSuccess()
        {
            findViewById(R.id.loading_tag).setVisibility(View.INVISIBLE);
            if (current_page.getPicture() != null)
            {
                ((ScrollView) findViewById(R.id.manga_page_scroll)).fullScroll(ScrollView.FOCUS_UP);
                ((ImageView) findViewById(R.id.manga_page)).setImageDrawable(current_page.getPicture());
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                builder.setTitle(R.string.error)
                        .setMessage(R.string.picture_loading_error)
                        .setPositiveButton(R.string.ok, null);
                builder.create().show();
            }
        }

        public void onError()
        {
            finish();
        }

        public boolean retry()
        {
            return (true);
        }

        public Context context()
        {
            return (PageActivity.this);
        }

        public String errorDescription()
        {
            return (getResources().getString(R.string.page_loading_error));
        }
    }
}
