package example.com.erp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.model.Gs_item;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class OrderableItemAdapter extends RecyclerView.Adapter<OrderableItemAdapter.ViewHolder> {

    private ArrayList<Gs_item> itemOrders;
    private CommonClicks.CartItemClicks commonClicks;
    private Context activity;

    public OrderableItemAdapter(ArrayList<Gs_item> arrayList, Context cartItemActivity) {
        this.itemOrders = arrayList;
        this.activity = cartItemActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart_item, parent, false);
        return new ViewHolder(v);
    }

    public void setCommonClicks(CommonClicks.CartItemClicks clicks) {
        this.commonClicks = clicks;
    }

    public void reloadData(ArrayList<Gs_item> list) {
        if (itemOrders != null) {
            itemOrders.clear();
            itemOrders.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (itemOrders != null) {
            itemOrders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Gs_item itemOrder = itemOrders.get(position);

        holder.itemOrder.setText(itemOrder.getGs_item_name());
        holder.itemDetailsOrder.setText(itemOrder.getGs_item_rate());

        Spannable wordSpan;
        if (!itemOrder.getGs_item_rate().equalsIgnoreCase("")) {
            int start = SharedPreference.getString(Constants.Currency).length();
            int end = (SharedPreference.getString(Constants.Currency).length() + itemOrder.getGs_item_rate().length() + 1);
            wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), itemOrder.getGs_item_rate()));
            wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
            wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordSpan.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.black)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.itemDetailsOrder.setText(wordSpan);
        }

        try {
            if (itemOrder.getOrder() != null && !itemOrder.getOrder().equals("")) {
                holder.itemOrderOrder.setText(itemOrder.getOrder());
                //holder.imgBuyOrder.setVisibility(View.GONE);
                holder.llOrderQty.setVisibility(View.VISIBLE);
            } else {
                //holder.imgBuyOrder.setVisibility(View.VISIBLE);
                holder.llOrderQty.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.imgBuyOrder.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onItemClick(itemOrder);
            }
        });

        holder.itemOrderOrder.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onItemClick(itemOrder);
            }
        });

        holder.itemRele.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onViewProductDetails(itemOrder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemRele, llOrderQty;
        private TextView itemOrder;
        private TextView itemDetailsOrder;
        private TextView txtQty;
        private TextView itemOrderOrder;
        private ImageView imgBuyOrder;

        ViewHolder(View itemView) {
            super(itemView);
            itemRele = itemView.findViewById(R.id.item_rele);
            llOrderQty = itemView.findViewById(R.id.ll_order_qty);
            itemOrder = itemView.findViewById(R.id.item_order);
            itemDetailsOrder = itemView.findViewById(R.id.item_details_order);
            txtQty = itemView.findViewById(R.id.txt_qty);
            itemOrderOrder = itemView.findViewById(R.id.item_order_order);
            imgBuyOrder = itemView.findViewById(R.id.img_buy_order);
        }
    }
}
