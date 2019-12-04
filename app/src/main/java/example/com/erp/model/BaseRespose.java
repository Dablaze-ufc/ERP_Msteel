package example.com.erp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BaseRespose {
    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("group_bulletin")
    @Expose
    private List<GroupBulletin> groupBulletin = null;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public List<GroupBulletin> getGroupBulletin() {
        return groupBulletin;
    }

    public void setGroupBulletin(List<GroupBulletin> groupBulletin) {
        this.groupBulletin = groupBulletin;
    }

}
