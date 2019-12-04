package example.com.erp.model;

import com.google.gson.annotations.SerializedName;

public class Transaction {

    public boolean sameDate = false;
    @SerializedName("id")
    String id;
    @SerializedName("transaction_date")
    String transaction_date;
    @SerializedName("voucher_no")
    String voucher_no;
    @SerializedName("transaction_amount")
    String transaction_amount;
    @SerializedName("dr_cr")
    String dr_cr;
    @SerializedName("Balance")
    String Balance;
    @SerializedName("ledger_name")
    String ledger_name;
    @SerializedName("voucher")
    String voucher;
    @SerializedName("transaction_file")
    private String transaction_file;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(String transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getDr_cr() {
        return dr_cr;
    }

    public void setDr_cr(String dr_cr) {
        this.dr_cr = dr_cr;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getLedger_name() {
        return ledger_name;
    }

    public String getVoucher() {
        return voucher;
    }

    public String getTransaction_file() {
        return transaction_file;
    }

    public void setTransaction_file(String transaction_file) {
        this.transaction_file = transaction_file;
    }
}
