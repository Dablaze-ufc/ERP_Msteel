package example.com.erp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Message {

    //{"success":4,"message":"Logged-in successfully","user_full_name":"CRYSTAL MADHAV","user_contact":"8000999001","user_id":"435"}
    @SerializedName("user_full_name")
    private String user_full_name;

    @SerializedName("user_contact")
    private String user_contact;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("trn_statement")
    private ArrayList<Transaction> trn_statement;

    @SerializedName("trn_details")
    private ArrayList<TransactionItem> trn_details;

    @SerializedName("items")
    private ArrayList<ItemOrder> items;

    @SerializedName("success")
    private String success;

    @SerializedName("message")
    private String message;

    @SerializedName("otp")
    private String otp;

    @SerializedName("group_name")
    private ArrayList<Group> group_name;

    @SerializedName("gs_group_name")
    private ArrayList<Group_name> gs_group_name;

    @SerializedName("gs_item")
    private ArrayList<Gs_item> gs_item;

    @SerializedName("banner")
    private ArrayList<ImageModel> banner;

    @SerializedName("ads")
    private ArrayList<ImageModel_ads> ads;

    @SerializedName("result")
    private Result result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public ArrayList<Group> getGroup_name() {
        return group_name;
    }

    public void setGroup_name(ArrayList<Group> group_name) {
        this.group_name = group_name;
    }

    public ArrayList<Group_name> getGs_group_name() {
        return gs_group_name;
    }

    public void setGs_group_name(ArrayList<Group_name> gs_group_name) {
        this.gs_group_name = gs_group_name;
    }

    public ArrayList<Gs_item> getGs_item() {
        return gs_item;
    }

    public void setGs_item(ArrayList<Gs_item> gs_item) {
        this.gs_item = gs_item;
    }

    public ArrayList<ImageModel> getBanner() {
        return banner;
    }

    public void setBanner(ArrayList<ImageModel> banner) {
        this.banner = banner;
    }

    public ArrayList<ImageModel_ads> getAds() {
        return ads;
    }

    public void setAds(ArrayList<ImageModel_ads> ads) {
        this.ads = ads;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public ArrayList<Transaction> getTrn_statement() {
        return trn_statement;
    }

    public void setTrn_statement(ArrayList<Transaction> trn_statement) {
        this.trn_statement = trn_statement;
    }

    public ArrayList<TransactionItem> getTrn_details() {
        return trn_details;
    }

    public void setTrn_details(ArrayList<TransactionItem> trn_details) {
        this.trn_details = trn_details;
    }

    public ArrayList<ItemOrder> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemOrder> items) {
        this.items = items;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public String getUser_contact() {
        return user_contact;
    }

    public String getUser_id() {
        return user_id;
    }


}
