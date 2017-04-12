package vikings.mangareader.MangaFoxProvider;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import vikings.mangareader.MangaProvider.Page;

class FoxPage extends Page
{
    private String url;

    FoxPage(String url)
    {
        this.url = url;
    }

    public void load(@Nullable final Runnable success, @Nullable final Runnable error)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Handler handler = new Handler(Looper.getMainLooper());
                if (parsePage(Utils.InputStreamToString(Utils.getInputStreamFromURL(url))))
                    handler.post(success);
                else
                    handler.post(error);

            }
        }).start();
    }

    public void unload()
    {
    }

    private boolean parsePage(String html)
    {
        if (html == null)
            return (false);

        try
        {
            picture = Drawable.createFromStream(
                    Utils.getInputStreamFromURL(Utils.parseUnique(html, "<div class=\"read_img\">", "<img src=\"", "\"")),
                    "page");
        }
        catch (OutOfMemoryError e)
        {
            picture = null;
        }

        parseNextButton(html);
        parsePreviousButton(html);

        return (true);
    }

    private void parsePreviousButton(String html)
    {
        int end = html.indexOf("class=\"btn prev_page\"");
        if (end != -1)
        {
            int start = html.lastIndexOf("<a href=\"", end);
            String previous_page_end_url = Utils.parseUnique(html.substring(start, end), "<a href=\"", "\"", "\"");
            if (previous_page_end_url != null)
            {
                if (!previous_page_end_url.contains("javascript") && !previous_page_end_url.contains("void"))
                    previous = new FoxPage(url.substring(0, url.lastIndexOf("/") + 1) + previous_page_end_url);
                else
                    Log.d("parsePreviousButton", "no previous page (javascript or void found in string)");
            }
            else
                Log.d("parsePreviousButton", "can found url");
        }
        else
            Log.d("parsePreviousButton", "previous button not found");
    }

    private void parseNextButton(String html)
    {
        int end = html.indexOf("class=\"btn next_page\"");
        if (end != -1)
        {
            int start = html.lastIndexOf("<a href=\"", end);
            String next_page_end_url = Utils.parseUnique(html.substring(start, end), "<a href=\"", "\"", "\"");
            if (next_page_end_url != null)
            {
                if (!next_page_end_url.contains("javascript") && !next_page_end_url.contains("void") &&
                        !next_page_end_url.contains("http"))
                {
                    next = new FoxPage(url.substring(0, url.lastIndexOf("/") + 1) + next_page_end_url);
                }
                else
                    Log.d("parseNextButton", "no next page (javascript or void found in string)");
            }
            else
                Log.d("parseNextButton", "can found url");
        }
        else
            Log.d("parseNextButton", "next button not found");
    }
}
