package example.com.erp.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.activity.BillReceiptActivity;
import example.com.erp.activity.ChallanDetailActivity;
import example.com.erp.activity.ChallanStatmentActivity;
import example.com.erp.activity.MainActivity;
import example.com.erp.callback.OrderItemClicks;
import example.com.erp.fragment.OrdersFragment;
import example.com.erp.model.Orders;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;
import example.com.erp.utility.Utils;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    // TODO :- Permission
    private final int PERMISSION_FOR_READ_WRITE = 102;
    int start = 0;
    private ArrayList<Orders.OrderList.Trn_statement> transactionArrayList;
    private Context context;
    private OrderItemClicks commonClicks;
    private String showDate = "";

    public OrderAdapter(Context context, ArrayList<Orders.OrderList.Trn_statement> arrayList) {
        this.context = context;
        this.transactionArrayList = arrayList;
    }

    public void clearData() {
        if (transactionArrayList != null) {
            transactionArrayList.clear();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE) {//hide header
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_TWO) {//header
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
            return new ViewHolderHeader(view);
        } else {
            return null;
        }
    }

    public void setCommonClicks(OrderItemClicks clicks) {
        this.commonClicks = clicks;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case TYPE_ONE:
                initLayoutOne((ViewHolder) holder, position);
                break;
            case TYPE_TWO:
                initLayoutTwo((ViewHolderHeader) holder, position);
                break;
            default:
                break;
        }
    }

    private void initLayoutOne(ViewHolder holder, int pos) {
        Spannable wordSpan;

        wordSpan = new SpannableString(transactionArrayList.get(pos).ledger_name);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // holder.ledger_name.setText( wordSpan );

        holder.ledger_name.setVisibility(View.GONE);
        holder.text_voucher.setTextSize(15);
        holder.text_voucher.setTypeface(holder.text_voucher.getTypeface(), Typeface.BOLD);

        if (context instanceof ChallanStatmentActivity) {
            holder.ledger_name.setVisibility(View.VISIBLE);
            holder.ledger_name.setText(wordSpan);
            holder.text_voucher.setTextSize(13);
            // holder.text_voucher.setTypeface(holder.text_voucher.getTypeface(), Typeface.DEFAULT.getStyle());
            holder.text_voucher.setTypeface(Typeface.DEFAULT);
        }

        holder.text_voucher.setText(String.format("%s %s%s", transactionArrayList.get(pos).voucher, "#", transactionArrayList.get(pos).voucher_no));

        /*Amount*/
        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency)/*transactionArrayList.get(pos).dr_cr.trim()*/, transactionArrayList.get(pos).transaction_amount.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(setOrderColor(Integer.parseInt(transactionArrayList.get(pos).status.trim()))), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.text_balance.setText(transactionArrayList.get(pos).status_type.trim());
        holder.text_balance.setTextColor(setOrderColor(Integer.parseInt(transactionArrayList.get(pos).status.trim())));

        holder.transaction_type.setBackgroundColor(setOrderColor(Integer.parseInt(transactionArrayList.get(pos).status.trim())));

        holder.parent_layout.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onItemClick(transactionArrayList.get(pos), pos);
            }
        });

        // manage Download Button Status
        holder.text_amount.setVisibility(View.GONE);

        if (context instanceof ChallanStatmentActivity) {
            holder.downloadBtn.setVisibility(View.VISIBLE);
            holder.text_amount.setVisibility(View.VISIBLE);
            holder.text_amount.setText(wordSpan);
        }

        if (transactionArrayList.get(pos).transaction_file == null || transactionArrayList.get(pos).transaction_file.equals("")) {
            holder.downloadBtn.setImageResource(R.drawable.error);
        } else {
            holder.downloadBtn.setImageResource(R.drawable.download);
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isReadStoragePermissionGranted()) {
                        // Utils.downloadTask(context, transactionArrayList.get( pos ).transaction_file);
                        context.startActivity(new Intent(context, BillReceiptActivity.class).putExtra("url", transactionArrayList.get(pos).transaction_file));
                    }
                }
            });
        }
    }

    private void initLayoutTwo(ViewHolderHeader holder, int pos) {
//      holder.text_date.setVisibility(View.GONE);

        Spannable wordSpan;
        start = ("Date:").length();
        wordSpan = new SpannableString(String.format("%s %s %s", "---", transactionArrayList.get(pos).transaction_date, "---"));
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, wordSpan.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, wordSpan.length() - 3, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 3, wordSpan.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.text_date.setText(wordSpan);

        holder.text_date.setText(transactionArrayList.get(pos).transaction_date);
//      String[] separatedDate = transactionArrayList.get( pos ).transaction_date.split("-");
//      holder.dateMonthTxt.setText(separatedDate[0] + "-" + separatedDate[1]);
//      holder.yearTxt.setText(separatedDate[2]);

        wordSpan = new SpannableString(transactionArrayList.get(pos).ledger_name);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // holder.ledger_name.setText( wordSpan );

        holder.ledger_name.setVisibility(View.GONE);
        holder.text_voucher.setTextSize(15);
        holder.text_voucher.setTypeface(holder.text_voucher.getTypeface(), Typeface.BOLD);

        if (context instanceof ChallanStatmentActivity) {
            holder.ledger_name.setVisibility(View.VISIBLE);
            holder.ledger_name.setText(wordSpan);
            holder.text_voucher.setTextSize(13);
            // holder.text_voucher.setTypeface(holder.text_voucher.getTypeface(), Typeface.DEFAULT);
            holder.text_voucher.setTypeface(Typeface.DEFAULT);
        }

        holder.text_voucher.setText(String.format("%s %s%s", transactionArrayList.get(pos).voucher, "#", transactionArrayList.get(pos).voucher_no));

        /*Amount*/
        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency)/* transactionArrayList.get(pos).dr_cr.trim()*/, transactionArrayList.get(pos).transaction_amount.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(setOrderColor(Integer.parseInt(transactionArrayList.get(pos).status.trim()))), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // holder.text_amount.setText( wordSpan );

