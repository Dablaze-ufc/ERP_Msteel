package example.com.erp.utility;
/*
Create by user on 15-02-2019 at 06:21 PM for ERP
*/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import example.com.erp.R;
import example.com.erp.database.CartItem;
import example.com.erp.database.DBHelper;
import example.com.erp.model.Group_name;
import example.com.erp.model.Gs_item;
import example.com.erp.network.CommonListener;
import example.com.erp.network.RPC;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CommonFunctions {
    private static AlertDialog alertDialog;

    public static void showOrderDialog(String uid, Context context, String itemName, String currency, String base, Group_name item, CommonListener listener) {
        AlertDialog.Builder builder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout;
        if (inflater != null) {
            layout = inflater.inflate(R.layout.alert_place_item, null, false);
            builder = new AlertDialog.Builder(context);
            builder.setView(layout);

            TextView details = layout.findViewById(R.id.details);
            TextView subDetails = layout.findViewById(R.id.sub_details);
            TextView orderQty = layout.findViewById(R.id.order_qty);
            TextView basePrice = layout.findViewById(R.id.base_price);
            TextView orderAmt = layout.findViewById(R.id.order_amt);
            EditText edtAddQty = layout.findViewById(R.id.edt_add_qty);
//         EditText edtCode = layout.findViewById( R.id.edt_code );
            Button btnCancel = layout.findViewById(R.id.btn_cancel);
            Button btnPlace = layout.findViewById(R.id.btn_place);
//         Button btnAddCart = layout.findViewById( R.id.btn_add_cart );

            ImageView imgDelete = layout.findViewById(R.id.img_delete);
            edtAddQty.setFilters(new InputFilter[]{new MoneyValueFilter()});

            details.setText(itemName);
            basePrice.setText(base);
            subDetails.setText(item.getGs_group_name() != null && !item.getGs_group_name().equalsIgnoreCase("") ? item.getGs_group_name() : "");

            String price = item.getBase_rate().replaceAll("\\s", "").replace("(TP)", "");
            edtAddQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() > 0) {
                        if (edtAddQty.getText().charAt(0) == '.') {
                            edtAddQty.setText("0.0");
                            edtAddQty.setSelection(edtAddQty.getText().toString().length());
                        }
                        orderQty.setText(edtAddQty.getText().toString().trim());
                        orderAmt.setText(String.format(Locale.getDefault(), "%.0f", Double.parseDouble(edtAddQty.getText().toString().trim()) * Double.parseDouble(price)));
                    } else {
                        orderQty.setText("");
                        orderAmt.setText("");
                    }
                }
            });

            imgDelete.setOnClickListener(V -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });

            btnCancel.setOnClickListener(V -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });

