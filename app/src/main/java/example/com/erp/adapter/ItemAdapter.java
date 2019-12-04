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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import example.com.erp.R;
import example.com.erp.contracts.CommonClicks;
import example.com.erp.model.Gs_item;
import example.com.erp.utility.Constants;
import example.com.erp.utility.SharedPreference;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Gs_item> itemArrayList;
    private ArrayList<Gs_item> orderArrayList;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private String flag;
    private CommonClicks commonClicks;

    public ItemAdapter(Context context, ArrayList<Gs_item> arrayList, ArrayList<Gs_item> orderArrayList, String flag) {
        this.context = context;
        this.itemArrayList = arrayList;
        this.orderArrayList = orderArrayList;
        // this.flag=flag;
    }

    public void reloadData(ArrayList<Gs_item> list) {
        if (itemArrayList != null) {
            itemArrayList.clear();
            itemArrayList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (itemArrayList != null) {
            itemArrayList.clear();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sub_groub, parent, false);
        return new ViewHolder(v);
    }

    public void setCommonClicks(CommonClicks clicks) {
        this.commonClicks = clicks;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Gs_item item = itemArrayList.get(position);
        holder.itemName.setText(item.getGs_item_name());

        /*Set Currency*/
        Spannable wordSpan;
        if (!item.getGs_item_rate().equalsIgnoreCase("")) {
            int start = SharedPreference.getString(Constants.Currency).length();
            int end = (SharedPreference.getString(Constants.Currency).length() + item.getGs_item_rate().length() + 1);
            wordSpan = new SpannableString(String.format("%s %s", SharedPreference.getString(Constants.Currency), item.getGs_item_rate()));
            wordSpan.setSpan(new RelativeSizeSpan(0.7f), 0, start, 0);
            wordSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.black)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.itemDetails.setText(wordSpan);
        }

        holder.relativeLayout.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onItemClick(item);
            }
        });

        holder.add_order.setOnClickListener(V -> {
            if (commonClicks != null) {
                commonClicks.onChartClick(item);
            }
        });

      /*holder.relativeLayout.setOnClickListener(v -> {
         AlertDialog.Builder builder;
         final AlertDialog alertDialog;
         Context mContext = context;
         LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
         final View layout = inflater.inflate(R.layout.details_item, null);
         ImageView imageView = layout.findViewById(R.id.img_delete);

         TextView details = layout.findViewById(R.id.details);
         TextView bRate = layout.findViewById(R.id.tran_date_v);
         TextView loading = layout.findViewById(R.id.loading_v);
         TextView insurance = layout.findViewById(R.id.insurance_v);
         TextView gst = layout.findViewById(R.id.gst_v);
         TextView netRate = layout.findViewById(R.id.net_rate_v);

         try {
            details.setText("Details of " + item.getGs_item_name());
            bRate.setText(item.getBase_rate());
            loading.setText(item.getLoading_charges());
            insurance.setText(item.getInsurance_charges());
            gst.setText(item.getGst_percentage());
            netRate.setText(item.getNet_rate());
         } catch (Exception e) {
            e.printStackTrace();
         }

         builder = new AlertDialog.Builder(mContext);
         builder.setView(layout);
         alertDialog = builder.create();

         alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
         //alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
         alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

         imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               alertDialog.dismiss();
            }
         });
         alertDialog.show();
      });*/
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public ArrayList<Gs_item> getItemArrayList() {
        return itemArrayList;
    }

    public ArrayList<Gs_item> getOrderArrayList() {
        return orderArrayList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemDetails;
        LinearLayout relativeLayout;
        ImageView add_order;
        //        TextView order;
//        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item);
            itemDetails = itemView.findViewById(R.id.item_details);
            relativeLayout = itemView.findViewById(R.id.item_rele);
            add_order = itemView.findViewById(R.id.add_order);

//            imageView = (ImageView) itemView.findViewById(R.id.img_buy);
//            order = (TextView) itemView.findViewById(R.id.item_order);

        }
    }

}
