package example.com.erp.activity;
/*
Create by user on 14-02-2019 at 04:32 PM for ERP
*/

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import example.com.erp.R;
import example.com.erp.adapter.ItemAdapter;
import example.com.erp.adapter.SlidingImageAdapter;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.model.Group;
import example.com.erp.model.Group_name;
import example.com.erp.model.Gs_item;
import example.com.erp.model.ImageModel;
import example.com.erp.model.Message;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import example.com.erp.utility.CommonFunctions;
import example.com.erp.utility.Constants;
import example.com.erp.utility.ItemOffsetDecoration;
import example.com.erp.utility.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubSubItemActivity extends AppCompatActivity {
    Handler delayhandler;
    Timer swipeTimer;
    private Toolbar toolbar;
    private ViewPager pager;
    private CirclePageIndicator idicator;
    private RecyclerView recyclerView;
    private String id, title, subTitle;
    private int currentpages = 0;
    private int NUM_PAGES = 0;
    private Message message;
    private ArrayList<ImageModel> imageModelArrayList;
    private SlidingImageAdapter slidingImageAdapter;
    private ArrayList<Gs_item> arrayList;
    private ArrayList<String> order;
    private ArrayList<Gs_item> orderArrylist;
    private ProgressDialog progressDialog;
    private ItemAdapter categoryAdapter;
    private CoordinatorLayout coordinator;
    private String toolbarTitle = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private int cartItemCount = 0;
    private Group_name group_name = new Group_name();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("id") != null ? getIntent().getStringExtra("id") : "";
            title = getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "";
            subTitle = getIntent().getStringExtra("subTitle") != null ? getIntent().getStringExtra("subTitle") : "";
            group_name = new Gson().fromJson(getIntent().getStringExtra("Group_data"), Group_name.class);
        }

        setContentView(R.layout.act_content_child);
        assignViews();
        getGroupList(id);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (categoryAdapter != null) {
                categoryAdapter.clearData();
                getGroupList(id);
            }
        });
    }

    private void assignViews() {
        delayhandler = new Handler();
        swipeTimer = new Timer();

        imageModelArrayList = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        pager = findViewById(R.id.pager);
        idicator = findViewById(R.id.idicator);
        recyclerView = findViewById(R.id.recycler_view);
        coordinator = findViewById(R.id.coordinator);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        orderArrylist = new ArrayList<>();
        arrayList = new ArrayList<>();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);

            if (!subTitle.equals("")) {
                SpannableStringBuilder str = new SpannableStringBuilder(SharedPreference.getString(Constants.Currency) + " " + subTitle);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                getSupportActionBar().setSubtitle(str);
                toolbarTitle = str.toString();
            }
        }
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.fair_blue),
                ContextCompat.getColor(this, R.color.feint_blue),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.fair_blue));
        getViewPagerImages();
        categoryAdapter = new ItemAdapter(this, arrayList, orderArrylist, "1");
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen._1sdp));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);
        setListener();
        setCartItemCount();
    }

    private void setListener() {
        categoryAdapter.setCommonClicks(new CommonClicks() {
            @Override
            public void onItemClick(Object obj) {
                showPopUpDialog((Gs_item) obj);
            }

            @Override
            public void onChartClick(Object obj) {
//            CommonFunctions.showOrderDialog( SharedPreference.getString( Constants.UserId ), SubSubItemActivity.this, title, SharedPreference.getString( Constants.Currency ), ((Gs_item) obj).getNet_rate(), (Gs_item) obj, new CommonListener() {
//               @Override
//               public void onSuccess(Object object) {
//                  if (object instanceof String) {
//                     String obj = object.toString();
//                     if (obj.equals( "Success" )) {
//                        invalidateOptionsMenu();
//                     }
//                  } else {
//                     CommonFunctions.showSuccessFailDialog( SubSubItemActivity.this );
//                  }
//               }
//
//               @Override
//               public void onFailure(String reason) {
//                  Toast.makeText( SubSubItemActivity.this, reason, Toast.LENGTH_SHORT ).show();
//               }
//            } );
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    private void showPopUpDialog(Gs_item item) {
        AlertDialog.Builder builder;
        final AlertDialog alertDialog;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout;
        if (inflater != null) {
            layout = inflater.inflate(R.layout.alert_item_details, coordinator, false);
            builder = new AlertDialog.Builder(this);
            builder.setView(layout);

            TextView details = layout.findViewById(R.id.details);
            TextView tranDateV = layout.findViewById(R.id.tran_date_v);
            TextView loadingV = layout.findViewById(R.id.loading_v);
            TextView insuranceV = layout.findViewById(R.id.insurance_v);
            TextView gstV = layout.findViewById(R.id.gst_v);
            TextView netRateV = layout.findViewById(R.id.net_rate_v);
            Button btnOk = layout.findViewById(R.id.btn_ok);
            ImageView imgDelete = layout.findViewById(R.id.img_delete);
            String currency = SharedPreference.getString(Constants.Currency);

            details.setText(item.getGs_item_name() != null && !item.getGs_item_name().equalsIgnoreCase("") ? item.getGs_item_name() : "");

            tranDateV.setText(item.getBase_rate() != null && !item.getBase_rate().equalsIgnoreCase("") ? currency + " " + item.getBase_rate() : "");
            loadingV.setText(item.getLoading_charges() != null && !item.getLoading_charges().equalsIgnoreCase("") ? currency + " " + item.getLoading_charges() : "");
            insuranceV.setText(item.getInsurance_charges() != null && !item.getInsurance_charges().equalsIgnoreCase("") ? currency + " " + item.getInsurance_charges() : "");
            gstV.setText(item.getGst_percentage() != null && !item.getGst_percentage().equalsIgnoreCase("") ? currency + " " + item.getGst_percentage() : "");
            netRateV.setText(item.getNet_rate() != null && !item.getNet_rate().equalsIgnoreCase("") ? currency + " " + item.getNet_rate() : "");

            alertDialog = builder.create();

            imgDelete.setOnClickListener(V -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });

            btnOk.setOnClickListener(V -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });

            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.show();

            ViewGroup parent = (ViewGroup) layout.getParent();
            parent.setPadding(0, 0, 0, 0);
        }
    }

    private void getViewPagerImages() {
        RPC.getBannerImages(new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                message = (Message) object;
                if (message != null) {
                    if (message.getSuccess().equals("1")) {
                        if (message.getBanner() != null && message.getBanner().size() > 0) {
                            imageModelArrayList = message.getBanner();
                            NUM_PAGES = imageModelArrayList.size();
                            setViewPager();
                        }
                    } else {
                        Toast.makeText(SubSubItemActivity.this, message.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(SubSubItemActivity.this, reason, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getGroupList(String Id) {
        swipeRefreshLayout.setRefreshing(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.getGroupDetail(Id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("4")) {
                            arrayList.clear();
                            arrayList.addAll(message.getGs_item());
                            if (arrayList.size() == 0) {
                                Toast.makeText(SubSubItemActivity.this, "No item Available", Toast.LENGTH_LONG).show();
                            }
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(SubSubItemActivity.this, R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            categoryAdapter.notifyDataSetChanged();
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SubSubItemActivity.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                progressDialog.dismiss();
                if (t instanceof IOException) {
                    Toast.makeText(SubSubItemActivity.this, "Check network connection..", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(SubSubItemActivity.this, "data cannot parse..", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                }
            }
        });

    }

    private void setViewPager() {
        slidingImageAdapter = new SlidingImageAdapter(imageModelArrayList, this, "FragmentPage");
        pager.setAdapter(slidingImageAdapter);
        idicator.setViewPager(pager);
        final float density = getResources().getDisplayMetrics().density;
        idicator.setRadius(5 * density);
        final Runnable update = () -> {
            if (currentpages == NUM_PAGES) {
                currentpages = 0;
            }
            pager.setCurrentItem(currentpages++, true);
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

        idicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void setCartItemCount() {
        cartItemCount = CommonFunctions.getCartCount(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            performBack();
            return true;
        } else if (item.getItemId() == R.id.order_item) {
            if (group_name.is_orderable.equals("0")) {
                Toast.makeText(SubSubItemActivity.this, group_name.booking_closed_msg, Toast.LENGTH_SHORT).show();
            } else {
                placeOrder();
            }

            return true;
        }
//      else if (item.getItemId() == R.id.action_item) {
//         startActivity( new Intent( this, CartItemActivity.class ), ActivityOptions.makeCustomAnimation( this, R.anim.enter_from_left, R.anim.exit_to_right ).toBundle() );
//         finish();
//      }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCartItemCount();
//      MenuItem itemCart = menu.findItem( R.id.action_item );
//      LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
//      CommonFunctions.setBadgeCount( this, icon, String.valueOf( cartItemCount ) );

        if (group_name.is_orderable.equals("0")) {
            menu.findItem(R.id.order_item).setIcon(R.drawable.error_white);
        } else {
            menu.findItem(R.id.order_item).setIcon(R.drawable.ic_add_cart_white);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    void performBack() {
        try {
            if (swipeTimer != null) {
                swipeTimer.cancel();
                swipeTimer.purge();
            }
            if (delayhandler != null) {
                delayhandler.removeCallbacks(null);
            }
        } catch (Exception ignored) {
        }
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    void placeOrder() {
        CommonFunctions.showOrderDialog(SharedPreference.getString(Constants.UserId), SubSubItemActivity.this, title, SharedPreference.getString(Constants.Currency), group_name.getBase_rate(), group_name, new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                if (object instanceof String) {
                    String obj = object.toString();
                    if (obj.equals("Success")) {
                        // invalidateOptionsMenu();
                        CommonFunctions.showSuccessFailDialog(SubSubItemActivity.this);
                    }
                } else {
                    CommonFunctions.showSuccessFailDialog(SubSubItemActivity.this);
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(SubSubItemActivity.this, reason, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        performBack();
        super.onBackPressed();
    }
}