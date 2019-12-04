package example.com.erp.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.erp.R;
import example.com.erp.contracts.CommonClicks;

public class TilesListAdapter extends RecyclerView.Adapter<TilesListAdapter.MyViewHolder> {

    private CommonClicks anInterface;
    private ArrayList<String> arrayList;

    public TilesListAdapter(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_group_name, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.group_name.setText(arrayList.get(position));
        holder.parent_card.setOnClickListener(V -> {
            if (anInterface != null) {
                anInterface.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView group_name;
        CardView parent_card;

        private MyViewHolder(View view) {
            super(view);
            group_name = itemView.findViewById(R.id.wholesale);
            parent_card = itemView.findViewById(R.id.parent_card);
        }
    }
}