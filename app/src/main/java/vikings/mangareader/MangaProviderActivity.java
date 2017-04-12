package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.MangaProvider.Manga;
import vikings.mangareader.MangaProvider.MangaProvider;

public class MangaProviderActivity extends AppCompatActivity
{
    static MangaProvider provider;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_provider_layout);

        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.inflateMenu(R.menu.main_toolbar_menu);
        setSupportActionBar(main_toolbar);


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

    @Override // handler for the overflow menu of the app bar
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_bookmark:
                Toast debug = Toast.makeText(getApplicationContext(),"pute pute pute bookmark",Toast.LENGTH_LONG);
                debug.show();
                return true;

            case R.id.action_account:
                debug = Toast.makeText(getApplicationContext(),"pdpdpd log in",Toast.LENGTH_LONG);
                debug.show();
                return true;
            case R.id.action_settings:
                debug = Toast.makeText(getApplicationContext(),"suce suce suce settings",Toast.LENGTH_LONG);
                debug.show();

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override // link the menu to the toolbar and implement the search function
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return true;
    }
}
