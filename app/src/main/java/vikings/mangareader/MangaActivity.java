package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

        init();
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
                    manga.load(new Runnable()
                    {//Success
                        @Override
                        public void run()
                        {
                            PageActivity.start(MangaActivity.this, chapter);
                        }
                    }, new Runnable()
                    {//Failure
                        @Override
                        public void run()
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MangaActivity.this);
                            builder.setTitle(R.string.error)
                                    .setMessage(R.string.no_internet_connection)
                                    .setPositiveButton(R.string.ok, null);

                            builder.create().show();
                        }
                    });
                }
            });
        }
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
