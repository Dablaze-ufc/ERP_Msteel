package example.com.erp.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.activity.BillReceiptActivity;
import example.com.erp.activity.OrderDetailsActivity;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.fragment.TransactionFragment;
import example.com.erp.model.Transaction;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;
import example.com.erp.utility.Utils;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    // TODO :- Permission
    private final int PERMISSION_FOR_READ_WRITE = 102;
    int start = 0;
    private ArrayList<Transaction> transactionArrayList;
    private Context context;
    private CommonClicks commonClicks;
    private String showDate = "";

    public TransactionAdapter(Context context, ArrayList<Transaction> arrayList) {
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

      /*View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction, parent, false);
      return new ViewHolder(v);*/
    }

    public void setCommonClicks(CommonClicks clicks) {
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

        wordSpan = new SpannableString(transactionArrayList.get(pos).getLedger_name());
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.ledger_name.setText(wordSpan);

        holder.text_voucher.setText(String.format("%s %s%s", transactionArrayList.get(pos).getVoucher(), "#", transactionArrayList.get(pos).getVoucher_no()));

        start = transactionArrayList.get(pos).getDr_cr().trim().length();
        wordSpan = new SpannableString(String.format("%s %s", transactionArrayList.get(pos).getDr_cr().trim(), transactionArrayList.get(pos).getTransaction_amount().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (transactionArrayList.get(pos).getDr_cr().equals("Cr")) {
            wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_green)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_red_dark)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.text_amount.setText(wordSpan);

        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s %s", "Balance", SharedPreference.getString(Constants.Currency), transactionArrayList.get(pos).getBalance().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, ("Balance").length() + 1, 0);
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), ("Balance").length() + 1, ("Balance").length() + 1 + start, 0);
        /*wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ("Balance").length() + 1, ("Balance").length() + 1 + start, 0);*/
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), ("Balance").length() + 1, ("Balance").length() + start + 1, 0);
        holder.text_balance.setText(wordSpan);

        if (transactionArrayList.get(pos).getDr_cr().equals("Cr")) {
            holder.transaction_type.setBackgroundColor(ContextCompat.getColor(context, R.color.color_green));
        } else {
            holder.transaction_type.setBackgroundColor(ContextCompat.getColor(context, R.color.color_red_dark));
        }

//      holder.parent_layout.setOnClickListener( V -> {
//         if (commonClicks != null) {
//            commonClicks.onItemClick( transactionArrayList.get( pos ) );
//         }
//      } );

        // manage Download Button Status
        holder.downloadBtn.setVisibility(View.VISIBLE);
        if (transactionArrayList.get(pos).getTransaction_file().equals("")) {
            holder.downloadBtn.setImageResource(R.drawable.error);
        } else {
            holder.downloadBtn.setImageResource(R.drawable.download);
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isReadStoragePermissionGranted()) {
                        // Utils.downloadTask(context, transactionArrayList.get( pos ).getTransaction_file());
                        context.startActivity(new Intent(context, BillReceiptActivity.class).putExtra("url", transactionArrayList.get(pos).getTransaction_file()));
                    }
                }
            });
        }

    }

    private void initLayoutTwo(ViewHolderHeader holder, int pos) {
        holder.text_date.setText(transactionArrayList.get(pos).getTransaction_date());
        Spannable wordSpan;
        start = ("Date:").length();
        wordSpan = new SpannableString(String.format("%s %s %s", "---", transactionArrayList.get(pos).getTransaction_date(), "---"));
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, wordSpan.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, wordSpan.length() - 3, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 3, wordSpan.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.text_date.setText(wordSpan);

        wordSpan = new SpannableString(transactionArrayList.get(pos).getLedger_name());
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.ledger_name.setText(wordSpan);

        holder.text_voucher.setText(String.format("%s %s%s", transactionArrayList.get(pos).getVoucher(), "#", transactionArrayList.get(pos).getVoucher_no()));

        /*Amount*/
        start = transactionArrayList.get(pos).getDr_cr().trim().length();
        wordSpan = new SpannableString(String.format("%s %s", transactionArrayList.get(pos).getDr_cr().trim(), transactionArrayList.get(pos).getTransaction_amount().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (transactionArrayList.get(pos).getDr_cr().equals("Cr")) {
            wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_green)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.color_red_dark)), start, wordSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.text_amount.setText(wordSpan);

        /*Balance*/
        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s %s", "Balance", SharedPreference.getString(Constants.Currency), transactionArrayList.get(pos).getBalance().trim()));

        wordSpan.setSpan(new RelativeSizeSpan(0.8f), 0, ("Balance").length() + 1, 0);
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), ("Balance").length() + 1, ("Balance").length() + 1 + start, 0);
        /*wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ("Balance").length() + 1, ("Balance").length() + 1 + start, 0);*/
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), ("Balance").length() + 1, ("Balance").length() + start + 1, 0);
        holder.text_balance.setText(wordSpan);

        if (transactionArrayList.get(pos).getDr_cr().equals("Cr")) {
            holder.transaction_type.setBackgroundColor(ContextCompat.getColor(context, R.color.color_green));
        } else {
            holder.transaction_type.setBackgroundColor(ContextCompat.getColor(context, R.color.color_red_dark));
        }

//      holder.parent_layout.setOnClickListener( V -> {
//         if (commonClicks != null) {
//            commonClicks.onItemClick( transactionArrayList.get( pos ) );
//         }
//      } );

        // manage Download Button Status
        holder.downloadBtn.setVisibility(View.VISIBLE);
        if (transactionArrayList.get(pos).getTransaction_file().equals("")) {
            holder.downloadBtn.setImageResource(R.drawable.error);
        } else {
            holder.downloadBtn.setImageResource(R.drawable.download);
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isReadStoragePermissionGranted()) {
                        // Utils.downloadTask(context, transactionArrayList.get( pos ).getTransaction_file());
                        context.startActivity(new Intent(context, BillReceiptActivity.class).putExtra("url", transactionArrayList.get(pos).getTransaction_file()));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Transaction transaction = transactionArrayList.get(position);
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
        ImageView imageView;
        LinearLayout parent_layout;
        LinearLayout transaction_type;
        ImageButton downloadBtn;

        ViewHolder(View itemView) {
            super(itemView);
            text_date = itemView.findViewById(R.id.t_date);
            text_voucher = itemView.findViewById(R.id.t_parti);
            text_amount = itemView.findViewById(R.id.t_amount);
            text_balance = itemView.findViewById(R.id.t_balance);
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
        ImageView imageView;
        LinearLayout parent_layout;
        LinearLayout transaction_type;
        ImageButton downloadBtn;

        ViewHolderHeader(View itemView) {
            super(itemView);
            text_date = itemView.findViewById(R.id.t_date);
            text_voucher = itemView.findViewById(R.id.t_parti);
            text_amount = itemView.findViewById(R.id.t_amount);
            text_balance = itemView.findViewById(R.id.t_balance);
            ledger_name = itemView.findViewById(R.id.ledger_name);
            imageView = itemView.findViewById(R.id.img_next);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            transaction_type = itemView.findViewById(R.id.transaction_type);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
        }
    }
}
