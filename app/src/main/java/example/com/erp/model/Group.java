package example.com.erp.model;

import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("id")
    String id;

    @SerializedName("gs_group_name")
    String gs_group_name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGs_group_name() {
        return gs_group_name;
    }

    public void setGs_group_name(String gs_group_name) {
        this.gs_group_name = gs_group_name;
    }


}
