package example.com.erp.fragment;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.activity.MainActivity;
import example.com.erp.activity.TransactionDetailsActivity;
import example.com.erp.adapter.TransactionAdapter;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.model.Message;
import example.com.erp.model.Transaction;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import example.com.erp.utility.Constants;
import example.com.erp.utility.ItemOffsetDecoration;
import example.com.erp.utility.SharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment {
    View rootView;
    RecyclerView recyclerView;
    private ArrayList<Transaction> arrayList;
    private TransactionAdapter transactionAdapter;
    private ProgressDialog progressDialog;
    private Context context;
    private SwipeRefreshLayout swipe_refresh_layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frag_transaction, container, false);
        context = container.getContext();
        progressDialog = new ProgressDialog(getContext(), R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        init();

        swipe_refresh_layout.setOnRefreshListener(() -> {
            if (transactionAdapter != null) {
                transactionAdapter.clearData();
                getTransaction();
            }
        });
        return rootView;
    }

    private void init() {
        swipe_refresh_layout = rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = rootView.findViewById(R.id.recycler_transaction);

        try {
            getTransaction();
        } catch (Exception e) {
            Toast.makeText(context, "!User not register..", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        arrayList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(getContext(), arrayList);
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
        transactionAdapter.setCommonClicks(new CommonClicks() {
            @Override
            public void onItemClick(Object obj) {
                Transaction transaction = (Transaction) obj;
                Intent transactionDetail = new Intent(getActivity(), TransactionDetailsActivity.class);
                transactionDetail.putExtra("tran_id", transaction.getId());
                transactionDetail.putExtra("tran_detail", new Gson().toJson(transaction));
                transactionDetail.putExtra("challan_btn", false);
                MainActivity.getInstance().startActivity(transactionDetail, ActivityOptions.makeCustomAnimation(getActivity(), R.anim.enter_from_left, R.anim.exit_to_right).toBundle());
            }

            @Override
            public void onChartClick(Object obj) {

            }
        });
    }

    private void getTransaction() {
        swipe_refresh_layout.setRefreshing(true);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String user_id = SharedPreference.getString(Constants.UserId);
        Call<Message> call = apiInterface.getTransaction(user_id);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                progressDialog.dismiss();
                swipe_refresh_layout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("2")) {
                            arrayList.addAll(message.getTrn_statement());

                            for (int i = 0; i < arrayList.size(); i++) {
                                if (i == 0) {
                                    arrayList.get(i).sameDate = false;
                                } else {
                                    arrayList.get(i).sameDate = arrayList.get(i - 1).getTransaction_date().equals(arrayList.get(i).getTransaction_date());
                                }
                            }
                            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.down_to_up);
                            recyclerView.setLayoutAnimation(controller);
                            transactionAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), message.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error :" + response.errorBody(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                swipe_refresh_layout.setRefreshing(false);
            }
        });
    }
}