//      if (context.getClass().equals(OrdersFragment.class)) {
//         holder.text_amount.setText("000");
//      }

        /*Balance*/
        holder.text_balance.setText(transactionArrayList.get(pos).status_type.trim());
        holder.text_balance.setTextColor(setOrderColor(Integer.parseInt(transactionArrayList.get(pos).status.trim())));

        holder.transaction_type.setBackgroundColor(setOrderColor(Integer.parseInt(transactionArrayList.get(pos).status.trim())));

        holder.parent_layout.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onItemClick(transactionArrayList.get(pos), pos);
            }
        });

        // manage Download Button Status
        holder.text_amount.setVisibility(View.GONE);

        if (context instanceof ChallanStatmentActivity) {
            holder.downloadBtn.setVisibility(View.VISIBLE);
            holder.text_amount.setVisibility(View.VISIBLE);
            holder.text_amount.setText(wordSpan);
        }


        if (transactionArrayList.get(pos).transaction_file == null || transactionArrayList.get(pos).transaction_file.equals("")) {
            holder.downloadBtn.setImageResource(R.drawable.error);
        } else {
            holder.downloadBtn.setImageResource(R.drawable.download);
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isReadStoragePermissionGranted()) {
                        // Utils.downloadTask(context, transactionArrayList.get( pos ).transaction_file);
                        context.startActivity(new Intent(context, BillReceiptActivity.class).putExtra("url", transactionArrayList.get(pos).transaction_file));
                    }
                }
            });
        }
    }

    private int setOrderColor(int status) {
        switch (status) {
            case 1: {
                return ContextCompat.getColor(context, R.color.statusPending);
            }
            case 2: {
                return ContextCompat.getColor(context, R.color.statusApprove);
            }
            case 3: {
                return ContextCompat.getColor(context, R.color.statusNotApprove);
            }
            case 5: {
                return ContextCompat.getColor(context, R.color.statusShipping);
            }
            case 6: {
                return ContextCompat.getColor(context, R.color.statusShipped);
            }
            case 8: {
                return ContextCompat.getColor(context, R.color.statusDelivered);
            }
            case 11: {
                return ContextCompat.getColor(context, R.color.statusCompleted);
            }
            case 12: {
                return ContextCompat.getColor(context, R.color.statusBilled);
            }
        }
        return ContextCompat.getColor(context, R.color.black);
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Orders.OrderList.Trn_statement transaction = transactionArrayList.get(position);
        if (!transaction.sameDate) {
            return TYPE_TWO;
        } else {
            return TYPE_ONE;
        }
    }

    private boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permission", "Permission is granted1");
                return true;
            } else {
                Log.v("Permission", "Permission is revoked1");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FOR_READ_WRITE);
                return false;
            }
        } else {
            Log.v("Permission", "Permission is granted2");
            return true;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_date;
        TextView text_voucher;
        TextView text_amount;
        TextView text_balance;
        TextView ledger_name;
        //      TextView dateMonthTxt;
//      TextView yearTxt;
        ImageView imageView;
        ImageButton downloadBtn;
        LinearLayout parent_layout;
        LinearLayout transaction_type;

        ViewHolder(View itemView) {
            super(itemView);
            text_date = itemView.findViewById(R.id.t_date);
            text_voucher = itemView.findViewById(R.id.t_parti);
            text_amount = itemView.findViewById(R.id.t_amount);
            text_balance = itemView.findViewById(R.id.t_balance);
//         dateMonthTxt = itemView.findViewById( R.id.dateMonthTxt );
//         yearTxt = itemView.findViewById( R.id.yearTxt );
            ledger_name = itemView.findViewById(R.id.ledger_name);
            imageView = itemView.findViewById(R.id.img_next);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            transaction_type = itemView.findViewById(R.id.transaction_type);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
            text_date.setVisibility(View.GONE);
        }
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder {
        TextView text_date;
        TextView text_voucher;
        TextView text_amount;
        TextView text_balance;
        TextView ledger_name;
        //      TextView dateMonthTxt;
//      TextView yearTxt;
        ImageView imageView;
        ImageButton downloadBtn;
        LinearLayout parent_layout;
        LinearLayout transaction_type;

        ViewHolderHeader(View itemView) {
            super(itemView);
            text_date = itemView.findViewById(R.id.t_date);
            text_voucher = itemView.findViewById(R.id.t_parti);
            text_amount = itemView.findViewById(R.id.t_amount);
            text_balance = itemView.findViewById(R.id.t_balance);
            ledger_name = itemView.findViewById(R.id.ledger_name);
//         dateMonthTxt = itemView.findViewById( R.id.dateMonthTxt );
//         yearTxt = itemView.findViewById( R.id.yearTxt );
            imageView = itemView.findViewById(R.id.img_next);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            transaction_type = itemView.findViewById(R.id.transaction_type);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
        }
    }
}