//         btnAddCart.setOnClickListener( V -> {
//            DBHelper dbHelper = new DBHelper( context );
//            if (TextUtils.isEmpty( edtAddQty.getText().toString().trim() )) {
//               Toast.makeText( context, "Please add atLeast one quantity.", Toast.LENGTH_SHORT ).show();
//            }
////            else if (TextUtils.isEmpty( edtCode.getText().toString().trim() )) {
////               Toast.makeText( context, "Please add code.", Toast.LENGTH_SHORT ).show();
////            }
//            else {
//               String qty;
//               CartItem cartItem = dbHelper.getBySelected( CartItem.class, "item_id", item.getId() );
//               if (cartItem != null) {
//                  qty = String.valueOf( Integer.parseInt( cartItem.getQty() ) + Integer.parseInt( edtAddQty.getText().toString().trim() ) );
//               } else {
//                  cartItem = new CartItem();
//                  qty = edtAddQty.getText().toString().trim();
//               }
//
//               Log.e( "JsonData", new Gson().toJson( item ) );
//
//               cartItem.setUser_id( uid );
//               cartItem.setItem_id( item.getId() );
//               cartItem.setGs_item_name( item.getGs_item_name() );
//               cartItem.setGs_groups_master_table_id( item.getGs_groups_master_table_id() );
//               cartItem.setBase_rate( item.getBase_rate() );
//               cartItem.setGs_item_rate( item.getGs_item_rate() );
//               cartItem.setGross_rate( item.getGross_rate() );
//               cartItem.setLoading_charges( item.getLoading_charges() );
//               cartItem.setInsurance_charges( item.getInsurance_charges() );
//               cartItem.setNet_rate( item.getNet_rate() );
//               cartItem.setGst_percentage( item.getGst_percentage() );
//               cartItem.setTax_paid_rate( item.getTax_paid_rate() );
//               cartItem.setOrder( item.getOrder() );
//               cartItem.setParent_gs_group_id( item.getParent_gs_group_id() );
////               cartItem.setCode( edtCode.getText().toString().trim() );
//               cartItem.setQty( qty );
//               cartItem.setGrp_name( itemName );
//
//               Dao.CreateOrUpdateStatus status = new DBHelper( context ).createOrUpdate( cartItem );
//               String value;
//               if (status.isUpdated()) {
//                  value = "Updated";
//               } else if (status.isCreated()) {
//                  value = "Added";
//               } else {
//                  value = "";
//               }
//               Toast.makeText( context, "Item " + value + " to cart successfully.", Toast.LENGTH_SHORT ).show();
//               alertDialog.cancel();
//               alertDialog.dismiss();
//               listener.onSuccess( "Success" );
//            }
//         } );

            btnPlace.setOnClickListener(V -> {
                if (TextUtils.isEmpty(edtAddQty.getText().toString().trim())) {
                    Toast.makeText(context, "Please add atLeast one quantity.", Toast.LENGTH_SHORT).show();
                }
//            else if (TextUtils.isEmpty( edtCode.getText().toString().trim() )) {
//               Toast.makeText( context, "Please add code.", Toast.LENGTH_SHORT ).show();
//            }
                else {
                    alertDialog.cancel();
                    alertDialog.dismiss();
//               RPC.submitOrder( item.getId(), edtAddQty.getText().toString().trim(), uid, listener );
                    RPC.submitOrder(item.item_id, edtAddQty.getText().toString().trim(), uid, listener);
                    // listener.onSuccess( "Success" );
                }
            });

            alertDialog = builder.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.show();

            ViewGroup parent = (ViewGroup) layout.getParent();
            parent.setPadding(0, 0, 0, 0);
        }
    }

    public static void editOrderDialog(int orderId, Context context, String itemName, String currency, String base, Gs_item item, CommonListener listener) {
        AlertDialog.Builder builder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout;
        if (inflater != null) {
            layout = inflater.inflate(R.layout.alert_place_item, null, false);
            builder = new AlertDialog.Builder(context);
            builder.setView(layout);

            TextView details = layout.findViewById(R.id.details);
            TextView subDetails = layout.findViewById(R.id.sub_details);
            TextView orderQty = layout.findViewById(R.id.order_qty);
            TextView basePrice = layout.findViewById(R.id.base_price);
            TextView orderAmt = layout.findViewById(R.id.order_amt);
            EditText edtAddQty = layout.findViewById(R.id.edt_add_qty);
//         EditText edtCode = layout.findViewById( R.id.edt_code );
            Button btnCancel = layout.findViewById(R.id.btn_cancel);
            Button btnPlace = layout.findViewById(R.id.btn_place);
//         Button btnAddCart = layout.findViewById( R.id.btn_add_cart );

            layout.findViewById(R.id.btn_place).setVisibility(View.GONE);
            layout.findViewById(R.id.view_two).setVisibility(View.GONE);

//         btnAddCart.setText( "Update Quantity" );

            ImageView imgDelete = layout.findViewById(R.id.img_delete);
            edtAddQty.setFilters(new InputFilter[]{new MoneyValueFilter()});

            details.setText(itemName);
            basePrice.setText(base);
            subDetails.setText(item.getGs_item_name() != null && !item.getGs_item_name().equalsIgnoreCase("") ? item.getGs_item_name() : "");

            String price = item.getNet_rate().replaceAll("\\s", "").replace("(TP)", "");
            edtAddQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() > 0) {
                        if (edtAddQty.getText().charAt(0) == '.') {
                            edtAddQty.setText("0.0");
                            edtAddQty.setSelection(edtAddQty.getText().toString().length());
                        }
                        orderQty.setText(edtAddQty.getText().toString().trim());
                        orderAmt.setText(String.format(Locale.getDefault(), "%.0f", Double.parseDouble(edtAddQty.getText().toString().trim()) * Double.parseDouble(price)));
                    } else {
                        orderQty.setText("");
                        orderAmt.setText("");
                    }
                }
            });

            imgDelete.setOnClickListener(V -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });

            btnCancel.setOnClickListener(V -> {
                alertDialog.cancel();
                alertDialog.dismiss();
            });

