package example.com.erp.utility;
/*
Create by user on 20-06-2018 at 12:20 PM for OnlineExam
*/

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) AppController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return activeNetwork.isConnectedOrConnecting();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return activeNetwork.isConnectedOrConnecting();
            }
        } else {
            return false;
        }
        return false;
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean isConnected;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                isConnected = activeNetwork.isConnected();
                if (connectivityReceiverListener != null) {
                    connectivityReceiverListener.onNetworkConnectionChange(isConnected);
                }
            } else {
                isConnected = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && activeNetwork.isConnected();
                if (connectivityReceiverListener != null) {
                    connectivityReceiverListener.onNetworkConnectionChange(isConnected);
                }
            }
        } else {
            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChange(false);
            }
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChange(boolean isConnect);
    }
}