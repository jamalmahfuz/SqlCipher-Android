package net.zetetic.tests;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;
import net.zetetic.ZeteticApplication;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class QueryNonEncryptedDatabaseTest extends SQLCipherTest {

    @Override
    public boolean execute(SQLiteDatabase database) {

        database.close();
        File unencryptedDatabase = ZeteticApplication.getInstance().getDatabasePath("unencrypted.db");

        try {
            ZeteticApplication.getInstance().extractAssetToDatabaseDirectory("unencrypted.db");
        } catch (IOException e) {
            Log.e(ZeteticApplication.TAG, "NOT EXPECTED: caught IOException", e);
            return false;
        }

        boolean success = false;

        // XXX CRASHING:
        try {
            char[] nullPassword = null;
            database = SQLiteDatabase.openOrCreateDatabase(unencryptedDatabase.getPath(), nullPassword, null, null);
        } catch (Exception e) {
            Log.e(ZeteticApplication.TAG, "NOT EXPECTED: exception", e);
            return false;
        }

        try {
            database = SQLiteDatabase.openOrCreateDatabase(unencryptedDatabase, "", null);
            Cursor cursor = database.rawQuery("select * from t1", new String[]{});
            cursor.moveToFirst();
            String a = cursor.getString(0);
            String b = cursor.getString(1);
            cursor.close();
            database.close();

            success = a.equals("one for the money") &&
                        b.equals("two for the show");
        } catch (Exception e) {
            Log.e(ZeteticApplication.TAG, "NOT EXPECTED: exception when reading database with blank password", e);
            return false;
        }

        return success;
    }

    @Override
    public String getName() {
        return "Query Non-Encrypted Database Test";
    }
}
