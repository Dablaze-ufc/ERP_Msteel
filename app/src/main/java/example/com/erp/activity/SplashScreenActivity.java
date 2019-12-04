package example.com.erp.activity;
/*
Create by user on 14-02-2019 at 06:54 PM for ERP
*/

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import example.com.erp.R;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class SplashScreenActivity extends AppCompatActivity {

    String version = "", appVersion = "";
    boolean isAnimationOver;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash_screen);
        if (isConnected()) {
            checkVersionCode();
        }
        showAnimation();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkVersionCode() {
        RPC.checkVersionCode(new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                appVersion = (String) object;
                if (isAnimationOver) {
                    verifyVersion();
                }
            }

            @Override
            public void onFailure(String reason) {
                showAnimation();
            }
        });
    }

    private void verifyVersion() {
        if (Integer.parseInt(appVersion.replaceAll("\\.", "")) > Integer.parseInt(version.replaceAll("\\.", ""))) {
            showUpdateAlert();
        } else {
            completeProcess();
        }
    }

    private void completeProcess() {
        Intent intent;
        if (!SharedPreference.getString(Constants.MobileNumber).equals("")) {
            intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 100);
    }

    private void showUpdateAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Application")
                .setMessage("Congratulation new version of application is available, Update now!")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1500);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimationOver = true;
                if (!appVersion.equals("")) {
                    verifyVersion();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.erp).startAnimation(animation);
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
