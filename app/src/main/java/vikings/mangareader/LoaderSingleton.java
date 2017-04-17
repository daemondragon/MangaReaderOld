package vikings.mangareader;

import android.support.v4.content.AsyncTaskLoader;

import java.util.List;
import java.util.Stack;

import vikings.mangareader.Manga.MangaLoader;

/**
 * Is used to pass loader directly from one activity to another, without using
 * serializable or parcelable, that can't use inheritance.
 */
class LoaderSingleton
{
    static Stack<AsyncTaskLoader<List<MangaLoader>>> provider = new Stack<>();
    static MangaLoader manga = null;
}
