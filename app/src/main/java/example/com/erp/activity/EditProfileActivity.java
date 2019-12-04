package example.com.erp.activity;

import android.app.ProgressDialog;
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import example.com.erp.R;
import example.com.erp.model.userInfo;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.AppController;
import example.com.erp.utility.ConnectivityReceiver;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class EditProfileActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Animation fade_in, fade_out;
    private CoordinatorLayout parentLayout;
    private Toolbar toolbar;
    private NestedScrollView nestedScroll;
    private TextInputLayout inputFirmName;
    private TextInputEditText edtFirmName;
    private TextInputLayout inputContactPerName;
    private TextInputEditText edtContactPerName;
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
    private Button btnUpdate;

    private ProgressDialog progressDialog;
    private String userNumber = "";
    private Pattern checkRegex = Pattern.compile("/^([0][1-9]|[1-2][0-9]|[3][0-5])([a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ]{1}[0-9a-zA-Z]{1})+$/");
    private ConnectivityReceiver receiver;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_profile);
        assignViews();
        checkConnection();

        btnUpdate.setOnClickListener(V -> validateRegister());
    }

    private void assignViews() {
        parentLayout = findViewById(R.id.parent_layout);
        toolbar = findViewById(R.id.toolbar);
        nestedScroll = findViewById(R.id.nested_scroll);
        inputFirmName = findViewById(R.id.input_firm_name);
        edtFirmName = findViewById(R.id.edt_firm_name);
        inputContactPerName = findViewById(R.id.input_contact_per_name);
        edtContactPerName = findViewById(R.id.edt_contact_per_name);
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
        btnUpdate = findViewById(R.id.btn_update);

        getUserInformation();
    }

    private void getUserInformation() {
        showProgress();
        RPC.getUserInformation(SharedPreference.getString(Constants.MobileNumber), SharedPreference.getString(Constants.Fcm), 0.0, 0.0, new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                userInfo user = (userInfo) object;
                userNumber = user.getUser_contact();
                loadUserDetails(user);
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(EditProfileActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails(userInfo user) {
        if (user == null)
            return;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Mobile number: " + user.getUser_contact());
        }

        edtFirmName.setText(user.getUser_full_name());
        edtContactPerName.setText(user.getUser_person_name());
        edtAddrOne.setText(user.getUser_street());
        edtAddrTwo.setText(user.getUser_street_name());
        edtLocality.setText(user.getUser_locality());
        edtLandmark.setText(user.getUser_address());
        edtCity.setText(user.getUser_city());
        edtPincode.setText(user.getUser_pin());
        edtGstNumber.setText(user.getGst());
    }

    private void validateRegister() {
        if (TextUtils.isEmpty(edtFirmName.getText().toString().trim())) {
            inputFirmName.setErrorEnabled(true);
            inputFirmName.setError("Cannot set empty full name!");
            edtFirmName.requestFocus();
            nestedScroll.smoothScrollTo(0, edtFirmName.getTop());
        } else if (TextUtils.isEmpty(edtAddrOne.getText().toString().trim())) {
            inputAddrOne.setErrorEnabled(true);
            inputAddrOne.setError("Cannot set empty address!");
            edtAddrOne.requestFocus();
            nestedScroll.smoothScrollTo(0, edtAddrOne.getTop());
        } else if (TextUtils.isEmpty(edtCity.getText().toString().trim())) {
            inputCity.setErrorEnabled(true);
            inputCity.setError("Cannot set empty city name!");
            edtCity.requestFocus();
            nestedScroll.smoothScrollTo(0, edtCity.getTop());
        } else if (TextUtils.isEmpty(edtPincode.getText().toString().trim())) {
            inputPincode.setErrorEnabled(true);
            inputPincode.setError("Cannot set empty pin code!");
            edtPincode.requestFocus();
            nestedScroll.smoothScrollTo(0, edtPincode.getTop());
        } /*else if (TextUtils.isEmpty(edtGstNumber.getText().toString().trim())) {
         inputGstNumber.setErrorEnabled(true);
         inputGstNumber.setError("Cannot set empty GST number!");
      } else if (!checkRegex.matcher(edtGstNumber.getText().toString().trim()).matches()) {
         inputGstNumber.setErrorEnabled(true);
         inputGstNumber.setError("GST number is not valid!");
      }*/ else {
            //server call
            updateUser();
        }
    }

    private void updateUser() {
        showProgress();
        RPC.updateUserProfile(SharedPreference.getString(Constants.UserId), userNumber, getString(edtFirmName), getString(edtContactPerName), getString(edtAddrOne), getString(edtAddrTwo), getString(edtLocality), getString(edtLandmark), getString(edtPincode), getString(edtGstNumber), getString(edtCity), new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                Toast.makeText(EditProfileActivity.this, "Your Profile has been updated successfully!!!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> finish(), 1000);
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(EditProfileActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(EditProfileActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    private String getString(View view) {
        if (view instanceof TextInputEditText) {
            return TextUtils.isEmpty(((TextInputEditText) view).getText().toString().trim()) ? "" : ((TextInputEditText) view).getText().toString().trim();
        } else {
            return "";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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