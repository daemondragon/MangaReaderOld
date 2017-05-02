package vikings.mangareader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.widget.CheckBox;

import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ChoiceFormat;
import java.util.ArrayList;

import vikings.mangareader.Database.DatabaseMangaSaver;
import vikings.mangareader.Manga.Chapter;
import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Manga;


public class SelectDownloadActivity extends AppCompatActivity {

    ListView dl_list;
    static Manga manga_to_dl;
    DownloadAdaptor dl_adapter;
    ArrayList<String> chapters_name;

    public static void start(Context context, Manga manga)
    {
        manga_to_dl = manga;
        context.startActivity(new Intent(context, SelectDownloadActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_download);

        dl_list = (ListView) findViewById(R.id.select_dl);

        chapters_name = new ArrayList<>();
        for (Loader<Chapter> chapter : manga_to_dl.chapters)
        {
            String name = chapter.name();
            if (name != null)
                chapters_name.add(name);
        }


        dl_adapter = new DownloadAdaptor(this,chapters_name,manga_to_dl);

        dl_list.setAdapter(dl_adapter);
        dl_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        for(int k =0; k< dl_list.getCount();k++)
            dl_list.setItemChecked(k,false);

        dl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((CheckBox)dl_adapter.getView(position,null,dl_list)).toggle();
            }
        });

        CheckBox select_all = (CheckBox)findViewById(R.id.select_all);
        select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               for(int i =0 ; i< dl_list.getCount();i++)
                   dl_list.setItemChecked(i,isChecked);
            }
        });


    }
    public void start_download(View view){
        Toast.makeText(view.getContext(),"Download starting",Toast.LENGTH_SHORT).show();
       ArrayList<Loader<Chapter>> temp = new ArrayList<>();


        for (int i = 0; i < dl_list.getAdapter().getCount(); i++) {
                if ( dl_list.isItemChecked(i) ) {
                    temp.add((Loader<Chapter>)dl_adapter.getItem(i));
                }
        }
        DatabaseMangaSaver saver = new DatabaseMangaSaver(view.getContext());
        saver.save(manga_to_dl, temp);
    }

}


 class DownloadAdaptor extends BaseAdapter{

     private ArrayList<String> chapter_list;
     private Context c;
     private Manga to_dl;

     public DownloadAdaptor(Context c, ArrayList<String> list,Manga m)
     {
         this.c = c;
         chapter_list = list;
         to_dl = m;
     }
     @Override
     public boolean hasStableIds() {
         return true;
     }
     @Override
     public int getCount() {
         return chapter_list.size();
     }

     @Override
     public Object getItem(int position) {
         return to_dl.chapters.get(position);
     }

     @Override
     public long getItemId(int position) {
         return position;
     }

     /**
      * create the checkable view from the string
      * @param position index of the item
      * @param convertView checkable view should be null here
      * @param parent the list view used to get context
      * @return the checkable view
      */
     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         CheckBox convert = new CheckBox(c);
         final ListView listView = (ListView)parent;
         final int pos = position;
         convert.setText(chapter_list.get(position));


         convert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 listView.setItemChecked(pos, isChecked);
             }
         });


         //LayoutInflater li = LayoutInflater.from(c);
         //convert= (CheckBox)  (li.inflate(R.layout.support_simple_spinner_dropdown_item,parent));
         //ArrayAdapter<String> a = new ArrayAdapter<String>(c,R.layout.support_simple_spinner_dropdown_item,chapter_list);

         //return a.getView(position,convert,parent);
         return convert;

     }


 }