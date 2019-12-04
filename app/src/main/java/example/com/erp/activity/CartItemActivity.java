package example.com.erp.activity;
/*
Create by user on 14-02-2019 at 04:32 PM for ERP
*/

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import example.com.erp.R;
import example.com.erp.adapter.CartItemAdapter;
import example.com.erp.adapter.SlidingImageAdapter;
import example.com.erp.callback.CartItemCallBack;
import example.com.erp.database.CartItem;
import example.com.erp.database.DBHelper;
import example.com.erp.model.Gs_item;
import example.com.erp.model.ImageModel;
import example.com.erp.model.Message;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;
import example.com.erp.utility.CommonFunctions;
import example.com.erp.utility.Constants;
import example.com.erp.utility.ItemOffsetDecoration;
import example.com.erp.utility.SharedPreference;

public class CartItemActivity extends AppCompatActivity {
    Handler delayhandler;
    Timer swipeTimer;
    ArrayList<CartItem> arrayList;
    CartItemAdapter categoryAdapter;
    String itemCodes = "";
    private CoordinatorLayout coordinator;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager pager;
    private CirclePageIndicator idicator;
    private CardView cartDetail;
    private TextView ledgerName;
    private TextView tAmount;
    private TextView tOrderStatus;
    private TextView codeNumber;
    private Button btn_cancel, btn_place;
    private RecyclerView recyclerView;
    private int currentpages = 0;
    private int NUM_PAGES = 0;
    private Message message;
    private ArrayList<ImageModel> imageModelArrayList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_content_child);
        assignViews();
        btn_cancel.setOnClickListener(V -> {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure want to remove all?")
                    .setTitle("Remove from cart")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        int status = dbHelper.deleteObjects(CartItem.class, arrayList);
                        calculateAmount();
                        cartDetail.setVisibility(View.GONE);
                        findViewById(R.id.cart_operation).setVisibility(View.GONE);
                        loadAdapterData();
                        dialog.cancel();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        dialog.dismiss();
                    })
                    .setCancelable(true)
                    .create()
                    .show();
        });

        btn_place.setOnClickListener(V -> {

            ArrayList<String> itemId = new ArrayList<>();
            ArrayList<String> itemQty = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                itemId.add(arrayList.get(i).getItem_id());
                itemQty.add(arrayList.get(i).getQty());
                itemCodes = arrayList.get(i).getCode();
            }
            String itemIds = CommonFunctions.joinArrayList(itemId, ",");
            String itemQtys = CommonFunctions.joinArrayList(itemQty, ",");

            new AlertDialog.Builder(this)
                    .setMessage("Are you sure want submit order of " + tAmount.getText().toString().trim() + "?")
                    .setTitle("Send Order")
                    .setPositiveButton("Place", (dialog, which) -> {
                        dialog.cancel();
                        dialog.dismiss();
                        SendOrder(itemIds, itemQtys, itemCodes);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        dialog.dismiss();
                    })
                    .setCancelable(true)
                    .create()
                    .show();

        });
        codeNumber.setOnClickListener(V -> {
            setCode();
        });
    }

    private void setCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set code");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> {
            for (int i = 0; i < arrayList.size(); i++) {
                CartItem cartItem = arrayList.get(i);
                cartItem.setCode(input.getText().toString().trim());
                dbHelper.createOrUpdate(cartItem);
            }
            dialog.dismiss();
            dialog.cancel();
            loadAdapterData();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            dialog.dismiss();
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void SendOrder(String itemIds, String itemQtys, String itemCodes) {
        RPC.submitOrderWithMultipleItems(itemIds, itemQtys, itemCodes, SharedPreference.getString(Constants.UserId), new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                int status = dbHelper.deleteObjects(CartItem.class, arrayList);
                CommonFunctions.showSuccessFailDialog(CartItemActivity.this);
                loadAdapterData();
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(CartItemActivity.this, reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignViews() {
        delayhandler = new Handler();
        swipeTimer = new Timer();
        imageModelArrayList = new ArrayList<>();
        coordinator = findViewById(R.id.coordinator);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        pager = findViewById(R.id.pager);
        idicator = findViewById(R.id.idicator);
        cartDetail = findViewById(R.id.cart_detail);
        ledgerName = findViewById(R.id.ledger_name);
        tAmount = findViewById(R.id.t_amount);
        codeNumber = findViewById(R.id.code_number);
        tOrderStatus = findViewById(R.id.t_order_status);
        recyclerView = findViewById(R.id.recycler_view);
        btn_place = findViewById(R.id.btn_place);
        btn_cancel = findViewById(R.id.btn_cancel);
        ledgerName.setText(SharedPreference.getString(Constants.FullName));
        tOrderStatus.setText("PENDING");
        codeNumber.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_edit_product, null), null, null, null);

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout.setEnabled(false);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
        getViewPagerImages();
        dbHelper = new DBHelper(this);
        loadAdapterData();
        setListener();

    }

    private void loadAdapterData() {
        arrayList = (ArrayList<CartItem>) dbHelper.query(CartItem.class, DBHelper.where("user_id", SharedPreference.getString(Constants.UserId)));
        if (arrayList.size() > 0) {
            cartDetail.setVisibility(View.VISIBLE);
            findViewById(R.id.cart_operation).setVisibility(View.VISIBLE);
        }
        categoryAdapter = new CartItemAdapter(this, arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen._1sdp));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(categoryAdapter);
        calculateAmount();
    }

    private void setListener() {
        categoryAdapter.setCallBack(new CartItemCallBack() {
            @Override
            public void onItemClick(Object object, int position) {
                CartItem item = (CartItem) object;
                Gs_item gsItem = new Gs_item();
                gsItem.setId(item.getItem_id());
                gsItem.setGs_item_name(item.getGs_item_name());
                gsItem.setGs_groups_master_table_id(item.getGs_groups_master_table_id());
                gsItem.setBase_rate(item.getBase_rate());
                gsItem.setGs_item_rate(item.getGs_item_rate());
                gsItem.setGross_rate(item.getGross_rate());
                gsItem.setLoading_charges(item.getLoading_charges());
                gsItem.setInsurance_charges(item.getInsurance_charges());
                gsItem.setNet_rate(item.getNet_rate());
                gsItem.setGst_percentage(item.getGst_percentage());
                gsItem.setTax_paid_rate(item.getTax_paid_rate());
                gsItem.setOrder(item.getOrder());
                gsItem.setParent_gs_group_id(item.getParent_gs_group_id());

                showPopUpDialog(gsItem);
            }

            @Override
            public void onItemEdit(Object object, int position) {
                CartItem item = (CartItem) object;
                Gs_item gsItem = new Gs_item();
                gsItem.setId(item.getItem_id());
                gsItem.setGs_item_name(item.getGs_item_name());
                gsItem.setGs_groups_master_table_id(item.getGs_groups_master_table_id());
                gsItem.setBase_rate(item.getBase_rate());
                gsItem.setGs_item_rate(item.getGs_item_rate());
                gsItem.setGross_rate(item.getGross_rate());
                gsItem.setLoading_charges(item.getLoading_charges());
                gsItem.setInsurance_charges(item.getInsurance_charges());
                gsItem.setNet_rate(item.getNet_rate());
                gsItem.setGst_percentage(item.getGst_percentage());
                gsItem.setTax_paid_rate(item.getTax_paid_rate());
                gsItem.setOrder(item.getOrder());
                gsItem.setParent_gs_group_id(item.getParent_gs_group_id());

                updateCartItem(item.getId(), gsItem, item.getGrp_name());
            }

            @Override
            public void onItemDelete(Object object, int position) {
                CartItem Item = (CartItem) object;
                deleteItem(Item, position);
            }
        });
    }

    private void updateCartItem(int orderId, Gs_item gsItem, String grp_name) {
        CommonFunctions.editOrderDialog(orderId, CartItemActivity.this, grp_name, SharedPreference.getString(Constants.Currency), gsItem.getNet_rate(), gsItem, new CommonListener() {
            @Override
            public void onSuccess(Object object) {
                if (object instanceof String) {
                    String obj = object.toString();
                    if (obj.equals("Success")) {
                        loadAdapterData();
                    }
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(CartItemActivity.this, reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem(CartItem cartItem, int position) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure want to remove from cart?")
                .setTitle("Remove from cart")
                .setPositiveButton("Remove", (dialog, which) -> {
                    dbHelper.deleteById(CartItem.class, cartItem.getId());
                    dialog.cancel();
                    dialog.dismiss();
                    arrayList.remove(position);
                    calculateAmount();
                    if (arrayList.size() == 0) {
                        cartDetail.setVisibility(View.GONE);
                        findViewById(R.id.cart_operation).setVisibility(View.GONE);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    dialog.dismiss();
                })
                .setCancelable(true)
                .create()
                .show();
    }

    private void calculateAmount() {
        long total = 0L;
        for (int i = 0; i < arrayList.size(); i++) {
            double singleAmt = categoryAdapter.calculateAmt(Double.parseDouble(arrayList.get(i).getBase_rate()), Double.parseDouble(arrayList.get(i).getLoading_charges()), Double.parseDouble(arrayList.get(i).getInsurance_charges()), Integer.parseInt(arrayList.get(i).getQty()), Double.parseDouble(arrayList.get(i).getGst_percentage()));
         /*String amt = arrayList.get( i ).getNet_rate().replaceAll( "\\(TP\\)", "" ).trim();
         String qty = arrayList.get( i ).getQty().trim();
         long temp = (long) (Double.parseDouble( amt ) * Integer.parseInt( qty ));*/

            total += singleAmt;
            codeNumber.setText(String.format("Code: %s", arrayList.get(i).getCode()));
        }
        int start = SharedPreference.getString(Constants.Currency).length();
        SpannableString wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), total));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, start, 0);
        tAmount.setText(wordSpan);
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
                        Toast.makeText(CartItemActivity.this, message.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(String reason) {
                Toast.makeText(CartItemActivity.this, reason, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPopUpDialog(Gs_item item) {
        android.app.AlertDialog.Builder builder;
        final android.app.AlertDialog alertDialog;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout;
        if (inflater != null) {
            layout = inflater.inflate(R.layout.alert_item_details, coordinator, false);
            builder = new android.app.AlertDialog.Builder(this);
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

    private void setViewPager() {
        SlidingImageAdapter slidingImageAdapter = new SlidingImageAdapter(imageModelArrayList, this, "FragmentPage");
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

        if (swipeTimer == null)
            swipeTimer = new Timer();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            performBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        performBack();
        super.onBackPressed();
    }
}