package example.com.erp.model;

import com.google.gson.annotations.SerializedName;


public class ImageModel {

    @SerializedName("id")
    String id;

    @SerializedName("branch_master_table_id")
    String branch_master_table_id;

    @SerializedName("Image_name")
    String Image_name;

    @SerializedName("image_small")
    String image_small;

    @SerializedName("image_big")
    String image_big;

    @SerializedName("ad_image_big")
    String ad_image_big;

    @SerializedName("image_url")
    String image_url;

    @SerializedName("status")
    String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch_master_table_id() {
        return branch_master_table_id;
    }

    public void setBranch_master_table_id(String branch_master_table_id) {
        this.branch_master_table_id = branch_master_table_id;
    }

    public String getImage_name() {
        return Image_name;
    }

    public void setImage_name(String image_name) {
        Image_name = image_name;
    }

    public String getImage_small() {
        return image_small;
    }

    public void setImage_small(String image_small) {
        this.image_small = image_small;
    }

    public String getImage_big() {
        return image_big;
    }

    public void setImage_big(String image_big) {
        this.image_big = image_big;
    }

    public String getAd_image_big() {
        return ad_image_big;
    }

    public void setAd_image_big(String ad_image_big) {
        this.ad_image_big = ad_image_big;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
