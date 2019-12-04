package example.com.erp.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import example.com.erp.activity.BillReceiptActivity;

public class Utils {

    // TODO :- download

    public static void downloadTask(Context context, String url, String extension) {
        ProgressDialog mProgressDialog;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(context, mProgressDialog, extension);
        downloadTask.execute(url);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true); //cancel the task
            }
        });
    }

    public static void downloadTask(BillReceiptActivity billReceiptActivity, String url) {
    }
}