//         btnAddCart.setOnClickListener( V -> {
//            DBHelper dbHelper = new DBHelper( context );
//            if (TextUtils.isEmpty( edtAddQty.getText().toString().trim() )) {
//               Toast.makeText( context, "Please add atLeast one quantity.", Toast.LENGTH_SHORT ).show();
//            }
////            else if (TextUtils.isEmpty( edtCode.getText().toString().trim() )) {
////               Toast.makeText( context, "Please add code.", Toast.LENGTH_SHORT ).show();
////            }
//            else {
//               String qty;
//               CartItem cartItem = dbHelper.getById( CartItem.class, orderId );
//               if (cartItem != null) {
//                  qty = String.valueOf( Integer.parseInt( cartItem.getQty() ) + Integer.parseInt( edtAddQty.getText().toString().trim() ) );
//                  cartItem.setQty( qty );
//                  Dao.CreateOrUpdateStatus status = new DBHelper( context ).createOrUpdate( cartItem );
//                  String value;
//                  if (status.isUpdated()) {
//                     value = "Updated";
//                  } else if (status.isCreated()) {
//                     value = "Added";
//                  } else {
//                     value = "";
//                  }
//                  Toast.makeText( context, "Item " + value + " to cart successfully.", Toast.LENGTH_SHORT ).show();
//               }
//               alertDialog.cancel();
//               alertDialog.dismiss();
//               listener.onSuccess( "Success" );
//            }
//         } );

            alertDialog = builder.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.show();

            ViewGroup parent = (ViewGroup) layout.getParent();
            parent.setPadding(0, 0, 0, 0);
        }
    }

    public static void showSuccessFailDialog(Context context) {
        AlertDialog.Builder builder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout;
        if (inflater != null) {
            layout = inflater.inflate(R.layout.alert_success_fail, null, false);
            ((ImageView) layout.findViewById(R.id.img_header)).setImageDrawable(VectorDrawableCompat.create(context.getResources(), R.drawable.ic_tick_right, null));

//         layout.findViewById( R.id.btn_done ).setOnClickListener( V -> {
//            alertDialog.cancel();
//            alertDialog.dismiss();
//         } );


            builder = new AlertDialog.Builder(context);
            builder.setView(layout);
            alertDialog = builder.create();
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.show();

            ViewGroup parent = (ViewGroup) layout.getParent();
            parent.setPadding(0, 0, 0, 0);

            Button btn_done = layout.findViewById(R.id.btn_done);
            btn_done.setVisibility(View.GONE);
//         btn_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               alertDialog.dismiss();
//               alertDialog.cancel();
//
//            }
//         });
        }
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static int getCartCount(Context context) {
        return new DBHelper(context).getCartCount(SharedPreference.getString(Constants.UserId));
    }

    public static String joinArrayList(ArrayList<?> list, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (Object o : list) {
            builder.append(o).append(delimiter);
        }
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static class MoneyValueFilter extends DigitsKeyListener {
        private int afterDecimal = 4, beforeDecimal = 20;
        private Pattern mPattern;

        MoneyValueFilter() {
            super(false, true);
            mPattern = Pattern.compile("-?[0-9]{0," + (beforeDecimal) + "}+((\\.[0-9]{0," + (afterDecimal) + "})?)|(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String replacement = source.subSequence(start, end).toString();
            String newVal = dest.subSequence(0, dstart).toString() + replacement + dest.subSequence(dend, dest.length()).toString();
            Matcher matcher = mPattern.matcher(newVal);
            if (matcher.matches())
                return null;

            if (TextUtils.isEmpty(source))
                return dest.subSequence(dstart, dend);
            else
                return "";
        }
    }

}
