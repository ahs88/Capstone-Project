package shopon.com.shopon;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import shopon.com.shopon.db.DataMigration;

/**
 * Created by Akshath on 20-07-2016.
 */
public class ShopOn extends Application{
    private static final String TAG = ShopOn.class.getCanonicalName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"Application oncreate");
        /*Realm.init(getApplicationContext());
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().inMemory().
                migration(new DataMigration(getApplicationContext())).schemaVersion(1).deleteRealmIfMigrationNeeded().
                build(); //.
        Realm.setDefaultConfiguration(realmConfig);*/
    }
}
