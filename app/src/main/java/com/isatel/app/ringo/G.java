package com.isatel.app.ringo;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Mohammad on 10/14/2017.
 */

public class G extends Application {
    public static boolean isLimited = false;
    public static Context context;
    private SQLiteDatabase myDB;


    @Override
    public void onCreate() {
        super.onCreate();
        dataBaseCreator();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        context= getApplicationContext();
    }
    public void dataBaseCreator() {
        try {
            myDB = context.openOrCreateDatabase("downloadedFiles", MODE_PRIVATE, null);

   /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "downloadedFiles"
                    + " (id VARCHAR, name VARCHAR);");

   /* Insert data to a Table*/
//            myDB.execSQL("INSERT INTO "
//                    + "downloadedFiles"
//                    + " (Field1, Field2)"
//                    + " VALUES ('Saranga', 22);");

//   /*retrieve data from database */
//            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName, null);
//
//            int Column1 = c.getColumnIndex("Field1");
//            int Column2 = c.getColumnIndex("Field2");
//
//            // Check if our result was valid.
//            c.moveToFirst();
//            if (c != null) {
//                // Loop through all Results
//                do {
//                    String Name = c.getString(Column1);
//                    int Age = c.getInt(Column2);
//                    Data = Data + Name + "/" + Age + "\n";
//                } while (c.moveToNext());
//            }
        }catch (Exception e){}
    }
}
