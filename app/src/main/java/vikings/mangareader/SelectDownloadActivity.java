package vikings.mangareader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.CheckBox;

import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;

import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;


public class SelectDownloadActivity extends AppCompatActivity {

    ListView dl_list;
    static Manga manga_to_dl;

    public static void start(Context context, Manga manga)
    {
        manga_to_dl = manga;
        context.startActivity(new Intent(context, SelectDownloadActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_download);

        dl_list = (ListView) findViewById(R.id.select_download);

        ArrayList<String> chapters_name = new ArrayList<>();
        for (Loader<Chapter> chapter : manga_to_dl.chapters)
        {
            String name = chapter.name();
            if (name != null)
                chapters_name.add(name);
        }

        final DownloadAdaptor dl_adapter = new DownloadAdaptor(this,chapters_name);

        dl_list.setAdapter(dl_adapter);


        dl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CheckedTextView)dl_adapter.getView(position,null,dl_list)).toggle();
            }
        });

    }

}


 class DownloadAdaptor extends BaseAdapter{

     private ArrayList<String> chapter_list;
     private Context c;

     public DownloadAdaptor(Context c, ArrayList<String> list)
     {
         this.c = c;
         chapter_list = list;
     }
     @Override
     public int getCount() {
         return chapter_list.size();
     }

     @Override
     public Object getItem(int position) {
         return chapter_list.get(position);
     }

     @Override
     public long getItemId(int position) {
         return position;
     }

     /**
      * create the checkable view from the string
      * @param position index of the item
      * @param convertView checkable view should be null here
      * @param parent the listview used to get context
      * @return the checkable view
      */
     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         CheckBox convert = new CheckBox(c);

         convert.setText(chapter_list.get(position));
         convert.setChecked(false);
         
         //LayoutInflater li = LayoutInflater.from(c);
         //convert= (CheckBox)  (li.inflate(R.layout.support_simple_spinner_dropdown_item,parent));
         //ArrayAdapter<String> a = new ArrayAdapter<String>(c,R.layout.support_simple_spinner_dropdown_item,chapter_list);

         //return a.getView(position,convert,parent);
         return convert;

     }
 }