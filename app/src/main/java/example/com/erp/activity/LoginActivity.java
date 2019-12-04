package example.com.erp.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import example.com.erp.R;
import example.com.erp.model.Message;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.AppController;
import example.com.erp.utility.ConnectivityReceiver;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Animation fade_in, fade_out;
    private CoordinatorLayout parentLayout;
    private TextView erp;
    private LinearLayout llSignIn;
    private TextView txtSignIn;
    private View viewSignIn;
    private LinearLayout llSignUp;
    private TextView txtSignUp;
    private View viewSignUp;
    private LinearLayout parentLogin;
    private TextInputLayout inputLoginMobileNo;
    private TextInputEditText edtLoginMobileNo;
    private TextInputLayout inputLoginPin;
    private TextInputEditText edtLoginPin;
    private Button btnLogin;
    private Button btnLoginWith;
    private LinearLayout parentRegistration;
    private TextInputLayout inputFirmName;
    private TextInputEditText edtFirmName;
    private TextInputLayout inputContactPerName;
    private TextInputEditText edtContactPerName;
    private TextInputLayout inputRegisterMobileNo;
    private TextInputEditText edtRegisterMobileNo;
    private TextInputLayout inputAddrOne;
    private TextInputEditText edtAddrOne;
    private TextInputLayout inputAddrTwo;
    private TextInputEditText edtAddrTwo;
    private TextInputLayout inputLocality;
    private TextInputEditText edtLocality;
    private TextInputLayout inputLandmark;
    private TextInputEditText edtLandmark;
    private TextInputLayout inputCity;
    private TextInputEditText edtCity;
    private TextInputLayout inputPincode;
    private TextInputEditText edtPincode;
    private TextInputLayout inputGstNumber;
    private TextInputEditText edtGstNumber;
    private TextInputLayout inputRegisterPin;
    private TextInputEditText edtRegisterPin;
    private TextInputLayout inputRegisterConfPin;
    private TextInputEditText edtRegisterConfPin;
    private Button btnRegister;
    private TextView redirect;

    private int callType = 0, loginType = 0;
    private ProgressDialog progressDialog;
    private Pattern checkRegex = Pattern.compile("/^([0][1-9]|[1-2][0-9]|[3][0-5])([a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ]{1}[0-9a-zA-Z]{1})+$/");
    private ConnectivityReceiver receiver;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_new_login);
        assignViews();
        checkConnection();

        edtLoginPin.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (isConnected) {
                    validatePinLogin();
                }
                return true;
            }
            return false;
        });

        llSignIn.setOnClickListener(V -> {
            callType = 0;
            setDefault(true);
        });

        llSignUp.setOnClickListener(V -> {
            callType = 1;
            setDefault(true);
        });

        btnLoginWith.setOnClickListener(V -> {
            if (btnLoginWith.getText().toString().equals("Login With OTP")) { //change button
                clearLoginErrors();
                loginType = 1;
                setDefaultLoginType(true);
            } else {//do login
                clearLoginErrors();
                loginType = 0;
                setDefaultLoginType(true);
            }
        });

        btnLogin.setOnClickListener(V -> {
            if (loginType == 0) {
                validatePinLogin();
            } else {
                validateAuthLogin();
            }
        });

        btnRegister.setOnClickListener(V -> {
            validateRegister();
        });
    }

    private void validateAuthLogin() {
        if (TextUtils.isEmpty(edtLoginMobileNo.getText().toString().trim())) {
            //set error
            inputLoginMobileNo.setErrorEnabled(true);
            inputLoginMobileNo.setError("Cannot set empty phone number!");
        } else if (edtLoginMobileNo.getText().toString().trim().length() < 6 || edtLoginMobileNo.getText().toString().trim().length() > 13) {
            //show error
            inputLoginMobileNo.setErrorEnabled(true);
            inputLoginMobileNo.setError("Phone number is not valid!");
        } else { //note login with mobile number
            loginUserWithAuthCode();
        }
    }

    private void validatePinLogin() {
        if (TextUtils.isEmpty(edtLoginMobileNo.getText().toString().trim())) {
            //set error
            inputLoginMobileNo.setErrorEnabled(true);
            inputLoginMobileNo.setError("Cannot set empty phone number!");
        } else if (edtLoginMobileNo.getText().toString().trim().length() < 6 || edtLoginMobileNo.getText().toString().trim().length() > 13) {
            //show error
            inputLoginMobileNo.setErrorEnabled(true);
            inputLoginMobileNo.setError("Phone number is not valid!");
        } else if (TextUtils.isEmpty(edtLoginPin.getText().toString().trim())) {
            //set error
            inputLoginPin.setErrorEnabled(true);
            inputLoginPin.setError("Cannot set empty pin!");
        } else if (edtLoginPin.getText().toString().trim().length() < 3) {
            //show error
            inputLoginPin.setErrorEnabled(true);
            inputLoginPin.setError("Pin is not valid!");
        } else { //note login with pin nd mobile number
            //server call
            loginUserWithPin();
        }
    }

    private void validateRegister() {
        if (TextUtils.isEmpty(edtFirmName.getText().toString().trim())) {
            inputFirmName.setErrorEnabled(true);
            inputFirmName.setError("Cannot set empty full name!");
        } else if (TextUtils.isEmpty(edtRegisterMobileNo.getText().toString().trim())) {
            inputRegisterMobileNo.setErrorEnabled(true);
            inputRegisterMobileNo.setError("Cannot set empty phone number!");
        } else if (edtRegisterMobileNo.getText().toString().trim().length() < 6 || edtRegisterMobileNo.getText().toString().trim().length() > 13) {
            inputRegisterMobileNo.setErrorEnabled(true);
            inputRegisterMobileNo.setError("Phone number is not valid!");
        } else if (TextUtils.isEmpty(edtAddrOne.getText().toString().trim())) {
            inputAddrOne.setErrorEnabled(true);
            inputAddrOne.setError("Cannot set empty address!");
        } else if (TextUtils.isEmpty(edtCity.getText().toString().trim())) {
            inputCity.setErrorEnabled(true);
            inputCity.setError("Cannot set empty city name!");
            edtCity.requestFocus();
        } else if (TextUtils.isEmpty(edtPincode.getText().toString().trim())) {
            inputPincode.setErrorEnabled(true);
            inputPincode.setError("Cannot set empty pin code!");
            edtPincode.requestFocus();
        } /*else if (TextUtils.isEmpty(edtGstNumber.getText().toString().trim())) {
         inputGstNumber.setErrorEnabled(true);
         inputGstNumber.setError("Cannot set empty GST number!");
      } else if (!checkRegex.matcher(edtGstNumber.getText().toString().trim()).matches()) {
         inputGstNumber.setErrorEnabled(true);
         inputGstNumber.setError("GST number is not valid!");
      }*/ else if (TextUtils.isEmpty(edtRegisterPin.getText().toString().trim())) {
            inputRegisterPin.setErrorEnabled(true);
            inputRegisterPin.setError("Cannot set empty pin!");
        } else if (edtRegisterPin.getText().toString().trim().length() < 3) {
            //show error
            inputRegisterPin.setErrorEnabled(true);
            inputRegisterPin.setError("Pin is not valid!");
        } else if (TextUtils.isEmpty(edtRegisterConfPin.getText().toString().trim())) {
            inputRegisterConfPin.setErrorEnabled(true);
            inputRegisterConfPin.setError("Cannot set empty confirm pin!");
        } else if (!edtRegisterConfPin.getText().toString().trim().equals(edtRegisterPin.getText().toString().trim())) {
            inputRegisterConfPin.setErrorEnabled(true);
            inputRegisterConfPin.setError("Confirm pin is not matched!");
        } else {
            //server call
            registerUser();
        }
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

    private void assignViews() {
        parentLayout = findViewById(R.id.parent_layout);
        erp = findViewById(R.id.erp);
        llSignIn = findViewById(R.id.ll_sign_in);
        txtSignIn = findViewById(R.id.txt_sign_in);
        viewSignIn = findViewById(R.id.view_sign_in);
        llSignUp = findViewById(R.id.ll_sign_up);
        txtSignUp = findViewById(R.id.txt_sign_up);
        viewSignUp = findViewById(R.id.view_sign_up);
        parentLogin = findViewById(R.id.parent_login);
        inputLoginMobileNo = findViewById(R.id.input_login_mobile_no);
        edtLoginMobileNo = findViewById(R.id.edt_login_mobile_no);
        inputLoginPin = findViewById(R.id.input_login_pin);
        edtLoginPin = findViewById(R.id.edt_login_pin);
        btnLogin = findViewById(R.id.btn_login);
        btnLoginWith = findViewById(R.id.btn_login_with);
        parentRegistration = findViewById(R.id.parent_registration);
        inputFirmName = findViewById(R.id.input_firm_name);
        edtFirmName = findViewById(R.id.edt_firm_name);
        inputContactPerName = findViewById(R.id.input_contact_per_name);
        edtContactPerName = findViewById(R.id.edt_contact_per_name);
        inputRegisterMobileNo = findViewById(R.id.input_register_mobile_no);
        edtRegisterMobileNo = findViewById(R.id.edt_register_mobile_no);
        inputAddrOne = findViewById(R.id.input_addr_one);
        edtAddrOne = findViewById(R.id.edt_addr_one);
        inputAddrTwo = findViewById(R.id.input_addr_two);
        edtAddrTwo = findViewById(R.id.edt_addr_two);
        inputLocality = findViewById(R.id.input_locality);
        edtLocality = findViewById(R.id.edt_locality);
        inputLandmark = findViewById(R.id.input_landmark);
        edtLandmark = findViewById(R.id.edt_landmark);
        inputCity = findViewById(R.id.input_city);
        edtCity = findViewById(R.id.edt_city);
        inputPincode = findViewById(R.id.input_pincode);
        edtPincode = findViewById(R.id.edt_pincode);
        inputGstNumber = findViewById(R.id.input_gst_number);
        edtGstNumber = findViewById(R.id.edt_gst_number);
        inputRegisterPin = findViewById(R.id.input_register_pin);
        edtRegisterPin = findViewById(R.id.edt_register_pin);
        inputRegisterConfPin = findViewById(R.id.input_register_conf_pin);
        edtRegisterConfPin = findViewById(R.id.edt_register_conf_pin);
        btnRegister = findViewById(R.id.btn_register);
        redirect = findViewById(R.id.redirect);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        setDefault(false);
    }

    private void loginUserWithPin() {
        showProgress();
        RPC.verifyAuthCode(getString(edtLoginMobileNo), "", getString(edtLoginPin), new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                Message message = (Message) object;
                hideProgress();
                SharedPreference.setString(Constants.MobileNumber, getString(edtLoginMobileNo));
                SharedPreference.setString(Constants.FullName, message.getUser_full_name());
                SharedPreference.setString(Constants.UserId, message.getUser_id());

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent, ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle());
                finish();
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(LoginActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUserWithAuthCode() {
        showProgress();
        RPC.getAuthCode(getString(edtLoginMobileNo), new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                Toast.makeText(LoginActivity.this, "Please Check your phone for auth code!!!", Toast.LENGTH_SHORT).show();
                Intent authIntent = new Intent(LoginActivity.this, AuthCodeVerificationActivity.class);
                authIntent.putExtra("mobileNumber", getString(edtLoginMobileNo));
                authIntent.putExtra("callType", "1");
                new Handler().postDelayed(() -> startActivity(authIntent, ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle()), 1000);
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(LoginActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
        showProgress();
        /*@Query("rFirmname") String Fname, @Query("rGstNumber") String gst, @Query("rMobileNo") String mobileNo, @Query("pin") String pin, @Query("personName") String personName, @Query("address") String address, @Query("location") String location, @Query("pincode") String pincode, @Query("streetNo") String streetNo, @Query("streetName") String streetName, @Query("city") String city*/
        /*String mobileNumber, String firmName, String contactName, String address_one, String address_two, String locality, String landmark,String city, String pincode, String gstNumber, String pinNumber*/

        RPC.registerUser(getString(edtRegisterMobileNo), getString(edtFirmName), getString(edtContactPerName), getString(edtAddrOne), getString(edtAddrTwo), getString(edtLocality), getString(edtLandmark), getString(edtCity), getString(edtPincode), getString(edtGstNumber), getString(edtRegisterConfPin), new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                Toast.makeText(LoginActivity.this, "Please Check your phone for auth code!!!", Toast.LENGTH_SHORT).show();

                Intent authIntent = new Intent(LoginActivity.this, AuthCodeVerificationActivity.class);
                authIntent.putExtra("mobileNumber", getString(edtRegisterMobileNo));
                authIntent.putExtra("callType", "2");
                authIntent.putExtra("firmName", getString(edtFirmName));
                authIntent.putExtra("gstNumber", getString(edtGstNumber));

                new Handler().postDelayed(() -> startActivity(authIntent, ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle()), 1000);
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(LoginActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getString(View view) {
        if (view instanceof TextInputEditText) {
            return TextUtils.isEmpty(((TextInputEditText) view).getText().toString().trim()) ? "" : ((TextInputEditText) view).getText().toString().trim();
        } else {
            return "";
        }
    }

    private void setDefault(boolean change) {
        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        if (change) {
            if (callType == 0) {// login
                parentRegistration.startAnimation(fade_out);
                fade_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        parentRegistration.setVisibility(View.GONE);
                        parentLogin.setVisibility(View.VISIBLE);
                        parentLogin.setAnimation(fade_in);
                        fade_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                txtSignIn.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.black));
                                txtSignUp.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.gray));
                                viewSignIn.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
                                viewSignUp.setBackgroundColor(0);
                                fade_in.setAnimationListener(null);
                                fade_out.setAnimationListener(null);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else { // register
                parentLogin.startAnimation(fade_out);
                fade_out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        parentLogin.setVisibility(View.GONE);
                        parentRegistration.setVisibility(View.VISIBLE);
                        parentRegistration.setAnimation(fade_in);
                        fade_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                txtSignUp.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.black));
                                txtSignIn.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.gray));
                                viewSignUp.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
                                viewSignIn.setBackgroundColor(0);
                                fade_in.setAnimationListener(null);
                                fade_out.setAnimationListener(null);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        } else {
            txtSignIn.setTextColor(ContextCompat.getColor(this, R.color.black));
            txtSignUp.setTextColor(ContextCompat.getColor(this, R.color.gray));
            viewSignIn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            viewSignUp.setBackgroundColor(0);
            parentRegistration.setVisibility(View.GONE);
            parentLogin.setVisibility(View.VISIBLE);
            parentLogin.startAnimation(fade_in);

            fade_in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fade_in = null;
                    fade_out = null;
                    parentLogin.setVisibility(View.VISIBLE);
                    parentRegistration.setVisibility(View.GONE);
                    setDefaultLoginType(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void setDefaultLoginType(boolean change) {
        if (change) {
            if (loginType == 0) {//login with pin
                inputLoginPin.setVisibility(View.VISIBLE);
                btnLoginWith.setText("Login With OTP");
            } else {
                inputLoginPin.setVisibility(View.GONE);
                btnLoginWith.setText("Login With PIN");
            }
        } else {
            inputLoginPin.setVisibility(View.VISIBLE);
            btnLogin.setText("Login");
            btnLoginWith.setText("Login With OTP");
        }
    }

    private void clearLoginErrors() {
        inputLoginPin.setErrorEnabled(false);
        inputLoginPin.setError(null);
        inputLoginMobileNo.setErrorEnabled(false);
        inputLoginMobileNo.setError(null);
    }

    @Override
    public void onNetworkConnectionChange(boolean isConnect) {
        this.isConnected = isConnect;
        changeStatus(isConnected);
    }

    private void changeStatus(boolean isConnected) {
        if (!isConnected) {
            showNetworkError();
        }
    }

    public void checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
        changeStatus(isConnected);
    }

    public void showNetworkError() {
        Snackbar snackbar = Snackbar
                .make(parentLayout, "No Internet Connection Found!!!", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", view -> {
                    if (ConnectivityReceiver.isConnected()) {
                        Snackbar.make(parentLayout, "Internet Connection Found!!!", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
                    } else {
                        showNetworkError();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            receiver = new ConnectivityReceiver();
            registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        AppController.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }

    public void unRegisterReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
        }
    }

}