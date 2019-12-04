package example.com.erp.utility;
/*
Create by user on 25-02-2019 at 04:44 PM for ERP
*/

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.firebase.iid.FirebaseInstanceId;

public class AppController extends Application {

    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        generateToken();
    }

    private void generateToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            SharedPreference.setString(Constants.Fcm, instanceIdResult.getToken());
        });
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
