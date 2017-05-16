package vikings.mangareader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class DrawerActivity extends AppCompatActivity
{
    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
    }

    /**
     * init the on click listener for the drawer list not the image button yet
     *  TODO link the image button !!!!!!!
     */
    protected void initDrawer()
    {
        String[] nav_list = getResources().getStringArray(R.array.nav_drawer_list);
        ListView drawer_list = (ListView) findViewById(R.id.drawer_view);
        drawer_list.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,nav_list));
        drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(DrawerActivity.this, MangaProviderActivity.class);
                        //Will clear all previous activity.
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("LOADER", "NEWS");
                        startActivity(intent);
                        break;
                    case 1:
                        (Toast.makeText(view.getContext(),"bookmark",Toast.LENGTH_LONG)).show();
                        break;
                    case 2:
                        intent = new Intent(DrawerActivity.this, MangaProviderActivity.class);
                        intent.putExtra("LOADER", "DATABASE");
                        startActivity(intent);
                        break;
                    case 3:
                        (Toast.makeText(view.getContext(),"settings",Toast.LENGTH_LONG)).show();
                        break;
                    default: (Toast.makeText(view.getContext(),"dafuck",Toast.LENGTH_LONG)).show();
                        break;
                }
            }
        });
    }
}
