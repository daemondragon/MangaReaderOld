package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vikings.mangareader.MangaProvider.Chapter;
import vikings.mangareader.MangaProvider.Page;

public class PageActivity extends Activity
{
    static Chapter chapter;

    private Page page;

    private GestureDetector detector;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.page_layout);

        init();

        loadChapter(chapter, new Runnable()
        {
            @Override
            public void run()
            {
                goToPage(chapter.getFirstPage());
            }
        });
    }

    public boolean onTouchEvent(MotionEvent e)
    {
        detector.onTouchEvent(e);
        return (super.onTouchEvent(e));
    }

    public void init()
    {
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener()
        {
            public boolean onFling(MotionEvent event1, MotionEvent event2,
                                   float velocityX, float velocityY) {
                float offsetX = event1.getX() - event2.getX();
                float offsetY = event1.getY() - event2.getY();

                Log.d("onFling", "fling detected! " + offsetX + ";" + offsetY);
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

    private void loadChapter(final Chapter chapter, final Runnable after)
    {
        chapter.load(new Runnable()
        {
            @Override
            public void run()
            {
                if (PageActivity.chapter != null && PageActivity.chapter != chapter)
                    PageActivity.chapter.unload();

                PageActivity.chapter = chapter;
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
                                loadChapter(chapter, after);
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
        ((ImageView)findViewById(R.id.manga_page)).setImageDrawable(picture);
    }

    private void goToPage(final Page page)
    {
        if (page != null)
        {
            final TextView loading = (TextView)findViewById(R.id.loading_tag);
            if (loading != null)
                loading.setVisibility(View.VISIBLE);

            page.load(new Runnable()
            {//Success
                @Override
                public void run()
                {
                    if (PageActivity.this.page != null)
                        PageActivity.this.page.unload();

                    PageActivity.this.page = page;
                    setPicture(page.getPicture());

                    if (loading != null)
                        loading.setVisibility(View.INVISIBLE);
                }
            }, new Runnable()
            {//Failure
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
                                    goToPage(page);
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
        {
            Log.d("goToPage", "page is null, finishing activity");
            finish();
        }
    }

    private void goToNextPage()
    {
        if (page.hasNext())
            goToPage(page.next());
        else
        {
            final Chapter next_chapter =  chapter.getNextChapter();
            if (next_chapter != null)
            {
                loadChapter(next_chapter, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        goToPage(next_chapter.getFirstPage());
                    }
                });
            }
            else
                finish();
        }
    }

    private void goToPreviousPage()
    {
        if (page.hasPrevious())
            goToPage(page.previous());
        else
        {
            final Chapter previous_chapter =  chapter.getPreviousChapter();
            if (previous_chapter != null)
            {
                loadChapter(previous_chapter, new Runnable()
                {
                    @Override
                    public void run()
                    {
                        goToPage(previous_chapter.getLastPage());
                    }
                });
            }
            else
                finish();
        }
    }
}
