package vikings.mangareader.Manga;

public class AsyncLoader
{
    interface Runnable
    {
        /**
         * Try running the runnable.
         * @return true if everything ran without error, false otherwise
         */
        boolean run();

        /**
         * Called if the runnable ran without error.
         */
        void onSuccess();

        /**
         * Called if an error occurred while run() is called.
         * @return true if the runnable have to be ran again, false otherwise
         */
        boolean onError();
    }
}
