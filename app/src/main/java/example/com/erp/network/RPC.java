package example.com.erp.network;
/*
Create by user on 13-02-2019 at 05:57 PM for ERP
*/

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import example.com.erp.model.Message;
import example.com.erp.model.VersionCodeModel;
import example.com.erp.model.userInfo;
import example.com.erp.utility.ApiClient;
import example.com.erp.utility.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RPC {

    public static void updateLedgerBalance(String data, final CommonListener listener) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiService.updateBalance(data);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    listener.onSuccess(message);
                } else {
                    listener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                t.printStackTrace();
                listener.onFailure("Sorry for inconvenience, Try after some time!!!");
            }
        });
    }

    public static void getBannerImages(CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.getTopImages();
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    commonListener.onSuccess(response.body());
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void getBottomBannerImages(CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.getBottomImages();
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    commonListener.onSuccess(response.body());
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void getDrawerBanner(CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.getDrawerBanner();
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Log.e("JsonData", new Gson().toJson(response.body()));
                    commonListener.onSuccess(response.body());
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void submitOrder(String itemId, String qty, String userId, CommonListener commonListener) {
        SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String date = dateFormatForMonth.format(new Date());
        Call<Message> call = apiInterface.sendOrder(userId, itemId, qty, date);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    commonListener.onSuccess(response.body());
                } else {
                    commonListener.onFailure("Fail to place order");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                commonListener.onFailure("Fail to place order");
            }
        });
    }

    public static void submitOrderWithMultipleItems(String itemId, String qty, String code, String userId, CommonListener commonListener) {
        SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String date = dateFormatForMonth.format(new Date());
        Call<Message> call = apiInterface.sendOrderWithMultipleItems(userId, itemId, code, qty, date);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    commonListener.onSuccess(response.body());
                } else {
                    commonListener.onFailure("Fail to place order");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                commonListener.onFailure("Fail to place order");
            }
        });
    }

    public static void getAuthCode(String mobileNumber, CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.receiveAuthCode(mobileNumber);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("3")) {
                            commonListener.onSuccess(message);
                        } else {
                            commonListener.onFailure(message.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void verifyAuthCode(String mobileNumber, String authCode, String pin, CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.verifyOtp(mobileNumber, authCode, pin);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("4")) {
                            commonListener.onSuccess(message);
                        } else {
                            commonListener.onFailure(message.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void registerUser(String mobileNumber, String firmName, String contactName, String address_one, String address_two, String locality, String landmark, String city, String pincode, String gstNumber, String pinNumber, CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        /*@Query("rFirmname") String Fname, @Query("rGstNumber") String gst, @Query("rMobileNo") String mobileNo, @Query("pin") String pin, @Query("personName") String personName, @Query("address") String address, @Query("location") String location, @Query("pincode") String pincode, @Query("streetNo") String streetNo, @Query("streetName") String streetName, @Query("city") String city*/

        Call<Message> call = apiInterface.otp_register(firmName, gstNumber, mobileNumber, pinNumber, contactName, landmark, locality, pincode, address_one, address_two, city);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("7")) {
                            commonListener.onSuccess(message);
                        } else {
                            commonListener.onFailure(message.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void updateUserProfile(String memebrId, String mobilenumber, String firmName, String contactName, String address_one, String address_two, String location, String address, String pincode, String gstNumber, String city, CommonListener commonListener) {

        /*@Query("id") String memberId, @Query("contact") String contactName, @Query("rFirmname") String rFirmname, @Query("personName") String personName, @Query("rGstNumber") String rGstNumber, @Query("address") String address, @Query("location") String location, @Query("pincode") String pincode, @Query("streetNo") String streetNo, @Query("streetName") String streetName, @Query("city") String city*/

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.updateProfile(memebrId, mobilenumber, firmName, contactName, gstNumber, address, location, pincode, address_one, address_two, city);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("7")) {
                            commonListener.onSuccess(message);
                        } else {
                            commonListener.onFailure(message.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void confirmRegisterUser(String mobileNumber, String firmName, String gstNumber, String authCode, String imeiNumber, CommonListener commonListener) {//todo change here...
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.confirm_otp("", "", mobileNumber, authCode, imeiNumber);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("7")) {
                            commonListener.onSuccess(message);
                        } else {
                            commonListener.onFailure(message.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void updateUserPin(String userId, String pin, CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Message> call = apiInterface.updateUserPin(pin, userId);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    if (message != null) {
                        if (message.getSuccess().equals("2")) {
                            commonListener.onSuccess(message);
                        } else {
                            commonListener.onFailure(message.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void checkVersionCode(CommonListener commonListener) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VersionCodeModel> call = apiInterface.getVersionCode();
        call.enqueue(new Callback<VersionCodeModel>() {
            @Override
            public void onResponse(@NonNull Call<VersionCodeModel> call, @NonNull Response<VersionCodeModel> response) {
                if (response.code() == 404) {
                    commonListener.onFailure("Please try after some time.");
                } else {
                    if (response.isSuccessful()) {
                        //{"success":1,"version":[{"app_version":"2.0.5"}]}
                        VersionCodeModel message = response.body();
                        if (message != null) {
                            if (message.success.equals("1")) {
                                commonListener.onSuccess(message.version.get(0).app_version);
                            } else {
                                commonListener.onFailure(message.message);
                            }
                        }
                    } else {
                        commonListener.onFailure("Please try after some time.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<VersionCodeModel> call, @NonNull Throwable t) {
                t.printStackTrace();
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }

    public static void getUserInformation(@NonNull String phoneNumber, @Nullable String token, @Nullable Double latitude, @Nullable Double longitude, CommonListener commonListener) {
        Call<userInfo> call = ApiClient.getClient().create(ApiInterface.class).getUserInformation(phoneNumber, token, latitude, longitude);
        call.enqueue(new Callback<userInfo>() {
            @Override
            public void onResponse(@NonNull Call<userInfo> call, @NonNull Response<userInfo> response) {
                if (response.isSuccessful()) {
                    userInfo user = response.body();
                    if (user != null) {
                        if (user.getSuccess().equals("4")) {
                            commonListener.onSuccess(user);
                        } else {
                            commonListener.onFailure(user.getMessage());
                        }
                    }
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<userInfo> call, @NonNull Throwable t) {
                t.printStackTrace();
                if (t instanceof IOException) {
                    commonListener.onFailure("Check network connection..");
                } else {
                    commonListener.onFailure("Sorry for inconvenience..");
                }
            }
        });
    }
}
