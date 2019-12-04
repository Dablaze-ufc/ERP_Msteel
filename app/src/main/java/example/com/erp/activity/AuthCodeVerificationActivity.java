package example.com.erp.activity;
/*
Create by user on 19-02-2019 at 03:52 PM for ERP
*/

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import example.com.erp.R;
import example.com.erp.model.Message;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

import static example.com.erp.utility.CommonFunctions.hideKeyboard;

public class AuthCodeVerificationActivity extends AppCompatActivity {
    private String mobileNumber = "", callType = "", firmName = "", gstNumber = "", imeiNumber = "";
    private CoordinatorLayout parentLayout;
    private TextView txtSubHeader;
    private TextInputEditText codeOne;
    private TextInputEditText codeTwo;
    private TextInputEditText codeThree;
    private TextInputEditText codeFour;
    private Button btnVerify;
    private TextView redirect;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_auth_code);
        if (getIntent() != null) {
            if (getIntent().getStringExtra("callType") != null) {
                callType = getIntent().getStringExtra("callType");
            }

            if (getIntent().getStringExtra("mobileNumber") != null) {
                mobileNumber = getIntent().getStringExtra("mobileNumber");
            }

            if (callType.equalsIgnoreCase("2")) { // registration form...
                if (getIntent().getStringExtra("firmName") != null) {
                    firmName = getIntent().getStringExtra("firmName");
                }

                if (getIntent().getStringExtra("gstNumber") != null) {
                    gstNumber = getIntent().getStringExtra("gstNumber");
                }
            }
        }
        assignViews();

        codeOne.addTextChangedListener(new MyTextWatcher(codeOne, codeTwo));
        codeTwo.addTextChangedListener(new MyTextWatcher(codeTwo, codeThree));
        codeThree.addTextChangedListener(new MyTextWatcher(codeThree, codeFour));
        codeFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    hideKeyboard(AuthCodeVerificationActivity.this);
                }
            }
        });

        btnVerify.setOnClickListener(V -> {
            if (TextUtils.isEmpty(codeOne.getText().toString().trim()) || TextUtils.isEmpty(codeTwo.getText().toString().trim()) || TextUtils.isEmpty(codeThree.getText().toString().trim()) || TextUtils.isEmpty(codeFour.getText().toString().trim())) {
                Toast.makeText(this, "Please Add Auth code first...", Toast.LENGTH_SHORT).show();
            } else {
                if (callType.equalsIgnoreCase("1")) {
                    verifyAuthCode(); //login verification
                } else if (callType.equalsIgnoreCase("2")) {
                    verifyUser(); //registration verification
                }
            }
        });
    }

    private void assignViews() {
        parentLayout = findViewById(R.id.parent_layout);
        txtSubHeader = findViewById(R.id.txt_sub_header);
        codeOne = findViewById(R.id.code_one);
        codeTwo = findViewById(R.id.code_two);
        codeThree = findViewById(R.id.code_three);
        codeFour = findViewById(R.id.code_four);
        btnVerify = findViewById(R.id.btn_verify);
        redirect = findViewById(R.id.redirect);
        progressDialog = new ProgressDialog(AuthCodeVerificationActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        SpannableString string = new SpannableString("Enter Auth code you received on mobile number:" + mobileNumber);
        string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.black)), 0, ("Enter Auth code you received on mobile number:").length(), 0);
        string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorText)), ("Enter Auth code you received on mobile number:").length() + 1, string.length(), 0);
        string.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ("Enter Auth code you received on mobile number:").length(), string.length(), 0);
        txtSubHeader.setText(string);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (telephonyManager != null)
            imeiNumber = telephonyManager.getDeviceId();

    }

    private void showProgress() {
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    private void verifyAuthCode() {
        showProgress();
        String code = codeOne.getText().toString().trim() + codeTwo.getText().toString().trim() + codeThree.getText().toString().trim() + codeFour.getText().toString().trim();
        RPC.verifyAuthCode(mobileNumber, code, "", new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                Message message = (Message) object;
                Toast.makeText(AuthCodeVerificationActivity.this, "User Logged successfully", Toast.LENGTH_SHORT).show();
                SharedPreference.setString(Constants.MobileNumber, mobileNumber);
                SharedPreference.setString(Constants.FullName, message.getUser_full_name());
                SharedPreference.setString(Constants.UserId, message.getUser_id());
                Intent intent = new Intent(AuthCodeVerificationActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(AuthCodeVerificationActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyUser() {
        showProgress();
        String code = codeOne.getText().toString().trim() + codeTwo.getText().toString().trim() + codeThree.getText().toString().trim() + codeFour.getText().toString().trim();
        RPC.confirmRegisterUser(mobileNumber, firmName, gstNumber, code, imeiNumber, new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                new AlertDialog.Builder(AuthCodeVerificationActivity.this)
                        .setTitle("Registration Successful")
                        .setMessage("Your registration successful")
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.cancel();
                            dialog.dismiss();
                            Intent intent = new Intent(AuthCodeVerificationActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show();
            }
        });
    }

    class MyTextWatcher implements TextWatcher {

        TextInputEditText current, next;

        MyTextWatcher(TextInputEditText currentText, TextInputEditText nextText) {
            this.current = currentText;
            this.next = nextText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length() == 1) {
                current.clearFocus();
                next.requestFocus();
            }
        }
    }
}
