package example.com.erp.model;

import com.google.gson.annotations.SerializedName;


public class ImageModel_ads {

    @SerializedName("id")
    String id;

    @SerializedName("ad_name")
    String ad_name;

    @SerializedName("ad_image_small")
    String ad_image_small;

    @SerializedName("ad_image_big")
    String ad_image_big;

    @SerializedName("image_url")
    String image_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getAd_image_small() {
        return ad_image_small;
    }

    public void setAd_image_small(String ad_image_small) {
        this.ad_image_small = ad_image_small;
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
}
