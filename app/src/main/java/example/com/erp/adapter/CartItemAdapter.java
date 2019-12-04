package example.com.erp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.callback.CartItemCallBack;
import example.com.erp.database.CartItem;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private CartItemCallBack callBack;
    private ArrayList<CartItem> transactionItems;
    private Context context;

    public CartItemAdapter(Context context, ArrayList<CartItem> transactionItems) {
        this.transactionItems = transactionItems;
        this.context = context;
    }

    public void setCallBack(CartItemCallBack cartItemCallBack) {
        this.callBack = cartItemCallBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ordered_cart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem Item = transactionItems.get(position);
        Log.e("JsonData", new Gson().toJson(Item));
        SpannableString wordSpan;
        int start;

        holder.trItem.setText(Item.getGs_item_name());
        holder.trGrp.setText(Item.getGrp_name());

        start = SharedPreference.getString(Constants.Currency).length();
        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), calculateAmt(Double.parseDouble(Item.getBase_rate()), Double.parseDouble(Item.getLoading_charges()), Double.parseDouble(Item.getInsurance_charges()), Integer.parseInt(Item.getQty()), Double.parseDouble(Item.getGst_percentage()))));

        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.trItemAmount.setText(wordSpan);

        holder.txtQty.setText(Item.getQty());

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), getProductRate(Double.parseDouble(Item.getBase_rate()), Double.parseDouble(Item.getLoading_charges()), Double.parseDouble(Item.getInsurance_charges()))));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.txtRate.setText(wordSpan);

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), String.valueOf(getTaxableAmt(Double.parseDouble(Item.getBase_rate()), Double.parseDouble(Item.getLoading_charges()), Double.parseDouble(Item.getInsurance_charges()), Integer.parseInt(Item.getQty())))));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.loading.setText(wordSpan);

        wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), getGSTAmt(Integer.parseInt(Item.getQty()), Double.parseDouble(Item.getGst_percentage()))));
        wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
        wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, start, 0);
        wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), 0, start, 0);
        holder.gstRate.setText(wordSpan);

        holder.icDeleteCart.setOnClickListener(V -> {
            if (callBack != null) {
                callBack.onItemDelete(Item, position);
            }
        });

        holder.icEditCart.setOnClickListener(V -> {
            if (callBack != null) {
                callBack.onItemEdit(Item, position);
            }
        });
        holder.parentLayout.setOnClickListener(V -> {
            if (callBack != null) {
                callBack.onItemClick(Item, position);
            }
        });
    }

    public double calculateAmt(double base, double loading, double insurance, int qty, double gst) {
        double taxableAmt = getTaxableAmt(base, loading, insurance, qty);
        double gstAmt = getGSTAmt(qty, gst);
        return gstAmt + taxableAmt;
    }

    private double getTaxableAmt(double base, double loading, double insurance, int qty) {
        double productRate = getProductRate(base, loading, insurance);
        return productRate * qty;
    }

    private double getProductRate(double base, double loading, double insurance) {
        return base + loading + insurance;
    }

    private double getGSTAmt(int qty, double gst) {
        return qty * gst;
    }

    @Override
    public int getItemCount() {
        return transactionItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout parentLayout;
        private TextView trItem;
        private TextView trGrp;
        private TextView trItemAmount;
        private TextView txtQty;
        private TextView txtRate;
        private TextView loading;
        private TextView gstRate;
        private ImageView icDeleteCart;
        private ImageView icEditCart;

        ViewHolder(View itemView) {
            super(itemView);

            parentLayout = itemView.findViewById(R.id.parent_layout);
            trItem = itemView.findViewById(R.id.tr_item);
            trGrp = itemView.findViewById(R.id.tr_grp);
            trItemAmount = itemView.findViewById(R.id.tr_item_amount);
            txtQty = itemView.findViewById(R.id.txt_qty);
            txtRate = itemView.findViewById(R.id.txt_rate);
            loading = itemView.findViewById(R.id.loading);
            gstRate = itemView.findViewById(R.id.gst_rate);
            icDeleteCart = itemView.findViewById(R.id.ic_delete_cart);
            icEditCart = itemView.findViewById(R.id.ic_edit_cart);

        }
    }
}
