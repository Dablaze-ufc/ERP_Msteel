package example.com.erp.model;

import com.google.gson.annotations.SerializedName;


public class Group_name {

    @SerializedName("id")
    public String id;

    @SerializedName("gs_group_name")
    public String gs_group_name;

    @SerializedName("base_rate")
    public String base_rate;

    @SerializedName("loading_charges")
    public String loading_charges;

    @SerializedName("insurance_charges")
    public String insurance_charges;

    @SerializedName("parent_gs_group_id")
    public String parent_gs_group_id;

    @SerializedName("upper_parent_gs_group_id")
    public String upper_parent_gs_group_id;

    @SerializedName("item_id")
    public String item_id;

    @SerializedName("is_orderable")
    public String is_orderable;

    @SerializedName("booking_closed_msg")
    public String booking_closed_msg;


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

    public String getBase_rate() {
        return base_rate;
    }

    public void setBase_rate(String base_rate) {
        this.base_rate = base_rate;
    }

    public String getLoading_charges() {
        return loading_charges;
    }

    public void setLoading_charges(String loading_charges) {
        this.loading_charges = loading_charges;
    }

    public String getInsurance_charges() {
        return insurance_charges;
    }

    public void setInsurance_charges(String insurance_charges) {
        this.insurance_charges = insurance_charges;
    }

    public String getParent_gs_group_id() {
        return parent_gs_group_id;
    }

    public void setParent_gs_group_id(String parent_gs_group_id) {
        this.parent_gs_group_id = parent_gs_group_id;
    }

    public String getUpper_parent_gs_group_id() {
        return upper_parent_gs_group_id;
    }

    public void setUpper_parent_gs_group_id(String upper_parent_gs_group_id) {
        this.upper_parent_gs_group_id = upper_parent_gs_group_id;
    }
}
