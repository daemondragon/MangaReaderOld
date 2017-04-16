package vikings.mangareader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Manga.MangaLoader;
import vikings.mangareader.MangaFoxProvider.MangaFoxNewsLoader;

public class MangaProviderActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<MangaLoader>>
{
    List<MangaLoader> mangas = null;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_provider_layout);

        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.inflateMenu(R.menu.main_toolbar_menu);
        setSupportActionBar(main_toolbar);

        getSupportLoaderManager().initLoader(0, null, this);

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
                    MangaActivity.start(MangaProviderActivity.this, mangas.get(position));
                }
            });
        }
    }

    public Loader<List<MangaLoader>> onCreateLoader(int id, Bundle args)
    {
        Loader<List<MangaLoader>> loader = new MangaFoxNewsLoader(this);
        loader.forceLoad();
        return (loader);
    }

    public void onLoadFinished(Loader<List<MangaLoader>> loader, List<MangaLoader> to_display)
    {
        mangas = to_display;

        ListView manga_list_view = (ListView) findViewById(R.id.manga_list);
        ArrayList<String> mangas_name = new ArrayList<>();
        for (MangaLoader manga : to_display)
        {
            String name = manga.name();
            if (name != null)
                mangas_name.add(name);
        }

        manga_list_view.setAdapter(new ArrayAdapter<>(MangaProviderActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                mangas_name));
    }

    public void onLoaderReset(Loader<List<MangaLoader>> loader)
    {

    }

    @Override // handler for the overflow menu of the app bar
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_bookmark:
                Toast debug = Toast.makeText(getApplicationContext(),"bookmark",Toast.LENGTH_LONG);
                debug.show();
                return true;

            case R.id.action_account:
                debug = Toast.makeText(getApplicationContext(),"log in",Toast.LENGTH_LONG);
                debug.show();
                return true;
            case R.id.action_settings:
                debug = Toast.makeText(getApplicationContext(),"settings",Toast.LENGTH_LONG);
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Totalement normal, je suis en train de casser des trucs.
                // (Beaucoup de truc....)
                /*provider.search(searchView.getQuery().toString(), null, new Runnable() {
                    @Override
                    public void run() {

                        //loadMangasList((ListView) findViewById(R.id.manga_list));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MangaProviderActivity.this);
                        builder.setTitle(R.string.error)
                                .setMessage(R.string.no_internet_connection)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {

                                    }
                                });
                        builder.create().show();
                    }
                });*/
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
}
