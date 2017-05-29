package vikings.mangareader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import vikings.mangareader.Database.DatabaseMangaRemover;
import vikings.mangareader.Database.DatabaseMangaSaver;
import vikings.mangareader.Manga.AsyncRunner;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;

public class DownloadOrRemoveActivity extends AppCompatActivity
{
    static final String DOWNLOAD = "download";
    private boolean download;
    static Manga manga;

    boolean[] checked;

    DatabaseMangaSaver saver;

    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.download_or_remove_layout);

        download = getIntent().getBooleanExtra(DOWNLOAD, true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.download_or_remove_toolbar);
        if (toolbar != null)
        {
            toolbar.inflateMenu(R.menu.download_or_remove_menu);
            setSupportActionBar(toolbar);
        }
        else
            Log.d("DownloadOrRemoveAct", "can't find toolbar");

        final ListView list_view = (ListView) findViewById(R.id.checked_list);
        checked = new boolean[manga.chapters.size()];
        for (int i = 0; i < checked.length; ++i)
            checked[i] = false;

        list_view.setAdapter(new CheckedTextAdapter(this, manga.chapters));

        findViewById(R.id.select_all).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox c_v = (CheckBox) v;
                for (int i = 0; i < checked.length; ++i)
                    checked[i] = c_v.isChecked();

                list_view.invalidateViews();//Used to refresh on-screen item
            }
        });

        saver = new DatabaseMangaSaver(this);
    }

    public boolean onPrepareOptionsMenu (Menu menu)
    {
        MenuItem item = menu.findItem(R.id.download_or_remove);
        if (item != null)
        {
            item.setTitle(getResources().getString(download ? R.string.download : R.string.remove));
            item.setIcon(getResources().getDrawable(download ? R.drawable.ic_download : R.drawable.ic_remove));
        }
        else
            Log.d("DownloadOrRemoveAct", "can't find menu item");
        return (true);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.download_or_remove_menu, menu);
        return (true);
    }

    public boolean onOptionsItemSelected (MenuItem item)
    {
        if (item.getItemId() == R.id.download_or_remove)
        {
            if (download)
            {
                List<Loader<Chapter>> list = new ArrayList<>();
                for (int i = 0; i < manga.chapters.size(); ++i)
                    if (checked[i])
                        list.add(manga.chapters.get(i));

                saver.save(manga, list);
                Log.d("DownloadOrRemoveAct", "Download");
            }
            else {
                Log.d("DownloadOrRemoveAct", "Remove");
            }
            return (true);
        }
        else
            return (false);
    }

    private class CheckedTextAdapter extends BaseAdapter
    {
        private Context context;
        private List<Loader<Chapter>> names;

        CheckedTextAdapter(Context context,@NonNull List<Loader<Chapter>> chapters)
        {
            this.context = context;
            names = chapters;
        }

        public int getCount() {
            return (names.size());
        }

        public Object getItem(int position) {
            return (names.get(position));
        }

        public long getItemId(int position) {
            return (position);
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checked_text_layout, parent, false);

                holder.text_view = (CheckedTextView) convertView.findViewById(R.id.checked_text_view);
                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder) convertView.getTag();

            holder.text_view.setText(names.get(position).name());
            holder.text_view.setChecked(checked[position]);
            holder.text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckedTextView c_v = (CheckedTextView)v;
                    c_v.toggle();
                    checked[position] = c_v.isChecked();
                }
            });
            return (convertView);
        }

        class ViewHolder
        {
            CheckedTextView text_view;
        }
    }
}
