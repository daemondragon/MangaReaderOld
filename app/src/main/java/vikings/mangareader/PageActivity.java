package vikings.mangareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.List;

import vikings.mangareader.Manga.AsyncLoader;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Page;

public class PageActivity extends AppCompatActivity
{
    private boolean retry;

    private static List<Loader<Chapter>> chapters;
    private static int chapter_index = -1;

    private Page current_page = null;
    private Chapter current_chapter = null;
    private GestureDetector detector;

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

    public void loadChapter(Loader<Chapter> loader)
    {
        final Chapter to_load = loader.load();
        current_chapter = to_load;
        if (to_load == null)
        {
            (new Handler(getMainLooper())).post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.chapter_loading_error)
                            .setPositiveButton(R.string.retry, null)
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        retry = false;
                                        finish();
                                    }
                                });
                    builder.create().show();
                }
            });
        }
    }

    public void loadPage(Loader<Page> loader)
    {
        final Page to_load = loader.load();
        current_page = to_load;
        (new Handler(getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                if (to_load != null)
                {
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
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.chapter_loading_error)
                            .setPositiveButton(R.string.retry, null)
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    retry = false;
                                    finish();
                                }
                            });
                    builder.create().show();
                }
            }
        });
    }

    public void init()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadChapter(chapters.get(chapter_index));
                loadPage(current_chapter.first_page);
            }
        }).start();
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
        {
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                final float offsetX = event1.getX() - event2.getX();
                float offsetY = event1.getY() - event2.getY();

                findViewById(R.id.loading_tag).setVisibility(View.INVISIBLE);

                if (Math.abs(offsetX) > Math.abs(offsetY) * 2)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (offsetX < 0)
                            {//previous
                                if (current_page != null)
                                {
                                    if (current_page.hasPrevious())
                                        loadPage(current_page.previous);
                                    else
                                    {
                                        if (chapter_index + 1 < chapters.size())
                                        {
                                            loadChapter(chapters.get(++chapter_index));
                                            loadPage(current_chapter.last_page);
                                        }
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
                                        loadPage(current_page.next);
                                    else
                                    {
                                        if (chapter_index != 0)
                                        {
                                            loadChapter(chapters.get(--chapter_index));
                                            loadPage(current_chapter.first_page);
                                        }
                                        else
                                            finish();
                                    }
                                }
                                else
                                    finish();
                            }
                        }
                    }).start();
                }
                return true;
            }

        });
    }
}
