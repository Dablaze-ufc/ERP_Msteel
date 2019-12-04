package example.com.erp.model;

import com.google.gson.annotations.SerializedName;


public class TransactionItem {

    @SerializedName("transaction_id")
    String transaction_id;

    @SerializedName("transaction_date")
    String transaction_date;

    @SerializedName("voucher_no")
    String voucher_no;

    @SerializedName("voucher_type")
    String voucher_type;

    @SerializedName("gs_item_name")
    String gs_item_name;

    @SerializedName("transaction_qty")
    String transaction_qty;

    @SerializedName("transaction_item_taxable_amount")
    String transaction_item_taxable_amount;

    @SerializedName("gst_amount")
    String gst_amount;

    @SerializedName("net_amount")
    String net_amount;

    @SerializedName("transaction_rate")
    String transaction_rate;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getVoucher_no() {
        return voucher_no;
    }

    public void setVoucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
    }

    public String getVoucher_type() {
        return voucher_type;
    }

    public void setVoucher_type(String voucher_type) {
        this.voucher_type = voucher_type;
    }

    public String getGs_item_name() {
        return gs_item_name;
    }

    public void setGs_item_name(String gs_item_name) {
        this.gs_item_name = gs_item_name;
    }

    public String getTransaction_qty() {
        return transaction_qty;
    }

    public void setTransaction_qty(String transaction_qty) {
        this.transaction_qty = transaction_qty;
    }

    public String getTransaction_item_taxable_amount() {
        return transaction_item_taxable_amount;
    }

    public void setTransaction_item_taxable_amount(String transaction_item_taxable_amount) {
        this.transaction_item_taxable_amount = transaction_item_taxable_amount;
    }

    public String getGst_amount() {
        return gst_amount;
    }

    public void setGst_amount(String gst_amount) {
        this.gst_amount = gst_amount;
    }

    public String getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(String net_amount) {
        this.net_amount = net_amount;
    }

    public String getTransaction_rate() {
        return transaction_rate;
    }

    public void setTransaction_rate(String transaction_rate) {
        this.transaction_rate = transaction_rate;
    }
}
