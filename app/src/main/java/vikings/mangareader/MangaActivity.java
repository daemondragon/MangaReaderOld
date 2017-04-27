package vikings.mangareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import vikings.mangareader.Manga.AsyncLoader;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class MangaActivity extends AppCompatActivity implements AsyncLoader.Runnable
{
    public static Loader<Manga> manga;

    private AsyncLoader loader = new AsyncLoader();
    private Manga to_display;

    public static void start(Context context, Loader<Manga> manga)
    {
        MangaActivity.manga = manga;
        context.startActivity(new Intent(context, MangaActivity.class));
    }

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_layout);

        loader.process(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.dl_toolbar);
        toolbar.inflateMenu(R.menu.download_toolbar_menu);
        setSupportActionBar(toolbar);
    }

    public boolean run()
    {
        to_display = manga.load();
        return (to_display != null);
    }

    public void onSuccess()
    {
        setTextIn((TextView)findViewById(R.id.manga_name), to_display.name());
        setTextIn((TextView)findViewById(R.id.manga_authors), to_display.authors());
        setTextIn((TextView)findViewById(R.id.manga_summary), to_display.summary());
        setTextIn((TextView)findViewById(R.id.manga_rating), to_display.rating() * 5 + " / 5 stars");
        setTextIn((TextView)findViewById(R.id.manga_genres), to_display.genres().toString());
        setTextIn((TextView)findViewById(R.id.manga_status), to_display.status() );

        ((ImageView)findViewById(R.id.manga_cover)).setImageDrawable(to_display.cover());

        ListView chapters_list = (ListView)findViewById(R.id.manga_chapters);
        loadChaptersList(chapters_list, to_display);

        if (chapters_list != null)
        {
            chapters_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {PageActivity.start(MangaActivity.this, to_display.chapters, position);
                }
            });
        }

        Switch switcher = (Switch)findViewById(R.id.chapters_summary_switch);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    ViewSwitcher summary_chapters = (ViewSwitcher)findViewById(R.id.chapters_summary);
                    if (!isChecked)
                        summary_chapters.showPrevious();
                    else
                        summary_chapters.showNext();

                }
            });
    }

    public void onError()
    {
        finish();
    }

    public boolean retry()
    {
        return (true);
    }

    public String errorDescription()
    {
        return (getResources().getString(R.string.manga_loading_error));
    }

    public Context context()
    {
        return (this);
    }

    private void setTextIn(TextView view, String text)
    {
        view.setText(text != null ? text : getResources().getString(R.string.unknown));
    }


    private void loadChaptersList(ListView chapters_list, Manga to_display)
    {
        ArrayList<String> chapters_name = new ArrayList<>();

        for (Loader<Chapter> chapter : to_display.chapters)
        {
            String name = chapter.name();
            if (name != null)
                chapters_name.add(name);
        }

        chapters_list.setAdapter(new ArrayAdapter<>(MangaActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                chapters_name));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.download_toolbar_menu, menu);
        return true;
    }
}
