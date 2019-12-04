package example.com.erp.activity;
/*
Create by user on 25-02-2019 at 06:03 PM for ERP
*/

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import example.com.erp.R;

public class ActHandlePushService extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            if (getIntent().getStringExtra("Intent_received") != null)
                switch (Integer.parseInt(getIntent().getStringExtra("Intent_received"))) {
                    case 1: {
                    }
                    break;

                    case 2: {
                    }
                    break;

                    case 3: {
                        openIntent(new Intent(this, MainActivity.class).putExtra("Page_Index", "3"));
                    }
                    break;

                    case 4: {
                    }
                    break;

                    case 5: {
                    }
                    break;

                    default: {
                        openIntent(new Intent(this, SplashScreenActivity.class));
                    }
                    break;
                }
        }
    }

    private void openIntent(Intent intent) {
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP), ActivityOptions.makeCustomAnimation(this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle());
        finish();
    }
}
