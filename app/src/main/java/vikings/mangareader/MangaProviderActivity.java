package vikings.mangareader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
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
import java.util.Stack;

import vikings.mangareader.Database.DatabaseMangasListLoader;
import vikings.mangareader.Manga.AsyncRunner;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;
import vikings.mangareader.MangaFox.MangaFoxNewsLoader;
import vikings.mangareader.MangaFox.MangaFoxSearchLoader;

public class MangaProviderActivity extends AppCompatActivity implements AsyncRunner.Runnable
{
    static Stack<Loader<List<Loader<Manga>>>> mangas_providers = new Stack<>();

    private List<Loader<Manga>> mangas = null;

    AsyncRunner loader = new AsyncRunner();



    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.manga_provider_layout);

        Toolbar main_toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        main_toolbar.inflateMenu(R.menu.main_toolbar_menu);
        setSupportActionBar(main_toolbar);

        init_drawer();

        if (mangas_providers.empty())
            mangas_providers.add(new MangaFoxNewsLoader());

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
                    MangaActivity.start(MangaProviderActivity.this, mangas.get(position));
                }
            });
        }
    }

    /**
     * init the on click listener for the drawer list not the image button yet
     *  !!!!!! to do link the image button !!!!!!!
     */
    public void init_drawer()
    {
        String[] nav_list = getResources().getStringArray(R.array.nav_drawer_list);
        ListView drawer_list = (ListView) findViewById(R.id.drawer_view);
        drawer_list.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,nav_list));
        drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0 :
                        (Toast.makeText(view.getContext(),"home",Toast.LENGTH_LONG)).show();
                        break;
                    case 1 :
                        (Toast.makeText(view.getContext(),"bookmark",Toast.LENGTH_LONG)).show();
                        break;
                    case 2 :
                        (Toast.makeText(view.getContext(),"downloads",Toast.LENGTH_LONG)).show();
                        break;
                    case 3 :
                        (Toast.makeText(view.getContext(),"settings",Toast.LENGTH_LONG)).show();
                        break;
                    default: (Toast.makeText(view.getContext(),"dafuck",Toast.LENGTH_LONG)).show();
                        break;
                }
            }
        });
    }

    public boolean run()
    {
        mangas = mangas_providers.peek().load();
        return (mangas != null);
    }

    public void onSuccess()
    {
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
                Toast debug = Toast.makeText(getApplicationContext(),"advanced search",Toast.LENGTH_LONG);
                debug.show();
                mangas_providers.add(new DatabaseMangasListLoader(context()));
                startActivity(new Intent(MangaProviderActivity.this, MangaProviderActivity.class));
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
            public boolean onQueryTextSubmit(String query) {
                mangas_providers.add(new MangaFoxSearchLoader(query));
                startActivity(new Intent(MangaProviderActivity.this, MangaProviderActivity.class));
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
