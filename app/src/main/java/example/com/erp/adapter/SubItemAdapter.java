package example.com.erp.adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.activity.SubItemActivity;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.model.Group_name;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.MyViewHolder> {

    private CommonClicks anInterface;
    private ArrayList<Group_name> arrayList;
    private SubItemActivity activity;
    private CommonClicks commonClicks;
    private Boolean isVisibleOrderIcon = false;

    public SubItemAdapter(SubItemActivity contentChildActivity, ArrayList<Group_name> arrayList) {
        this.arrayList = arrayList;
        this.activity = contentChildActivity;
    }

    public void setAnInterface(CommonClicks commonInterface) {
        anInterface = commonInterface;
    }

    public void clearData() {
        if (arrayList != null) {
            arrayList.clear();
        }
        notifyDataSetChanged();
    }

    public void reloadData(ArrayList<Group_name> list, Boolean isVisibleOrderIcon) {
        if (arrayList != null) {
            this.isVisibleOrderIcon = isVisibleOrderIcon;
            arrayList.clear();
            arrayList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group, parent, false));
    }

    public void setCommonClicks(CommonClicks clicks) {
        this.commonClicks = clicks;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Group_name categories = arrayList.get(position);
        holder.text_cat.setText(categories.getGs_group_name());

        Spannable wordSpan;
        if (!categories.getBase_rate().equalsIgnoreCase("")) {
            int start = SharedPreference.getString(Constants.Currency).length();
            int end = (SharedPreference.getString(Constants.Currency).length() + categories.getBase_rate().length() + 1);
            wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), categories.getBase_rate()));
            wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
            wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordSpan.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.black)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.text_rate.setText(wordSpan);
        }

        if (isVisibleOrderIcon) {
            holder.add_order.setVisibility(View.VISIBLE);
        } else {
            holder.add_order.setVisibility(View.GONE);
        }

        holder.parent_layout.setOnClickListener(V -> {
            if (anInterface != null) {
                anInterface.onItemClick(position);
            }
        });

        holder.chart.setOnClickListener(V -> {
            if (anInterface != null) {
                anInterface.onChartClick(position);
            }
        });

        if (categories.is_orderable.equals("0")) {
            holder.add_order.setImageResource(R.drawable.error);
            holder.add_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(activity, categories.booking_closed_msg, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.add_order.setImageResource(R.drawable.ic_add_cart);
            holder.add_order.setOnClickListener(V -> {
                if (commonClicks != null) {
                    commonClicks.onChartClick(categories);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_cat, text_rate;
        ImageView btn_next;
        ImageView chart;
        LinearLayout parent_layout;
        ImageView add_order;

        private MyViewHolder(View view) {
            super(view);
            text_cat = itemView.findViewById(R.id.text_cat_t);
            text_rate = itemView.findViewById(R.id.text_tate_t);
            btn_next = itemView.findViewById(R.id.btn_next_t);
            chart = itemView.findViewById(R.id.chart);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            add_order = itemView.findViewById(R.id.add_order);
        }
    }
}