package vikings.mangareader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import vikings.mangareader.MangaFoxProvider.MangaFoxProvider;
import vikings.mangareader.MangaProvider.MangaProvider;

public class LoadingScreenActivity extends Activity
{
    public void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.loading_screen_layout);

        launchMangaFoxProvider();
    }

    private void launchMangaFoxProvider()
    {
        final MangaProvider provider = new MangaFoxProvider();
        provider.load(new Runnable()
        {//Success
            @Override
            public void run()
            {
                MangaProviderActivity.provider = provider;
                startActivity(new Intent(LoadingScreenActivity.this, MangaProviderActivity.class));
            }
        }, new Runnable()
        {//Failure
            @Override
            public void run()
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoadingScreenActivity.this);
                builder.setTitle(R.string.error)
                        .setMessage(R.string.no_internet_connection)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                launchMangaFoxProvider();
                            }
                        })
                        .setNegativeButton(R.string.offline_mode, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Log.d("launchMangaFoxProvider", "offline mode expected");
                            }
                        });

                builder.create().show();
            }
        });
    }
}
