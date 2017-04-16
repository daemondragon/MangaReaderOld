package vikings.mangareader;

import vikings.mangareader.Manga.MangaLoader;

/**
 * Is used to pass loader directly from one activity to another, without using
 * serializable or parcelable, that can't use inheritance.
 */
class LoaderSingleton
{
    static MangaLoader manga = null;
}
