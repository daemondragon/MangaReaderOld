package vikings.mangareader.Manga;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import vikings.mangareader.R;

public class AsyncRunner
{
    private BlockingDeque<Runnable> transfer = new LinkedBlockingDeque<>();
    private boolean process_runnable;

    public interface Runnable
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
         * Will only be called if ALL retry have failed (will not be called between retry)
         */
        void onError();

        /**
         * Tell if the runnable want to be retried if an error occurred.
         * @return true if the runnable want to be reran.
         */
        boolean retry();

        /**
         * If an error occurred and retry return true, the alertDialog will show this string to tell what happened
         * @return the string that will be showed.
         */
        String errorDescription();

        /**
         * Get the context of the runnable, is used for the AlertDialog.
         * If retry always return false, the function can return null.
         * @return the context of the runnable.
         */
        Context context();
    }

    /**
     * Create a new loader.
     * Start a new thread.
     */
    public AsyncRunner()
    {
        process_runnable = false;//Just to run startProcessing the first time
        startProcessing();
    }

    /**
     * Put the Runnable in the process queue. When all previous Runnable have been ran, it will be processed.
     * @param run the runnable to process.
     */
    public void process(Runnable run)
    {
        transfer.offer(run);
    }

    /**
     * Put the Runnable in the front of the process queue, bypassing all previous inserted Runnable.
     * @param run the runnable to process.
     */
    public void forceProcess(Runnable run)
    {
        transfer.addFirst(run);
    }

    /**
     * Stop the process of all runnable.
     * The runnable which is currently processed will not be terminated
     * but will not be able to retry (It will be replace in the process queue).
     * To process again Runnable, startProcessing must be called
     */
    public void stopProcessing()
    {
        process_runnable = false;
    }

    /**
     * Have to be called after a call of stopProcessing.
     * stopProcessing haven't be called before, nothing is done.
     */
    public void startProcessing()
    {
        if (!process_runnable)
        {
            process_runnable = true;
            new Thread(new java.lang.Runnable() {
                @Override
                public void run() {
                    try
                    {
                        while (process_runnable)
                        {
                            final Handler handler = new Handler(Looper.getMainLooper());
                            final Runnable to_run = transfer.take();
                            if (to_run.run())
                                handler.post(new java.lang.Runnable() {
                                    @Override
                                    public void run() {
                                        to_run.onSuccess();
                                    }
                                });
                            else {
                                handler.post(new java.lang.Runnable() {
                                    @Override
                                    public void run() {
                                        if (to_run.retry())
                                        {
                                            AlertDialog dialog = new AlertDialog.Builder(to_run.context())
                                                    .setTitle(R.string.error)
                                                    .setMessage(to_run.errorDescription())
                                                    .setCancelable(false)
                                                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which)
                                                        {
                                                            transfer.offerFirst(to_run);
                                                        }
                                                    })
                                                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener()
                                                    {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which)
                                                        {
                                                            to_run.onError();
                                                        }
                                                    }).create();

                                            dialog.show();
                                        }
                                        else
                                            to_run.onError();
                                    }
                                });
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.d("AsyncRunner", e.toString());
                    }
                }
            }).start();
        }
    }
}
