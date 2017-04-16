package vikings.mangareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
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

import vikings.mangareader.Manga.ChapterLoader;
import vikings.mangareader.Manga.Manga;

public class MangaActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Manga>
{
    public static void startFrom(Context context)
    {
        if (LoaderSingleton.manga == null)
            return;

        context.startActivity(new Intent(context, MangaActivity.class));
    }

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_layout);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void onDestroy()
    {
        super.onDestroy();
        LoaderSingleton.manga = null;
    }

    public Loader<Manga> onCreateLoader(int id, Bundle args)
    {
        LoaderSingleton.manga.forceLoad();
        return (LoaderSingleton.manga);
    }

    public void onLoadFinished(final Loader<Manga> loader, final Manga to_display)
    {
        if (to_display != null)
        {
            setTextIn((TextView)findViewById(R.id.manga_name), to_display.name());
            setTextIn((TextView)findViewById(R.id.manga_authors), to_display.authors());
            setTextIn((TextView)findViewById(R.id.manga_summary), to_display.summary());
            setTextIn((TextView)findViewById(R.id.manga_rating), to_display.rating() * 5 + " / 5 stars");
            setTextIn((TextView)findViewById(R.id.manga_status), to_display.status() );
            setTextIn((TextView)findViewById(R.id.manga_genres), to_display.genres().toString());

            ((ImageView)findViewById(R.id.manga_cover)).setImageDrawable(to_display.cover());

            ListView chapters_list = (ListView)findViewById(R.id.manga_chapters);
            loadChaptersList(chapters_list, to_display);

            if (chapters_list != null)
            {
                chapters_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        PageActivity.start(MangaActivity.this, to_display.chapters, position);
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
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MangaActivity.this);
            builder.setTitle(R.string.error)
                    .setMessage(R.string.manga_loading_error)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            loader.forceLoad();
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
    }

    public void onLoaderReset(Loader<Manga> loader)
    {

    }

    private void setTextIn(TextView view, String text)
    {
        view.setText(text != null ? text : getResources().getString(R.string.unknown));
    }


    private void loadChaptersList(ListView chapters_list, Manga to_display)
    {
        ArrayList<String> chapters_name = new ArrayList<>();
        for (ChapterLoader chapter : to_display.chapters)
        {
            String name = chapter.name();
            if (name != null)
                chapters_name.add(name);
        }

        chapters_list.setAdapter(new ArrayAdapter<>(MangaActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                chapters_name));
    }
}
