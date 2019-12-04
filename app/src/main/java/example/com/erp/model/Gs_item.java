package example.com.erp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Gs_item implements Serializable {

    @SerializedName("id")
    String id;

    @SerializedName("gs_item_name")
    String gs_item_name;

    @SerializedName("gs_groups_master_table_id")
    String gs_groups_master_table_id;

    @SerializedName("base_rate")
    String base_rate;

    @SerializedName("gs_item_rate")
    String gs_item_rate;

    @SerializedName("gross_rate")
    String gross_rate;

    @SerializedName("loading_charges")
    String loading_charges;

    @SerializedName("insurance_charges")
    String insurance_charges;

    @SerializedName("net_rate")
    String net_rate;

    @SerializedName("gst_percentage")
    String gst_percentage;

    @SerializedName("tax_paid_rate")
    String tax_paid_rate;

    @SerializedName("order")
    String order;

    @SerializedName("parent_gs_group_id")
    String parent_gs_group_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGs_item_name() {
        return gs_item_name;
    }

    public void setGs_item_name(String gs_item_name) {
        this.gs_item_name = gs_item_name;
    }

    public String getGs_groups_master_table_id() {
        return gs_groups_master_table_id;
    }

    public void setGs_groups_master_table_id(String gs_groups_master_table_id) {
        this.gs_groups_master_table_id = gs_groups_master_table_id;
    }

    public String getBase_rate() {
        return base_rate;
    }

    public void setBase_rate(String base_rate) {
        this.base_rate = base_rate;
    }

    public String getGs_item_rate() {
        return gs_item_rate;
    }

    public void setGs_item_rate(String gs_item_rate) {
        this.gs_item_rate = gs_item_rate;
    }

    public String getGross_rate() {
        return gross_rate;
    }

    public void setGross_rate(String gross_rate) {
        this.gross_rate = gross_rate;
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

    public String getNet_rate() {
        return net_rate;
    }

    public void setNet_rate(String net_rate) {
        this.net_rate = net_rate;
    }

    public String getGst_percentage() {
        return gst_percentage;
    }

    public void setGst_percentage(String gst_percentage) {
        this.gst_percentage = gst_percentage;
    }

    public String getTax_paid_rate() {
        return tax_paid_rate;
    }

    public void setTax_paid_rate(String tax_paid_rate) {
        this.tax_paid_rate = tax_paid_rate;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getParent_gs_group_id() {
        return parent_gs_group_id;
    }

    public void setParent_gs_group_id(String parent_gs_group_id) {
        this.parent_gs_group_id = parent_gs_group_id;
    }
}
