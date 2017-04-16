package vikings.mangareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.List;

import vikings.mangareader.Manga.ChapterLoader;
import vikings.mangareader.Manga.PageLoader;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Page;

public class PageActivity extends AppCompatActivity
{
    private static List<ChapterLoader> chapters;
    private static int chapter_index = -1;

    private Page current_page = null;
    private GestureDetector detector;

    private static final int chapter_manager = 0;
    private static final int page_manager = 1;

    public static void start(Context context, List<ChapterLoader> chapters, int chapter_index)
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

        getSupportLoaderManager().initLoader(chapter_manager, getIntent().getExtras(),
                new ChapterLoaderManager(chapters.get(chapter_index)));

        init();
    }

    public boolean dispatchTouchEvent(MotionEvent e)
    {
        detector.onTouchEvent(e);
        return (super.dispatchTouchEvent(e));
    }

    public void init()
    {
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
        {
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                float offsetX = event1.getX() - event2.getX();
                float offsetY = event1.getY() - event2.getY();

                if (Math.abs(offsetX) > Math.abs(offsetY) * 2)
                {
                    if (offsetX < 0)
                    {//previous
                        if (current_page != null)
                        {
                            if (current_page.hasPrevious())
                                getSupportLoaderManager().restartLoader(page_manager, null, new PageLoaderManager(current_page.previous));
                            else
                            {
                                if (chapter_index + 1 < chapters.size())
                                    getSupportLoaderManager().restartLoader(chapter_manager, null,
                                            new ChapterLoaderManager(chapters.get(++chapter_index)));
                                else
                                    finish();
                            }
                        }
                        else
                            finish();

                    }
                    else
                    {
                        if (current_page != null)
                        {
                            if (current_page.hasNext())
                                getSupportLoaderManager().restartLoader(page_manager, null, new PageLoaderManager(current_page.next));
                            else
                            {
                                if (chapter_index != 0)
                                    getSupportLoaderManager().restartLoader(chapter_manager, null,
                                            new ChapterLoaderManager(chapters.get(--chapter_index)));
                                else
                                    finish();
                            }
                        }
                        else
                            finish();
                    }
                }
                return true;
            }

        });
    }

    private class ChapterLoaderManager implements LoaderManager.LoaderCallbacks<Chapter>
    {
        ChapterLoader to_load = null;

        ChapterLoaderManager(ChapterLoader to_load)
        {
            this.to_load = to_load;
        }

        public Loader<Chapter> onCreateLoader(int id, Bundle args)
        {
            to_load.forceLoad();
            return (to_load);
        }

        public void onLoadFinished(final Loader<Chapter> loader, Chapter to_display)
        {
            if (to_display != null)
                getSupportLoaderManager().restartLoader(page_manager, null, new PageLoaderManager(to_display.first_page));
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                builder.setTitle(R.string.error)
                        .setMessage(R.string.chapter_provider_loading_error)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loader.forceLoad();
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.create().show();
            }
        }

        public void onLoaderReset(Loader<Chapter> loader)
        {

        }
    }

    private class PageLoaderManager implements LoaderManager.LoaderCallbacks<Page>
    {
        PageLoader to_load = null;

        PageLoaderManager(PageLoader to_load)
        {
            this.to_load = to_load;
        }

        public Loader<Page> onCreateLoader(int id, Bundle args)
        {
            to_load.forceLoad();
            findViewById(R.id.loading_tag).setVisibility(View.VISIBLE);
            return (to_load);
        }

        public void onLoadFinished(final Loader<Page> loader, Page to_display)
        {
            if (to_display != null)
            {
                current_page = to_display;
                findViewById(R.id.loading_tag).setVisibility(View.INVISIBLE);
                if (to_display.getPicture() != null)
                {
                    ((ScrollView) findViewById(R.id.manga_page_scroll)).fullScroll(ScrollView.FOCUS_UP);
                    ((ImageView) findViewById(R.id.manga_page)).setImageDrawable(to_display.getPicture());
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
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                builder.setTitle(R.string.error)
                        .setMessage(R.string.page_loading_error)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loader.forceLoad();
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.create().show();
            }
        }

        public void onLoaderReset(Loader<Page> loader)
        {

        }
    }
}
