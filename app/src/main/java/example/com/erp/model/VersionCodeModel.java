package example.com.erp.model;
/*
Create by user on 22-02-2019 at 07:06 PM for ERP
*/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VersionCodeModel {
    //{"success":1,"version":[{"app_version":"2.0.5"}]}
    @Expose
    @SerializedName("version")
    public ArrayList<Version> version;
    @Expose
    @SerializedName("success")
    public String success;
    @SerializedName("message")
    public String message;

    public static class Version {
        @Expose
        @SerializedName("app_version")
        public String app_version;
    }
}
