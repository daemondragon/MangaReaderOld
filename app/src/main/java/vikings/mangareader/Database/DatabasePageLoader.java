package vikings.mangareader.Database;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import junit.framework.Assert;

import java.util.List;

import vikings.mangareader.Manga.Loader;
import vikings.mangareader.Manga.Page;

class DatabasePageLoader extends Loader<Page>
{
    private List<String> pages_filename;
    private int          index;

    DatabasePageLoader(@NonNull List<String> pages_filename, int index)
    {
        Assert.assertTrue("Out of range index", index >= 0 && index < pages_filename.size());
        this.pages_filename = pages_filename;
        this.index = index;
    }

    public Page load()
    {
        Page page = new Page();
        page.previous = (index > 0 ? new DatabasePageLoader(pages_filename, index - 1) : null);
        page.next = (index + 1 < pages_filename.size() ? new DatabasePageLoader(pages_filename, index + 1) : null);
        page.picture = Drawable.createFromPath(pages_filename.get(index));

        return (page);
    }
}
