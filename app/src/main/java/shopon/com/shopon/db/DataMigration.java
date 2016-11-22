package shopon.com.shopon.db;

import android.content.Context;
import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import shopon.com.shopon.viewmodel.login.preferences.UserSharedPreferences;

/**
 * Created by Akshath on 06-11-2016.
 */
public class DataMigration implements RealmMigration {
    private static final String TAG = DataMigration.class.getName();

    Context mContext;

    public DataMigration(Context ctx){
        mContext = ctx;
    }

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d(TAG,"migrate oldVersion:"+oldVersion+" newVersion:"+oldVersion);
        UserSharedPreferences pref = new UserSharedPreferences(mContext);
        pref.clearAllData();
       /* if (oldVersion == 0) {
            schema.create("CustomersRealm")
                    .addField("email", String.class)
                    .addField("name", String.class)
                    .addField("intrestedIn", String.class)
                    .addField("merchentId", String.class)
                    .addField("mobile", String.class)
                    .addField("id", int.class);
            oldVersion++;
        }

        // Migrate to version 2: Add a primary key + object references
        // Example:
        // public Person extends RealmObject {
        //     private String name;
        //     @PrimaryKey
        //     private int age;
        //     private Dog favoriteDog;
        //     private RealmList<Dog> dogs;
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 1) {
            schema.get("CustomerRealm")
                    .addPrimaryKey("id");
            oldVersion++;
        }*/
    }
}
