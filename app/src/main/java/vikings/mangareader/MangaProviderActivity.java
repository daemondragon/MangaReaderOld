package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.MangaProvider.Manga;
import vikings.mangareader.MangaProvider.MangaProvider;

public class MangaProviderActivity extends Activity
{
    static MangaProvider provider;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_provider_layout);

        init();
    }

    public void init()
    {
        ListView list = (ListView) findViewById(R.id.manga_list);
        if (list != null)
        {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    final Manga manga = provider.mangas().get(position);
                    manga.load(new Runnable()
                        {//Success
                            @Override
                            public void run()
                            {
                                MangaActivity.manga = manga;
                                startActivity(new Intent(MangaProviderActivity.this, MangaActivity.class));
                            }
                        }, new Runnable()
                        {//Failure
                            @Override
                            public void run()
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MangaProviderActivity.this);
                                builder.setTitle(R.string.error)
                                        .setMessage(R.string.no_internet_connection)
                                        .setPositiveButton(R.string.ok, null);

                                builder.create().show();
                            }
                        });
                }
            });
            loadMangasList(list);
        }
    }

    private void loadMangasList(@NonNull ListView manga_list_view)
    {
        List<Manga> mangas = provider.mangas();
        ArrayList<String> mangas_name = new ArrayList<>();
        for (Manga manga : mangas)
        {
            String name = manga.name();
            if (name != null)
                mangas_name.add(name);
        }

        manga_list_view.setAdapter(new ArrayAdapter<>(MangaProviderActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                mangas_name));
    }
}
