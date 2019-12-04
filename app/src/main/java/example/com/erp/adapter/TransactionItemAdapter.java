package example.com.erp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.model.TransactionItem;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class TransactionItemAdapter extends RecyclerView.Adapter<TransactionItemAdapter.ViewHolder> {

    private ArrayList<TransactionItem> transactionItems;
    private Context context;

    public TransactionItemAdapter(Context context, ArrayList<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_transaction_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TransactionItem Item = transactionItems.get(position);
        SpannableString wordSpan;
        int start;

        holder.trItem.setText(Item.getGs_item_name());

        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.getNet_amount().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.trItemAmount.setText(wordSpan);

        holder.txtQty.setText(Item.getTransaction_qty());

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.getTransaction_rate().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.txtRate.setText(wordSpan);

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.getTransaction_item_taxable_amount().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.preTax.setText(wordSpan);

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), Item.getGst_amount().trim()));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.gstRate.setText(wordSpan);
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
        private TextView valueText;

        public ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            trItem = itemView.findViewById(R.id.tr_item);
            trItemAmount = itemView.findViewById(R.id.tr_item_amount);
            txtQty = itemView.findViewById(R.id.txt_qty);
            txtRate = itemView.findViewById(R.id.txt_rate);
            preTax = itemView.findViewById(R.id.pre_tax);
            gstRate = itemView.findViewById(R.id.gst_rate);
            valueText = itemView.findViewById(R.id.valueText);

        }
    }
}
