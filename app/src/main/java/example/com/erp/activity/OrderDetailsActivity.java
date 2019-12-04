package example.com.erp.activity;
/*
Create by user on 14-02-2019 at 06:54 PM for ERP
*/

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.adapter.OrderItemAdapter;
import example.com.erp.model.Message;
import example.com.erp.model.Orders;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import example.com.erp.utility.Constants;
import example.com.erp.utility.ItemOffsetDecoration;
import example.com.erp.utility.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView text_date, text_v_no, text_sale;
    String id, transDetail;
    ArrayList<Orders.OrderDetail.Trn_details> transactionItems;
    Button orderReceived;
    private ProgressDialog progressDialog;
    private OrderItemAdapter transactionItemAdapter;
    private Orders.OrderList.Trn_statement transaction;
    private String orderPosition = "";
    private boolean statusUpdated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_item_transaction);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("tran_id");
            transDetail = getIntent().getStringExtra("tran_detail");
            orderPosition = getIntent().getStringExtra("orderPosition ");

            Log.e("Android->>", "onCreate: " + getIntent().getStringExtra("orderPosition "));

        }
        if (transDetail != null && !transDetail.equals("")) {
            transaction = new Gson().fromJson(transDetail, Orders.OrderList.Trn_statement.class);
        }
        assignViews();
        getTransactionItem(id);

        orderReceived.setOnClickListener(V -> new AlertDialog.Builder(this)
                .setMessage("Are you sure want to submit as you received your order?")
                .setTitle("Update Order Status")
                .setPositiveButton("Received", (dialog, which) -> {
                    dialog.cancel();
                    dialog.dismiss();
                    changeOrderStatus();
                })
                .setNegativeButton("Not Received", (dialog, which) -> {
                    dialog.cancel();
                    dialog.dismiss();
                })
                .setCancelable(true)
                .create()
                .show());
    }

    public void clickToChallanStatment(View view) {
        startActivity(new Intent(OrderDetailsActivity.this, ChallanStatmentActivity.class).putExtra("tran_id", id));
    }

    private void changeOrderStatus() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.updateOrderStatus(transaction.id, SharedPreference.getString(Constants.UserId));
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("2")) {
                            Toast.makeText(OrderDetailsActivity.this, message.getMessage(), Toast.LENGTH_LONG).show();
                            transaction.status = "11";
                            transaction.status_type = "Completed";
                            statusUpdated = true;
                            loadOrderItems();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(OrderDetailsActivity.this, "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OrderDetailsActivity.this, "Error :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void assignViews() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        transactionItems = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Order Details");
        }

        text_date = findViewById(R.id.transItemDate);
        text_v_no = findViewById(R.id.transItemVoucher);
        text_sale = findViewById(R.id.tran_sale);
        recyclerView = findViewById(R.id.tr_recyclerview);
        orderReceived = findViewById(R.id.order_received);
        loadOrderItems();
    }

    private void loadOrderItems() {
        transactionItemAdapter = new OrderItemAdapter(this, transactionItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen._1sdp));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionItemAdapter);
        setOrderDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            performBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOrderDetails() {
        Log.e("JsonData", new Gson().toJson(transaction));

        int start;
        Spannable wordSpan;

        wordSpan = new SpannableString(transaction.ledger_name);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // ((TextView) findViewById( R.id.ledger_name )).setText( wordSpan );

        ((TextView) findViewById(R.id.t_parti)).setText(String.format("%s %s%s", transaction.voucher, "#", transaction.voucher_no));

        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency)/*transaction.dr_cr.trim()*/, transaction.transaction_amount.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(setOrderColor(Integer.parseInt(transaction.status.trim()))), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // ((TextView) findViewById( R.id.t_amount )).setText( wordSpan );

        findViewById(R.id.transaction_type).setBackgroundColor(setOrderColor(Integer.parseInt(transaction.status.trim())));
        ((TextView) findViewById(R.id.t_order_status)).setText(transaction.status_type);
        ((TextView) findViewById(R.id.t_order_status)).setTextColor(setOrderColor(Integer.parseInt(transaction.status.trim())));

        if (Integer.parseInt(transaction.status.trim()) == 6) {
            // orderReceived.setVisibility( View.VISIBLE );
            orderReceived.setVisibility(View.GONE);
        } else {
            orderReceived.setVisibility(View.GONE);
        }
    }

    private int setOrderColor(int status) {
        switch (status) {
            case 1: {
                return ContextCompat.getColor(this, R.color.statusPending);
            }
            case 2: {
                return ContextCompat.getColor(this, R.color.statusApprove);
            }
            case 3: {
                return ContextCompat.getColor(this, R.color.statusNotApprove);
            }
            case 5: {
                return ContextCompat.getColor(this, R.color.statusShipping);
            }
            case 6: {
                return ContextCompat.getColor(this, R.color.statusShipped);
            }
            case 8: {
                return ContextCompat.getColor(this, R.color.statusDelivered);
            }
            case 11: {
                return ContextCompat.getColor(this, R.color.statusCompleted);
            }
            case 12: {
                return ContextCompat.getColor(this, R.color.statusBilled);
            }
        }
        return ContextCompat.getColor(this, R.color.black);
    }

    private void getTransactionItem(String id) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Orders.OrderDetail> call = apiInterface.getOrderDetails(id);
        call.enqueue(new Callback<Orders.OrderDetail>() {
            @Override
            public void onResponse(@NonNull Call<Orders.OrderDetail> call, @NonNull Response<Orders.OrderDetail> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Orders.OrderDetail message = response.body();
                    if (message != null) {
                        if (message.success.equals("2")) {
                            transactionItems.addAll(message.trn_details);
                            text_sale.setText(transactionItems.get(0).voucher_type);
                            text_date.setText(String.format("Date :%s", transactionItems.get(0).transaction_date));
                            ((TextView) findViewById(R.id.txt_trans_date)).setText(transactionItems.get(0).transaction_date);
                            text_v_no.setText(String.format("V.No :%s", transactionItems.get(0).voucher_no));
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(OrderDetailsActivity.this, R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            transactionItemAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, message.message, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(OrderDetailsActivity.this, "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Orders.OrderDetail> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OrderDetailsActivity.this, "Error :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    void performBack() {
        Log.e("Android->>", "performBack: " + orderPosition);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("orderPosition", statusUpdated ? orderPosition : "");
        setResult(Activity.RESULT_OK, resultIntent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        performBack();
        super.onBackPressed();
    }
}
