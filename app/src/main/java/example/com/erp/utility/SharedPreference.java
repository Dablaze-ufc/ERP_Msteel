package example.com.erp.utility;
/*
Create by user on 11-04-2019 at 05:02 PM for ERP
*/

public class SharedPreference {

    public static void setString(String key, String value) {
        AppController.getInstance().getSharedPreferences("MyPref", 0).edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return AppController.getInstance().getSharedPreferences("MyPref", 0).getString(key, "");
    }

    public static void clearAllPref() {
        AppController.getInstance().getSharedPreferences("MyPref", 0).edit().clear().apply();
    }

    public static void clearPref(String key) {
        AppController.getInstance().getSharedPreferences("MyPref", 0).edit().remove(key).apply();
    }

}
