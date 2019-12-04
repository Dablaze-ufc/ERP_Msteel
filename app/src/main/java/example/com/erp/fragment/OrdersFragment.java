package example.com.erp.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import example.com.erp.R;
import example.com.erp.activity.MainActivity;
import example.com.erp.activity.OrderDetailsActivity;
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

public class OrdersFragment extends Fragment {
    //Wigdet
    View rootView;
    RecyclerView recyclerView;
    private ArrayList<Orders.OrderList.Trn_statement> arrayList;
    private OrderAdapter transactionAdapter;
    private ProgressDialog progressDialog;
    private Context context;
    private SwipeRefreshLayout swipe_refresh_layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_orders, container, false);
        context = container.getContext();
        progressDialog = new ProgressDialog(getContext(), R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        init();

        swipe_refresh_layout.setOnRefreshListener(() -> {
            if (transactionAdapter != null) {
                transactionAdapter.clearData();
                getOrders();
            }
        });
        return rootView;
    }

    private void init() {
        swipe_refresh_layout = rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = rootView.findViewById(R.id.recycler_transaction);

        try {
            getOrders();
        } catch (Exception e) {
            Toast.makeText(context, "!User not register..", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        arrayList = new ArrayList<>();
        transactionAdapter = new OrderAdapter(getContext(), arrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen._1sdp));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(transactionAdapter);
        setListener();
    }

    private void setListener() {
        transactionAdapter.setCommonClicks(new OrderItemClicks() {
            @Override
            public void onItemClick(Object obj, int position) {
                Log.e("Android->>", "onItemClick: " + position);
                Orders.OrderList.Trn_statement transaction = (Orders.OrderList.Trn_statement) obj;
                Intent transactionDetail = new Intent(getActivity(), OrderDetailsActivity.class);
                transactionDetail.putExtra("tran_id", transaction.id);
                transactionDetail.putExtra("orderPosition", String.valueOf(position));
                transactionDetail.putExtra("tran_detail", new Gson().toJson(transaction));
                MainActivity.getInstance().startActivityForResult(transactionDetail, Constants.VIEW_ORDER);

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
        Call<Orders.OrderList> call = apiInterface.getOrders(user_id);
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
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            transactionAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), message.message, Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Orders.OrderList> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                swipe_refresh_layout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.VIEW_ORDER) {
            Log.e("Android->>", "onActivityResult: 1");
            if (resultCode == Activity.RESULT_OK) {
                getOrders();
            }
        }
        // In fragment class callback
    }
}