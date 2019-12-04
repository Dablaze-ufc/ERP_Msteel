package example.com.erp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.activity.ChallanDetailActivity;
import example.com.erp.activity.MainActivity;
import example.com.erp.activity.OrderDetailsActivity;
import example.com.erp.activity.TransactionDetailsActivity;
import example.com.erp.model.Orders;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private ArrayList<Orders.OrderDetail.Trn_details> transactionItems;
    private Context context;

    public OrderItemAdapter(Context context, ArrayList<Orders.OrderDetail.Trn_details> transactionItems) {
        this.transactionItems = transactionItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Orders.OrderDetail.Trn_details Item = transactionItems.get(position);
        SpannableString wordSpan;
        int start;

        holder.trItem.setText(Item.gs_item_name);
        holder.tr_balance.setText(Item.balance_qty);

        start = SharedPreference.getString(Constants.Currency).length();
        // wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.net_amount.trim()));
        wordSpan = new SpannableString(String.format("%s", Item.net_amount.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, 0, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, 0, 0);
        // holder.trItemAmount.setText(wordSpan);

        holder.txtQty.setText(Item.transaction_qty);

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.transaction_rate.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.txtRate.setText(wordSpan);

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.transaction_item_taxable_amount.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.preTax.setText(wordSpan);

        if (context instanceof OrderDetailsActivity /*|| context instanceof ChallanDetailActivity*/) {
            holder.preTax.setVisibility(View.GONE);
            holder.valueText.setVisibility(View.GONE);
        }

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.gst_amount.trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        // holder.gstRate.setText(wordSpan);

        holder.tr_balance.setVisibility(View.VISIBLE);
        if (context instanceof OrderDetailsActivity) {
            // holder.tr_balance.setVisibility(View.VISIBLE);
            // holder.balanceStartTxt.setVisibility(View.VISIBLE);
            // holder.balanceEndTxt.setVisibility(View.VISIBLE);
        } else if (context instanceof TransactionDetailsActivity) {
            // holder.tr_balance.setVisibility(View.GONE);
            // holder.balanceStartTxt.setVisibility(View.GONE);
            // holder.balanceEndTxt.setVisibility(View.GONE);
        } else if (context instanceof ChallanDetailActivity) {
            holder.balanceQty.setVisibility(View.GONE);
            holder.tr_balance.setVisibility(View.GONE);
            holder.challanNo.setVisibility(View.VISIBLE);
            holder.challanNo.setText(Item.ref_no);
//         holder.challanNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               context.startActivity(new Intent(context, MainActivity.class).putExtra("Page_Index", "2"));
//            }
//         });
        } else {
            // holder.tr_balance.setVisibility(View.GONE);
            // holder.balanceStartTxt.setVisibility(View.GONE);
            // holder.balanceEndTxt.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MainActivity.class).putExtra("Page_Index", "2"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout parentLayout;
        private TextView trItem;
        private TextView trItemAmount;
        private TextView txtQty;
        private TextView txtRate;
        private TextView preTax;
        private TextView gstRate;
        private TextView tr_balance;
        private TextView balanceStartTxt;
        private TextView balanceEndTxt;
        private TextView balanceQty;
        private TextView valueText;
        private TextView challanNo;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            trItem = itemView.findViewById(R.id.tr_item);
            trItemAmount = itemView.findViewById(R.id.tr_item_amount);
            txtQty = itemView.findViewById(R.id.txt_qty);
            txtRate = itemView.findViewById(R.id.txt_rate);
            preTax = itemView.findViewById(R.id.pre_tax);
            gstRate = itemView.findViewById(R.id.gst_rate);
            tr_balance = itemView.findViewById(R.id.tr_balance);
            // balanceStartTxt = itemView.findViewById(R.id.balanceStartTxt);
            // balanceEndTxt = itemView.findViewById(R.id.balanceEndTxt);
            balanceQty = itemView.findViewById(R.id.balanceQty);
            valueText = itemView.findViewById(R.id.valueText);
            challanNo = itemView.findViewById(R.id.challanNo);

        }
    }
}
