package example.com.erp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("gs_group_master_table_id")
    @Expose
    private String gsGroupMasterTableId;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGsGroupMasterTableId() {
        return gsGroupMasterTableId;
    }

    public void setGsGroupMasterTableId(String gsGroupMasterTableId) {
        this.gsGroupMasterTableId = gsGroupMasterTableId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
