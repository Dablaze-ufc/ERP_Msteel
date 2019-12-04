package example.com.erp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class userInfo {

    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("user_city")
    private String user_city;
    @Expose
    @SerializedName("user_pin")
    private String user_pin;
    @Expose
    @SerializedName("user_address")
    private String user_address;
    @Expose
    @SerializedName("user_locality")
    private String user_locality;
    @Expose
    @SerializedName("city")
    private String city;
    @Expose
    @SerializedName("user_street_name")
    private String user_street_name;
    @Expose
    @SerializedName("user_street")
    private String user_street;
    @Expose
    @SerializedName("pin")
    private String pin;
    @Expose
    @SerializedName("currency")
    private String currency;
    @Expose
    @SerializedName("gst")
    private String gst;
    @Expose
    @SerializedName("user_contact")
    private String user_contact;
    @Expose
    @SerializedName("user_full_name")
    private String user_full_name;
    @Expose
    @SerializedName("user_person_name")
    private String user_person_name;
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("success")
    private String success;

    //{"success":4,"id":"435","user_full_name":"CRYSTAL MADHAV","user_contact":"8000999001","gst":null,"currency":"INR","pin":"1","user_person_name":"CRYSTAL MADAHV","user_street":"STREET NO. 1","user_street_name":"STREET NAME","user_locality":"LOCATION HERE","user_address":"ADDRESS LINE ONE ,TWO","user_pin":"360000","user_city":""}


    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_pin() {
        return user_pin;
    }

    public void setUser_pin(String user_pin) {
        this.user_pin = user_pin;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUser_locality() {
        return user_locality;
    }

    public void setUser_locality(String user_locality) {
        this.user_locality = user_locality;
    }

    public String getUser_street_name() {
        return user_street_name;
    }

    public void setUser_street_name(String user_street_name) {
        this.user_street_name = user_street_name;
    }

    public String getUser_street() {
        return user_street;
    }

    public void setUser_street(String user_street) {
        this.user_street = user_street;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getUser_contact() {
        return user_contact;
    }

    public void setUser_contact(String user_contact) {
        this.user_contact = user_contact;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getUser_person_name() {
        return user_person_name;
    }

    public void setUser_person_name(String user_person_name) {
        this.user_person_name = user_person_name;
    }
}
