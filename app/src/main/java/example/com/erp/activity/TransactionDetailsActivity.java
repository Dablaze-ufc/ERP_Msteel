package example.com.erp.activity;
/*
Create by user on 14-02-2019 at 06:54 PM for ERP
*/

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import example.com.erp.adapter.TransactionItemAdapter;
import example.com.erp.model.Message;
import example.com.erp.model.Transaction;
import example.com.erp.model.TransactionItem;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import example.com.erp.utility.Constants;
import example.com.erp.utility.ItemOffsetDecoration;
import example.com.erp.utility.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView text_date, text_v_no, text_sale;
    String id, transDetail;
    ArrayList<TransactionItem> transactionItems;
    Button challanBtn;
    private ProgressDialog progressDialog;
    private TransactionItemAdapter transactionItemAdapter;
    private Transaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_item_transaction);

        if (getIntent() != null) {
            id = getIntent().getStringExtra("tran_id");
            transDetail = getIntent().getStringExtra("tran_detail");
            Boolean challanBtnVisibility = getIntent().getBooleanExtra("challan_btn", false);
            challanBtn = findViewById(R.id.challanBtn);
            if (!challanBtnVisibility) {
                challanBtn.setVisibility(View.GONE);
            }

        }
        if (transDetail != null && !transDetail.equals("")) {
            transaction = new Gson().fromJson(transDetail, Transaction.class);
        }
        assignViews();
        getTransactionItem(id);
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
            getSupportActionBar().setTitle("Transaction Details");
        }

        text_date = findViewById(R.id.transItemDate);
        text_v_no = findViewById(R.id.transItemVoucher);
        text_sale = findViewById(R.id.tran_sale);
        recyclerView = findViewById(R.id.tr_recyclerview);

        transactionItemAdapter = new TransactionItemAdapter(this, transactionItems);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen._1sdp));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(transactionItemAdapter);
        setTransactionDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            performBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTransactionDetail() {
        int start;
        Spannable wordSpan;

        wordSpan = new SpannableString(transaction.getLedger_name());
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((TextView) findViewById(R.id.ledger_name)).setText(wordSpan);

        ((TextView) findViewById(R.id.t_parti)).setText(String.format("%s %s%s", transaction.getVoucher(), "#", transaction.getVoucher_no()));

        start = transaction.getDr_cr().trim().length();
        wordSpan = new SpannableString(String.format("%s %s", transaction.getDr_cr().trim(), transaction.getTransaction_amount().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (transaction.getDr_cr().equals("Cr")) {
            wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_green)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_red_dark)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        ((TextView) findViewById(R.id.t_amount)).setText(wordSpan);

        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s %s", "Balance", SharedPreference.getString(Constants.Currency), transaction.getBalance().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, ("Balance").length() + 1, 0);
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), ("Balance").length() + 1, ("Balance").length() + 1 + start, 0);
        /*wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ("Balance").length() + 1, ("Balance").length() + 1 + start, 0);*/
        wordSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), ("Balance").length() + 1, ("Balance").length() + start + 1, 0);
//      ((TextView) findViewById( R.id.t_balance )).setText( wordSpan );

        if (transaction.getDr_cr().equals("Cr")) {
            findViewById(R.id.transaction_type).setBackgroundColor(ContextCompat.getColor(this, R.color.color_green));
        } else {
            findViewById(R.id.transaction_type).setBackgroundColor(ContextCompat.getColor(this, R.color.color_red_dark));
        }

    }

    private void getTransactionItem(String id) {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.getTransactionDetails(id);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("2")) {
                            transactionItems.addAll(message.getTrn_details());
                            text_sale.setText(transactionItems.get(0).getVoucher_type());
                            text_date.setText(String.format("Date :%s", transactionItems.get(0).getTransaction_date()));
                            ((TextView) findViewById(R.id.txt_trans_date)).setText(transactionItems.get(0).getTransaction_date());
                            text_v_no.setText(String.format("V.No :%s", transactionItems.get(0).getVoucher_no()));
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(TransactionDetailsActivity.this, R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            transactionItemAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TransactionDetailsActivity.this, message.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TransactionDetailsActivity.this, "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TransactionDetailsActivity.this, "Error :" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    void performBack() {
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        performBack();
        super.onBackPressed();
    }
}
