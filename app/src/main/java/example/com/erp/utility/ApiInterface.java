package example.com.erp.utility;

import example.com.erp.model.BaseRespose;
import example.com.erp.model.DayGraphModel;
import example.com.erp.model.GroupBulletin;
import example.com.erp.model.Message;
import example.com.erp.model.Orders;
import example.com.erp.model.VersionCodeModel;
import example.com.erp.model.userInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    /*
    User
    */
    @GET("api/customer/login_otp_verify.php")
    Call<Message> verifyOtp(@Query("rMobileNo") String mNumber, @Query("otp") String otpNumber, @Query("pin") String pin);

    @GET("api/customer/login_send_otp.php")
    Call<Message> receiveAuthCode(@Query("rMobileNo") String rMobileno);

    @GET("api/customer/ledger_balance.php")
    Call<Message> updateBalance(@Query("user_id") String user_id);

    @GET("api/customer/generate_otp_register.php")
    Call<Message> otp_register(@Query("rFirmname") String Fname, @Query("rGstNumber") String gst, @Query("rMobileNo") String mobileNo, @Query("pin") String pin, @Query("personName") String personName, @Query("address") String address, @Query("location") String location, @Query("pincode") String pincode, @Query("streetNo") String streetNo, @Query("streetName") String streetName, @Query("city") String city);

    @GET("api/customer/user_info_update.php")
    Call<Message> updateProfile(@Query("id") String memberId, @Query("contact") String contactName, @Query("rFirmname") String rFirmname, @Query("personName") String personName, @Query("rGstNumber") String rGstNumber, @Query("address") String address, @Query("location") String location, @Query("pincode") String pincode, @Query("streetNo") String streetNo, @Query("streetName") String streetName, @Query("city") String city);

    @GET("api/customer/confirm_otp_register.php")
    Call<Message> confirm_otp(@Query("rFirmname") String Fname, @Query("rGstNumber") String gst, @Query("rMobileNo") String mobileNo, @Query("otp") String otp, @Query("imei") String imei);

    @GET("api/customer/user_info.php")
    Call<userInfo> getUserInformation(@Query("rMobileNo") String mNumber, @Query("fcm_id") String fcm_id, @Query("user_lat") Double lat, @Query("user_long") Double longi);

    @GET("api/customer/user_pin_update.php")
    Call<Message> updateUserPin(@Query("pin") String pin, @Query("user_id") String user_id);

    /*
    Order
    */
    @GET("api/customer/order_submit.php")
    Call<Message> sendOrder(@Query("user_id") String userId, @Query("item_id") String itemId, @Query("qty") String itemQuantity, @Query("transaction_date") String date);


    @GET("api/customer/order_submit.php")
    Call<Message> sendOrderWithMultipleItems(@Query("user_id") String userId, @Query("item_id") String itemId, @Query("code") String code, @Query("qty") String itemQuantity, @Query("transaction_date") String date);

    @GET("api/customer/order_statement.php")
    Call<Orders.OrderList> getOrders(@Query("user_id") String user_id);

    @GET("api/customer/delivery_challan_statement.php")
    Call<Orders.OrderList> getChallan(@Query("user_id") String user_id, @Query("transaction_id") String trans_id);

    @GET("api/customer/delivery_challan_statement.php")
    Call<Orders.OrderList> getChallanAll(@Query("user_id") String user_id);

    @GET("api/customer/delivery_challan_detail.php")
    Call<Orders.OrderDetail> getChallanDetail(@Query("transaction_id") String trans_id);

    @GET("api/customer/order_detail.php")
    Call<Orders.OrderDetail> getOrderDetails(@Query("transaction_id") String trans_id);


    @GET("api/customer/order_received.php")
    Call<Message> updateOrderStatus(@Query("transaction_id") String trans_id, @Query("user_id") String user_id);

    /*
    Transaction
    */
    @GET("api/customer/transaction_statement.php")
    Call<Message> getTransaction(@Query("user_id") String user_id);

    @GET("api/customer/transaction_detail.php")
    Call<Message> getTransactionDetails(@Query("transaction_id") String trans_id);

    /*
    Items
    */
    @GET("api/customer/group_item_detail.php")
    Call<Message> getGroupDetail(@Query("id") String id);

    @GET("api/customer/search_item.php")
    Call<Message> getSearchedItem(@Query("search_text") String search);

    @GET("api/customer/dashboard_graph.php")
    Call<DayGraphModel> getGraph(@Query("gs_groups_master_id") String gs_groups_master_id, @Query("request_type") String request_type);

    /*
    Images
    */
    @GET("api/customer/app_contact_us_image.php")
    Call<Message> getDrawerBanner();

    @GET("api/customer/app_banner_image.php")
    Call<Message> getTopImages();

    @GET("api/customer/app_ads_images.php")
    Call<Message> getBottomImages();

    /*
    Cart
    */
    @GET("api/customer/item_orderable.php")
    Call<Message> getCartItems();

    /*
    Extra
    */
    @GET("api/customer/app_version.php")
    Call<VersionCodeModel> getVersionCode();

    //   Bulletin
    @GET("api/customer/group_item_bulletin.php")
    Call<BaseRespose> getGroupBulletin();
}