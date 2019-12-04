package example.com.erp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import example.com.erp.BuildConfig;
import example.com.erp.R;
import example.com.erp.adapter.SlidingImageAdapter_ads;
import example.com.erp.fragment.FragmentHome;
import example.com.erp.fragment.OrdersFragment;
import example.com.erp.fragment.TransactionFragment;
import example.com.erp.fragment.ViewProfileFragment;
import example.com.erp.model.ImageModel_ads;
import example.com.erp.model.Message;
import example.com.erp.model.Result;
import example.com.erp.model.userInfo;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.AppController;
import example.com.erp.utility.CommonFunctions;
import example.com.erp.utility.ConnectivityReceiver;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 100;
    private static MainActivity instance;
    final int PERMISSION_FOR_READ_WRITE = 102;
    public boolean isConnected;
    DrawerLayout drawer;
    NavigationView navigationView;
    ArrayList<ImageModel_ads> imageModelArrayList;
    SlidingImageAdapter_ads slidingImageAdapter;
    TextView userName, contactNumber;
    Toolbar toolbar;
    ViewPager viewPager;
    CoordinatorLayout parent_layout;
    View header;
    ImageView img_banner;
    int currentpages = 0;
    int NUM_PAGES = 0;
    ProgressDialog progressDialog;
    String balance = "";
    Handler delayhandler;
    boolean isLocationPermissionGranted;
    Timer swipeTimer;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private double userLatitude = 0.0, userLongitude = 0.0;
    private LocationRequest mLocationRequest;
    private int navItemIndex = 0;
    private AlertDialog alertDialog;
    private ConnectivityReceiver receiver;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_home);
        instance = this;
        assignViews();
        checkConnection();
        // isReadStoragePermissionGranted();
        header.findViewById(R.id.txt_update_balance).setOnClickListener(V -> {
            progressDialog.setMessage("please wait..");
            progressDialog.show();
            RPC.updateLedgerBalance(SharedPreference.getString(Constants.UserId), new CommonListener() {
                @Override
                public void onSuccess(Object object) {
                    progressDialog.dismiss();
                    Message message = (Message) object;
                    if (message.getSuccess().equals("2")) {
                        Result result = message.getResult();
                        if (result == null) {
                            balance = "0.0";
                            ((TextView) header.findViewById(R.id.txt_balance)).setText(String.format("%s %s", balance, SharedPreference.getString(Constants.Currency)));
                            return;
                        }
                        balance = result.getBalance();
                        ((TextView) header.findViewById(R.id.txt_balance)).setText(String.format("%s %s", balance, SharedPreference.getString(Constants.Currency)));
                    } else {
                        Toast.makeText(MainActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String reason) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            });
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_gallery: {
                    drawer.closeDrawer(GravityCompat.START);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Logout")
                            .setMessage("Are you sure want to logout from application?")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.cancel();
                                dialog.dismiss();
                                String fcm = SharedPreference.getString(Constants.Fcm);
                                SharedPreference.clearAllPref();
                                SharedPreference.setString(Constants.Fcm, fcm);
                                Intent intent = new Intent(MainActivity.this, SplashScreenActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.cancel();
                                dialog.dismiss();
                            })
                            .setCancelable(true)
                            .create()
                            .show();
                }
                break;

                case R.id.nav_transactions: {
                    drawer.closeDrawer(GravityCompat.START);
                    displayView(2, null);
                }
                break;
                case R.id.nav_orders: {
                    drawer.closeDrawer(GravityCompat.START);
                    displayView(3, null);
                }
                break;
                case R.id.nav_home: {
                    drawer.closeDrawer(GravityCompat.START);
                    displayView(0, null);
                }
                break;
                case R.id.nav_change_pin: {
                    drawer.closeDrawer(GravityCompat.START);
                    showPinAlert(true);
                }
                break;
                case R.id.nav_view_profile: {
                    drawer.closeDrawer(GravityCompat.START);
                    displayView(4, null);
                }
                break;
                case R.id.nav_challan:
                    startActivity(new Intent(MainActivity.this, ChallanStatmentActivity.class).putExtra("tran_id", ""));
                    break;
            }
            return false;
        });

        header.findViewById(R.id.imageView).setOnClickListener(V -> {
            drawer.closeDrawer(GravityCompat.START);
            displayView(4, null);
        });

        //For open particular page on notification click...

      /*if (getIntent() != null && getIntent().getStringExtra("Page_Index") != null) {
         if (getIntent().getStringExtra("Page_Index").equals("3")) {
            navItemIndex = 2;
            callFragment(new OrdersFragment(), "Orders");
            getIntent().removeExtra("Page_Index");
         }
      } else {
         navItemIndex = 0;
         callFragment(new FragmentHome(), "Home");
      }*/
        displayView(0, null);
        if (getIntent() != null && getIntent().getStringExtra("Page_Index") != null) {
            if (getIntent().getStringExtra("Page_Index").equals("2")) {
                navItemIndex = 2;
                callFragment(new OrdersFragment(), "Orders");
                getIntent().removeExtra("Page_Index");
            }
        }
    }

    // TODO :- Permission
    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "Permission is granted1");
                return true;
            } else {
                Log.v("Permission", "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FOR_READ_WRITE);
                return false;
            }
        } else {
            Log.v("Permission", "Permission is granted2");
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            receiver = new ConnectivityReceiver();
            registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        AppController.getInstance().setConnectivityListener(this);
        invalidateOptionsMenu();
    }

    private void assignViews() {
        delayhandler = new Handler();
        swipeTimer = new Timer();

        imageModelArrayList = new ArrayList<>();
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.userName);
        contactNumber = header.findViewById(R.id.textView);
        img_banner = findViewById(R.id.img_banner);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.pager1);
        parent_layout = findViewById(R.id.parent_layout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        ((TextView) header.findViewById(R.id.txt_balance)).setText(getResources().getString(R.string.txt_update_balance));
        setDrawer();

        String userDetail = SharedPreference.getString(Constants.UserDetails);
        if (!TextUtils.isEmpty(userDetail)) {
            userInfo user = new Gson().fromJson(userDetail, userInfo.class);
            if (user != null) {
                userName.setText(user.getUser_full_name());
                contactNumber.setText(user.getUser_contact());
            }
        }

        getBottomPagerImages();
        checkLocationPermission();
        getDrawerImage();
    }

    private void getDrawerImage() {
        RPC.getDrawerBanner(new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                Message message = (Message) object;
                if (message != null) {
                    if (message.getAds() != null && message.getAds().size() > 0) {
                        Picasso.with(MainActivity.this)
                                .load(message.getAds().get(0).getAd_image_small())
                                /*.placeholder(R.drawable.splash_logo)
                                .error(R.drawable.splash_logo)*/
                                .into(img_banner);
                    } else {
                        //img_banner.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.splash_logo));
                    }
                } else {
                    //img_banner.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.splash_logo));
                }
            }

            @Override
            public void onFailure(String reason) {

            }
        });
    }

    private void setDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initPagerItem() {

        CirclePageIndicator indicator = findViewById(R.id.idicator1);
        indicator.setViewPager(viewPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);

        final Runnable update = () -> {
            if (currentpages == NUM_PAGES) {
                currentpages = 0;
            }
            viewPager.setCurrentItem(currentpages++, true);
        };

        if (swipeTimer == null) {
            swipeTimer = new Timer();
        }

        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                delayhandler.post(update);
            }
        }, 10000, 10000);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentpages = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void requestLocations() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                if (mCurrentLocation != null) {
                    userLatitude = mCurrentLocation.getLatitude();
                    userLongitude = mCurrentLocation.getLongitude();
                    stopLocationUpdates();
                    getUserInformation();//todo call 1
                }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (isLocationPermissionGranted)
            startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mSettingsClient.checkLocationSettings(new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).build())
                .addOnSuccessListener(this, locationSettingsResponse -> mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()))
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ignored) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings.";
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void stopLocationUpdates() {
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, task -> {
                });
    }

    private void checkLocationPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            isLocationPermissionGranted = true;
                            requestLocations();
                        } else {
                            getUserInformation();//todo call 2
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void showPinAlert(boolean canClose) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View inflateView = inflater.inflate(R.layout.alert_set_pin, parent_layout, false);
        builder.setView(inflateView);

        TextInputLayout inpPinCode = inflateView.findViewById(R.id.inp_pin_code);
        TextInputEditText edtPinCode = inflateView.findViewById(R.id.edt_pin_code);
        TextInputLayout inpConfPinCode = inflateView.findViewById(R.id.inp_conf_pin_code);
        TextInputEditText edtConfPinCode = inflateView.findViewById(R.id.edt_conf_pin_code);
        Button btnVerify = inflateView.findViewById(R.id.btn_verify);

        btnVerify.setOnClickListener(V -> {
            if (TextUtils.isEmpty(edtPinCode.getText().toString().trim())) {
                inpPinCode.setErrorEnabled(true);
                inpPinCode.setError("Cannot set empty pin!");
            } else if (edtPinCode.getText().toString().trim().length() < 3) {
                inpPinCode.setErrorEnabled(true);
                inpPinCode.setError("Pin is not valid!");
            } else if (TextUtils.isEmpty(edtConfPinCode.getText().toString().trim())) {
                inpConfPinCode.setErrorEnabled(true);
                inpConfPinCode.setError("Cannot set empty confirm pin!");
            } else if (!edtConfPinCode.getText().toString().trim().equals(edtPinCode.getText().toString().trim())) {
                inpConfPinCode.setErrorEnabled(true);
                inpConfPinCode.setError("Confirm pin is not matched!");
            } else {
                RPC.updateUserPin(SharedPreference.getString(Constants.UserId), edtConfPinCode.getText().toString().trim(), new CommonListener() {
                    @Override
                    public void onSuccess(Object object) {
                        alertDialog.cancel();
                        alertDialog.dismiss();
                        showPinSuccessAlert();
                    }

                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(MainActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog = builder.create();

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        alertDialog.setCanceledOnTouchOutside(canClose);
        alertDialog.setCancelable(canClose);
        alertDialog.show();

        ViewGroup parent = (ViewGroup) inflateView.getParent();
        parent.setPadding(0, 0, 0, 0);
    }

    private void getUserInformation() {
        showProgress();
        RPC.getUserInformation(SharedPreference.getString(Constants.MobileNumber), SharedPreference.getString(Constants.Fcm), userLatitude, userLongitude, new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                hideProgress();
                userInfo user = (userInfo) object;
                userName.setText(user.getUser_full_name());
                contactNumber.setText(user.getUser_contact());
                SharedPreference.setString(Constants.UserId, user.getId());
                SharedPreference.setString(Constants.UserDetails, new Gson().toJson(user));
                SharedPreference.setString(Constants.Currency, user.getCurrency());
                if (user.getPin().equals("0")) showPinAlert(false);
            }

            @Override
            public void onFailure(String reason) {
                hideProgress();
                Toast.makeText(MainActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.cancel();
        }
    }

    private void showPinSuccessAlert() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Success")
                .setMessage("Pin set successfully. Now you can login with pin without getting auth code.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                    dialog.cancel();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void getBottomPagerImages() {
        RPC.getBottomBannerImages(new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                Message message = (Message) object;
                if (message.getAds() != null && message.getAds().size() > 0) {
                    imageModelArrayList = message.getAds();
                    NUM_PAGES = imageModelArrayList.size();
                    slidingImageAdapter = new SlidingImageAdapter_ads(imageModelArrayList, MainActivity.this, "HomePage");
                    viewPager.setAdapter(slidingImageAdapter);
                    initPagerItem();
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(MainActivity.this, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayView(int index, @Nullable String extraVal) {
        navItemIndex = index;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

        switch (index) {
            case 0: {
                invalidateOptionsMenu();
                callFragment(new FragmentHome(), "Home");
            }
            break;

            case 1: {
                invalidateOptionsMenu();
                Intent intent = new Intent(this, CartItemActivity.class);
                if (extraVal != null) {
                    intent.putExtra("query", extraVal);
                }
                startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle());
            }
            break;

            case 2: {
                invalidateOptionsMenu();
                callFragment(new TransactionFragment(), "Transaction");
            }
            break;

            case 3: {
                invalidateOptionsMenu();
                callFragment(new OrdersFragment(), "Orders");
            }
            break;

            case 4: {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    getSupportActionBar().setTitle(SharedPreference.getString(Constants.MobileNumber));
                }
                invalidateOptionsMenu();
                callFragment(new ViewProfileFragment(), "Profile");
            }
            break;

            case 5: {
                invalidateOptionsMenu();
                startActivity(new Intent(MainActivity.this, EditProfileActivity.class), ActivityOptions.makeCustomAnimation(this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle());
            }
            break;
            case 6: {
                invalidateOptionsMenu();
                Intent intent = new Intent(this, OrderableItemActivity.class);
                if (extraVal != null) {
                    intent.putExtra("query", extraVal);
                }
                startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.enter_from_left, R.anim.exit_to_right).toBundle());
            }
            break;

        }
    }

    public void callFragment(Fragment fragment, String fragTitle) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.content_frame, fragment, fragTitle);
            fragmentTransaction.commitAllowingStateLoss();
        }
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
                .make(parent_layout, "No Internet Connection Found!!!", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", view -> {
                    if (ConnectivityReceiver.isConnected()) {
                        Snackbar.make(parent_layout, "Internet Connection Found!!!", Snackbar.LENGTH_SHORT).setActionTextColor(Color.WHITE).show();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
//      MenuItem itemCart = menu.findItem( R.id.action_item );
//      LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
//      CommonFunctions.setBadgeCount( this, icon, String.valueOf( CommonFunctions.getCartCount( this ) ) );

        if (navItemIndex == 4) {
            menu.findItem(R.id.action_edit).setVisible(true);
            // menu.findItem( R.id.action_search ).setVisible( true);
            //menu.findItem( R.id.action_item ).setVisible( false );
        } else {
//         menu.findItem( R.id.action_edit ).setVisible( false );
//         menu.findItem( R.id.action_search ).setVisible( true );
//         menu.findItem( R.id.action_item ).setVisible( true );
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            displayView(6, null);
        }
//      else if (item.getItemId() == R.id.action_item) {
//         displayView( 1, null );
//         return true;
//      }
        else if (item.getItemId() == R.id.action_edit) {
            displayView(5, null);
            return true;
        } else if (item.getItemId() == R.id.action_Enquiry) {
            startActivity(new Intent(MainActivity.this, EnquiryActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        isLocationPermissionGranted = true;
                        break;
                    case Activity.RESULT_CANCELED:
                        isLocationPermissionGranted = false;
                        break;
                }
                break;
            case Constants.VIEW_ORDER: {
                try {
                    Log.e("Android->>", "onActivityResult: ");
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("Orders");
                    fragment.onActivityResult(requestCode, resultCode, data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isLocationPermissionGranted) {
            stopLocationUpdates();
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (navItemIndex > 0) {
            displayView(0, null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delayhandler.removeCallbacks(null);
    }

    @Override
    public void onNetworkConnectionChange(boolean isConnect) {
        this.isConnected = isConnected;
        changeStatus(isConnected);
    }
}
