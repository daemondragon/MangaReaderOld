package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import java.util.List;

import vikings.mangareader.MangaProvider.Chapter;
import vikings.mangareader.MangaProvider.Manga;

public class MangaActivity extends Activity
{
    static Manga manga;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_layout);

        loadManga();
    }

    public void onDestroy()
    {
        if (manga != null)
            manga.unload();

        super.onDestroy();
    }

    private void loadManga()
    {
        if (manga == null)
            finish();

        manga.load(new Runnable()
        {
            @Override
            public void run()
            {
                init();
            }
        }, new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MangaActivity.this);
                builder.setTitle(R.string.error)
                        .setMessage(R.string.no_internet_connection)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                loadManga();
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

    private void init()
    {
        ((TextView)findViewById(R.id.manga_name)).setText(manga.name());
        ((TextView)findViewById(R.id.manga_authors)).setText(manga.authors());
        ((TextView)findViewById(R.id.manga_summary)).setText(manga.summary());
        ((TextView)findViewById(R.id.manga_genres)).setText(manga.genres().toString());
        ((ImageView)findViewById(R.id.manga_cover)).setImageDrawable(manga.cover());

        ListView chapters_list = (ListView)findViewById(R.id.manga_chapters);
        loadChaptersList(chapters_list);

        if (chapters_list != null)
        {
            chapters_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    final Chapter chapter = manga.chapters().get(position);
                    PageActivity.start(MangaActivity.this, chapter);
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

    private void loadChaptersList(ListView chapters_list)
    {
        List<Chapter> chapters = manga.chapters();
        ArrayList<String> chapters_name = new ArrayList<>();
        for (Chapter chapter : chapters)
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
