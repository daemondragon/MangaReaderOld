package vikings.mangareader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Stack;

import vikings.mangareader.Database.DatabaseMangaLoader;
import vikings.mangareader.Database.DatabaseMangasListLoader;
import vikings.mangareader.Manga.AsyncRunner;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;
import vikings.mangareader.MangaFox.FoxMangaLoader;
import vikings.mangareader.MangaFox.MangaFoxNewsLoader;
import vikings.mangareader.MangaFox.MangaFoxSearchLoader;

public class MangaProviderActivity extends DrawerActivity implements AsyncRunner.Runnable
{
    static final String LOADER = "LOADER";
    static final String FOX_LOADER = "FOX";
    static final String DATABASE_LOADER = "DB";

    static final String SEARCH_KEY = "SEARCH";
    static final String SEARCH_QUERY = "SEARCH_QUERY";

    private String loader_name;

    private List<Loader<Manga>> mangas = null;

    private AsyncRunner loader = new AsyncRunner();

    SwipeRefreshLayout refresh;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_provider_layout);

        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.inflateMenu(R.menu.main_toolbar_menu);
        setSupportActionBar(main_toolbar);

        refresh = (SwipeRefreshLayout) findViewById(R.id.main_page);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loader.process(MangaProviderActivity.this);
            }
        });

        initDrawer();

        loader_name = getIntent().getStringExtra(LOADER);
        loader_name = (loader_name == null ? "" : loader_name);

        init();
    }

    public void onResume()
    {
        super.onResume();
        loader.process(this);
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
                    Intent intent = new Intent(MangaProviderActivity.this, MangaActivity.class);
                    intent.putExtra(MangaActivity.MANGA_NAME, mangas.get(position).name());
                    if (DATABASE_LOADER.equals(loader_name))
                        intent.putExtra(MangaActivity.MANGA_PATH, ((DatabaseMangaLoader)mangas.get(position)).manga_path);
                    else
                        intent.putExtra(MangaActivity.MANGA_URL, ((FoxMangaLoader)mangas.get(position)).url);

                    startActivity(intent);
                }
            });
        }
    }

    public boolean run()
    {
        switch (loader_name)
        {
            case DATABASE_LOADER:
                if (getIntent().getBooleanExtra(SEARCH_KEY, false))
                    ;//Search in database.
                else
                    mangas = new DatabaseMangasListLoader(this).load();

                break;
            case FOX_LOADER:
            default:
                if (getIntent().getBooleanExtra(SEARCH_KEY, false))
                    mangas = new MangaFoxSearchLoader(getIntent().getStringExtra(SEARCH_QUERY)).load();
                else
                    mangas = new MangaFoxNewsLoader().load();

                break;
        }
        return (mangas != null);
    }

    public void onSuccess()
    {
        if (refresh != null)
            refresh.setRefreshing(false);

        ListView manga_list_view = (ListView) findViewById(R.id.manga_list);
        ArrayList<String> mangas_name = new ArrayList<>();
        for (Loader<Manga> manga : mangas) {
            String name = manga.name();
            if (name != null)
                mangas_name.add(name);
        }

        manga_list_view.setAdapter(new ArrayAdapter<>(MangaProviderActivity.this,
                R.layout.support_simple_spinner_dropdown_item,
                mangas_name));
    }

    public void onError()
    {
    }

    public boolean retry()
    {
        return (true);
    }

    public String errorDescription()
    {
        return (getResources().getString(R.string.manga_provider_loading_error));
    }

    public Context context()
    {
        return (this);
    }

    @Override // handler for the overflow menu of the app bar
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                return true;


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
            public boolean onQueryTextSubmit(String query)
            {
                Intent intent = new Intent(MangaProviderActivity.this, MangaProviderActivity.class);
                intent.putExtra(LOADER, loader_name);
                intent.putExtra(SEARCH_KEY, true);
                intent.putExtra(SEARCH_QUERY, query);
                startActivity(intent);
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
