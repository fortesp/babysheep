package fortesp.babysheep.preferences;

import android.content.SharedPreferences;

public abstract class AbstractApplicationPreferences {

    SharedPreferences sharedPreferences;

    public void clearAllPrefs() {
        sharedPreferences.edit().clear().commit();
    }

    public void setPref(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public void setPref(String key, Integer value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public void setPref(String key, Boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, -1);
    }
}