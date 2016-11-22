package shopon.com.shopon.viewmodel.login.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Akshath on 20-07-2016.
 */
public class UserSharedPreferences {

    private final Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private static final String USER_PREFERENCE = "USER_PREFERENCE";

    public UserSharedPreferences(Context activity) {
        mContext = activity;
        sharedPreferences = mContext.getSharedPreferences (USER_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void clearAllData() {
        editor.clear().commit();
    }

    /* Generic Code */
    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            editor.remove(key).commit();
        }
    }



    public void savePref(String key, Object value) {
        delete(key);

        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException ("Attempting to save non-primitive preference");
        }

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPref(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean isPrefExists(String key) {
        return sharedPreferences.contains(key);
    }

}
