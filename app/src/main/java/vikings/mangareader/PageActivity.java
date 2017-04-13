package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import vikings.mangareader.MangaProvider.Chapter;
import vikings.mangareader.MangaProvider.Page;

public class PageActivity extends Activity
{
    private static List<Chapter> chapters;
    private static int chapter_index = -1;

    private Page current_page;
    private GestureDetector detector;

    public static void start(Context context, List<Chapter> chapters, int chapter_index)
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

        loadChapter(chapter_index, new Runnable()
        {
            @Override
            public void run()
            {
                goToPage(chapters.get(chapter_index).getFirstPage());
            }
        });
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
                        goToPreviousPage();
                    else
                        goToNextPage();
                }
                return true;
            }

        });
    }

    private void loadChapter(final int new_index, final Runnable after)
    {
        if (new_index >= 0 && new_index < chapters.size())
        {
            Chapter to_load = chapters.get(new_index);
            to_load.load(new Runnable()
            {
                @Override
                public void run()
                {
                    if (chapter_index >= 0 && chapter_index < chapters.size() && chapter_index != new_index)
                        chapters.get(chapter_index).unload();

                    chapter_index = new_index;
                    after.run();
                }
            }, new Runnable()
            {
                @Override
                public void run()
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.no_internet_connection)
                            .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    loadChapter(new_index, after);
                                }
                            })
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener()
                            {

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    finish();
                                }
                            });

                    builder.create().show();
                }
            });
        }
        else
            finish();
    }

    private void loadPage(final Page page, final Runnable success)
    {
        if (page == null)
            return;

        page.load(success, new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PageActivity.this);
                builder.setTitle(R.string.error)
                        .setMessage(R.string.no_internet_connection)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                loadPage(page, success);
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                finish();
                            }
                        });
                builder.create().show();
            }
        });
    }

    private void setPicture(Drawable picture)
    {
        if (picture != null)
        {
            ((ScrollView) findViewById(R.id.manga_page_scroll)).fullScroll(ScrollView.FOCUS_UP);
            ((ImageView) findViewById(R.id.manga_page)).setImageDrawable(picture);
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

    private void goToPage(final Page page)
    {
        if (page != null)
        {
            final TextView loading = (TextView)findViewById(R.id.loading_tag);
            if (loading != null)
                loading.setVisibility(View.VISIBLE);

            loadPage(page, new Runnable() {
                @Override
                public void run()
                {
                    current_page = page;
                    setPicture(page.getPicture());

                    if (loading != null)
                        loading.setVisibility(View.INVISIBLE);
                }
            });
        }
        else
        {
            Log.d("goToPage", "page is null, finishing activity");
            finish();
        }
    }

    private void goToNextPage()
    {
        if (current_page.hasNext())
            goToPage(current_page.next());
        else
        {
            loadChapter(chapter_index - 1, new Runnable()
            {
                @Override
                public void run()
                {
                    goToPage(chapters.get(chapter_index).getFirstPage());
                }
            });
        }
    }

    private void goToPreviousPage()
    {
        if (current_page.hasNext())
            goToPage(current_page.next());
        else
        {
            loadChapter(chapter_index + 1, new Runnable()
            {
                @Override
                public void run()
                {
                    goToPage(chapters.get(chapter_index).getLastPage());
                }
            });
        }
    }
}
