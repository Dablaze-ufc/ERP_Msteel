package example.com.erp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.adapter.OrderAdapter;
import example.com.erp.callback.OrderItemClicks;
import example.com.erp.model.Orders;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import example.com.erp.utility.Constants;
import example.com.erp.utility.ItemOffsetDecoration;
import example.com.erp.utility.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallanStatmentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private SwipeRefreshLayout swipe_refresh_layout;
    private OrderAdapter transactionAdapter;
    private ArrayList<Orders.OrderList.Trn_statement> arrayList;
    private ProgressDialog progressDialog;
    private String transactionId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challan_statment);

        if (getIntent() != null) {
            transactionId = getIntent().getStringExtra("tran_id");
        }
        registerIds();

    }

    // TODO :- Methods
    void registerIds() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recycler_transaction);
        progressDialog = new ProgressDialog(ChallanStatmentActivity.this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);

        swipe_refresh_layout.setOnRefreshListener(() -> {
            if (transactionAdapter != null) {
                transactionAdapter.clearData();
                getOrders();
            }
        });

        setRecycler();
    }

    void setRecycler() {
        arrayList = new ArrayList<>();
        getOrders();
        transactionAdapter = new OrderAdapter(ChallanStatmentActivity.this, arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(ChallanStatmentActivity.this, R.dimen._1sdp));
        recyclerView.setLayoutManager(new LinearLayoutManager(ChallanStatmentActivity.this));
        recyclerView.setAdapter(transactionAdapter);
        setListener();
    }

    private void setListener() {
        transactionAdapter.setCommonClicks(new OrderItemClicks() {
            @Override
            public void onItemClick(Object obj, int position) {
                Log.e("Android->>", "onItemClick: " + position);
                Orders.OrderList.Trn_statement transaction = (Orders.OrderList.Trn_statement) obj;
                /*Intent transactionDetail = new Intent( ChallanStatmentActivity.this, OrderDetailsActivity.class );
                transactionDetail.putExtra( "tran_id", transaction.id );
                transactionDetail.putExtra( "orderPosition", String.valueOf( position ) );
                transactionDetail.putExtra( "tran_detail", new Gson().toJson( transaction ) );
                MainActivity.getInstance().startActivityForResult( transactionDetail, Constants.VIEW_ORDER );*/

                Intent transactionDetail = new Intent(ChallanStatmentActivity.this, ChallanDetailActivity.class);
                transactionDetail.putExtra("tran_id", transaction.id);
                transactionDetail.putExtra("orderPosition", String.valueOf(position));
                transactionDetail.putExtra("tran_detail", new Gson().toJson(transaction));
                startActivity(transactionDetail);

                /*MainActivity.getInstance().startActivity( transactionDetail, ActivityOptions.makeCustomAnimation( getActivity(), R.anim.enter_from_left, R.anim.exit_to_right ).toBundle() );*/
            }
        });
    }

    private void getOrders() {
        swipe_refresh_layout.setRefreshing(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String user_id = SharedPreference.getString(Constants.UserId);
        Call<Orders.OrderList> call;
        if (transactionId.equals("")) {
            call = apiInterface.getChallanAll(user_id);
            //call = apiInterface.getChallan( user_id, null );
        } else {
            call = apiInterface.getChallan(user_id, transactionId);
        }

        call.enqueue(new Callback<Orders.OrderList>() {
            @Override
            public void onResponse(@NonNull Call<Orders.OrderList> call, @NonNull Response<Orders.OrderList> response) {
                progressDialog.dismiss();
                swipe_refresh_layout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Orders.OrderList message = response.body();
                    if (message != null) {
                        if (message.success.equals("2")) {
                            arrayList.clear();
                            arrayList.addAll(message.trn_statement);
                            // Collections.reverse(arrayList);
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i == 0) {
                                    arrayList.get(i).sameDate = false;
                                } else {
                                    arrayList.get(i).sameDate = arrayList.get(i - 1).transaction_date.equals(arrayList.get(i).transaction_date);
                                }
//                        arrayList.get( i ).sameDate = false;
                            }
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ChallanStatmentActivity.this, R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            transactionAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ChallanStatmentActivity.this, message.message, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ChallanStatmentActivity.this, "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Orders.OrderList> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                swipe_refresh_layout.setRefreshing(false);
            }
        });
    }

    // TODO :- Button Click
    public void clickToBack(View view) {
        finish();
    }
}
