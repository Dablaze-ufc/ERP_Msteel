package example.com.erp.model;

import com.google.gson.annotations.SerializedName;


public class ItemOrder {

    @SerializedName("id")
    String id;
    @SerializedName("gs_item_name")
    String gs_item_name;

    @SerializedName("gs_item_rate")
    String gs_item_rate;

    @SerializedName("order")
    String order;

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

    public String getGs_item_rate() {
        return gs_item_rate;
    }

    public void setGs_item_rate(String gs_item_rate) {
        this.gs_item_rate = gs_item_rate;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
