package vikings.mangareader.MangaFox;

import android.graphics.drawable.Drawable;
import android.util.Log;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Page;
import vikings.mangareader.Utils;

class FoxPageLoader extends Loader<Page>
{
    private String url;

    FoxPageLoader(String url)
    {
        this.url = url;
    }

    public Page load()
    {
        String html = Utils.InputStreamToString(Utils.getInputStreamFromURL(url));

        if (html == null || "".equals(html))
            return (null);

        Page page = new Page();
        try
        {
            page.picture = Drawable.createFromStream(
                    Utils.getInputStreamFromURL(Utils.parseUnique(html, "<div class=\"read_img\">", "<img src=\"", "\"")),
                    "page");
        }
        catch (OutOfMemoryError e)
        {
            page.picture = null;
        }

        parsePreviousButton(html, page);
        parseNextButton(html, page);

        return (page);
    }

    private void parsePreviousButton(String html, Page page)
    {
        int end = html.indexOf("class=\"btn prev_page\"");
        if (end != -1)
        {
            int start = html.lastIndexOf("<a href=\"", end);
            String previous_page_end_url = Utils.parseUnique(html.substring(start, end), "<a href=\"", "\"", "\"");
            if (previous_page_end_url != null)
            {
                if (!previous_page_end_url.contains("javascript") && !previous_page_end_url.contains("void") &&
                        !previous_page_end_url.contains("http"))
                    page.previous = new FoxPageLoader(url.substring(0, url.lastIndexOf("/") + 1) + previous_page_end_url);
                else
                    Log.d("parsePreviousButton", "no previous page (javascript or void found in string)");
            }
            else
                Log.d("parsePreviousButton", "can found url");
        }
        else
            Log.d("parsePreviousButton", "previous button not found");
    }

    private void parseNextButton(String html, Page page)
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
                    page.next = new FoxPageLoader(url.substring(0, url.lastIndexOf("/") + 1) + next_page_end_url);
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
