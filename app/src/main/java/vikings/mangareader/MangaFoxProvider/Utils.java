package vikings.mangareader.MangaFoxProvider;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Utils
{
    static InputStream getInputStreamFromURL(String url)
    {
        try
        {
            Log.d("getInputStreamFromURL", url);
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            return (connection.getInputStream());
        }
        catch (Exception e)
        {
            Log.d("getInputStreamFromURL", e.toString());
            return (null);
        }
    }

    static String InputStreamToString(InputStream in)
    {
        if (in == null)
            return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder string = new StringBuilder();
        try
        {
            for (String line; (line = reader.readLine()) != null; )
                string.append(line);
            return (string.toString());
        }
        catch (Exception e)
        {
            Log.d("toString", "error while reading input stream");
            return (null);
        }
    }

    static String fromHtmlString(String str)
    {
        return (str
                .replaceAll("<br( )*/>", "\n")
                .replaceAll("&quot;", "\""));
    }

    static String parse(String to_search, String distinctive_token, String start, String end)
    {
        int start_info = to_search.indexOf(distinctive_token);
        if (start_info != -1)
        {
            start_info = to_search.indexOf(start, start_info - 1);
            if (start_info != -1)
            {
                start_info += start.length();
                int end_info =  to_search.indexOf(end, start_info);
                if (end_info != -1)
                    return (to_search.substring(start_info, end_info));
            }
        }
        return (null);
    }
}
