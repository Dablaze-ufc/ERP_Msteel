package example.com.erp.model;
/*
Create by user on 25-02-2019 at 07:08 PM for ERP
*/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Orders {

    public static class OrderList {
        @Expose
        @SerializedName("trn_statement")
        public ArrayList<Trn_statement> trn_statement;
        @Expose
        @SerializedName("success")
        public String success;
        @Expose
        @SerializedName("message")
        public String message;

        public static class Trn_statement {
            @Expose
            @SerializedName("Balance")
            public String Balance;
            @Expose
            @SerializedName("transaction_amount")
            public String transaction_amount;
            @Expose
            @SerializedName("dr_cr")
            public String dr_cr;
            @Expose
            @SerializedName("status")
            public String status;
            @Expose
            @SerializedName("status_type")
            public String status_type;
            @Expose
            @SerializedName("voucher_no")
            public String voucher_no;
            @Expose
            @SerializedName("voucher")
            public String voucher;
            @Expose
            @SerializedName("ledger_name")
            public String ledger_name;
            @Expose
            @SerializedName("transaction_date")
            public String transaction_date;
            @Expose
            @SerializedName("id")
            public String id;
            @Expose
            @SerializedName("transaction_file")
            public String transaction_file;

            public boolean sameDate = true;
        }
    }

    public static class OrderDetail {
        @Expose
        @SerializedName("trn_details")
        public ArrayList<Trn_details> trn_details;
        @Expose
        @SerializedName("success")
        public String success;
        @Expose
        @SerializedName("message")
        public String message;

        public static class Trn_details {
            @Expose
            @SerializedName("net_amount")
            public String net_amount;
            @Expose
            @SerializedName("gst_amount")
            public String gst_amount;
            @Expose
            @SerializedName("transaction_item_taxable_amount")
            public String transaction_item_taxable_amount;
            @Expose
            @SerializedName("transaction_rate")
            public String transaction_rate;
            @Expose
            @SerializedName("transaction_qty")
            public String transaction_qty;
            @Expose
            @SerializedName("gs_item_name")
            public String gs_item_name;
            @Expose
            @SerializedName("voucher_type")
            public String voucher_type;
            @Expose
            @SerializedName("voucher_no")
            public String voucher_no;
            @Expose
            @SerializedName("transaction_date")
            public String transaction_date;
            @Expose
            @SerializedName("transaction_id")
            public String transaction_id;

            @Expose
            @SerializedName("balance_qty")
            public String balance_qty;

            @Expose
            @SerializedName("ref_no")
            public String ref_no;

        }
    }
}
