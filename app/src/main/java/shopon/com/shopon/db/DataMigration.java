package shopon.com.shopon.db;

import android.content.Context;
import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import shopon.com.shopon.preferences.UserSharedPreferences;


/**
 * Created by Akshath on 06-11-2016.
 */
public class DataMigration implements RealmMigration {
    private static final String TAG = DataMigration.class.getName();

    Context mContext;

    public DataMigration(Context ctx) {
        mContext = ctx;
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d(TAG, "migrate oldVersion:" + oldVersion + " newVersion:" + oldVersion);
        UserSharedPreferences pref = new UserSharedPreferences(mContext);
        pref.clearAllData();

    }
}
