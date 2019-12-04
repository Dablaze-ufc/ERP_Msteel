package example.com.erp.model;

import com.google.gson.annotations.SerializedName;


public class Result {

    @SerializedName("sumDr")
    String sumDr;

    @SerializedName("sumCr")
    String sumCr;

    @SerializedName("Balance")
    String Balance;

    public String getSumDr() {
        return sumDr;
    }

    public void setSumDr(String sumDr) {
        this.sumDr = sumDr;
    }

    public String getSumCr() {
        return sumCr;
    }

    public void setSumCr(String sumCr) {
        this.sumCr = sumCr;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }
}
