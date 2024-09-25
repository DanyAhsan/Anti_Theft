package com.atexcode.antitheft.lib;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AtexLocalDB {

    private static final String DATABASE_NAME = "atexdb1.db";
    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private final Context context;

    public AtexLocalDB(Context context) {
        this.context = context;
    }

    public AtexLocalDB open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Logs Table Methods
    public long insertLog(String event, String details) {


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yy", Locale.US);
        String currentDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        String currentTime = timeFormat.format(new Date());

        ContentValues contentValues = new ContentValues();
        contentValues.put("event", event);
        contentValues.put("details", details);
        contentValues.put("date", currentDate);
        contentValues.put("time", currentTime);
        return database.insert("Logs", null, contentValues);
    }

    public ArrayList<HashMap<String, String>> getAllLogs() {
        Cursor cursor = database.query("Logs", new String[]{"event", "details", "date", "time"},
                null, null, null, null, null);

        ArrayList<HashMap<String, String>> logsList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                HashMap<String, String> logData = new HashMap<>();
                int eventIndex = cursor.getColumnIndex("event");
                int detailsIndex = cursor.getColumnIndex("details");
                int dateIndex = cursor.getColumnIndex("date");
                int timeIndex = cursor.getColumnIndex("time");

                logData.put("event", cursor.getString(eventIndex));
                logData.put("details", cursor.getString(detailsIndex));
                logData.put("date", cursor.getString(dateIndex));
                logData.put("time", cursor.getString(timeIndex));

                logsList.add(logData);
            }
            cursor.close();
        }

        return logsList;
    }


    // device_info Table Methods

    public boolean updateAdminStatus(boolean admin_enabled) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("admin_enabled", admin_enabled ? 1 : 0);
        return database.update("device_info", contentValues, null, null) > 0;
    }
    public boolean getAdminStatus() {
        boolean enabled = false;
        Cursor cursor = database.query("device_info", new String[]{"admin_enabled"},
                null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int adminEnabledIndex = cursor.getColumnIndex("admin_enabled");

                enabled =  cursor.getInt(adminEnabledIndex) == 1;

            }

            cursor.close();
        }

        return enabled;
    }

    public boolean updateDeviceInfo(boolean admin_enabled, boolean stolen, int invalidAttempts, String blockPhoneNumber, boolean callListener, boolean simChangeAlert) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("admin_enabled", admin_enabled ? 1 : 0);
        contentValues.put("stolen", stolen ? 1 : 0);
        contentValues.put("invalid_attempts", invalidAttempts);
        contentValues.put("block_phone_no", blockPhoneNumber);
        contentValues.put("call_listener", callListener ? 1 : 0);
        contentValues.put("sim_change_alert", simChangeAlert ? 1 : 0);
        return database.update("device_info", contentValues, null, null) > 0;
    }
    public boolean updateSettings(int invalidAttempts, String blockPhoneNumber, boolean callListener, boolean simChangeAlert) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("invalid_attempts", invalidAttempts);
        contentValues.put("block_phone_no", blockPhoneNumber);
        contentValues.put("call_listener", callListener ? 1 : 0);
        contentValues.put("sim_change_alert", simChangeAlert ? 1 : 0);
        return database.update("device_info", contentValues, null, null) > 0;
    }
    public boolean setDeviceStatus(boolean stolen) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("stolen", stolen);
        return database.update("device_info", contentValues, null, null) > 0;
    }

    public HashMap<String, Object> getDeviceInfo() {
        HashMap<String, Object> dataMap = new HashMap<>();
        Cursor cursor = database.query("device_info", new String[]{"admin_enabled","stolen", "invalid_attempts", "block_phone_no", "call_listener", "sim_change_alert"},
                null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int adminEnabledIndex = cursor.getColumnIndex("admin_enabled");
                int stolenIndex = cursor.getColumnIndex("stolen");
                int invalidAttemptsIndex = cursor.getColumnIndex("invalid_attempts");
                int blockPhoneNumberIndex = cursor.getColumnIndex("block_phone_no");
                int call_listenerIndex = cursor.getColumnIndex("call_listener");
                int simChangeAlertIndex = cursor.getColumnIndex("sim_change_alert");

                dataMap.put("admin_enabled", cursor.getInt(adminEnabledIndex) == 1);
                dataMap.put("stolen", cursor.getInt(stolenIndex) == 1);
                dataMap.put("invalid_attempts", cursor.getInt(invalidAttemptsIndex));
                dataMap.put("block_phone_no", cursor.getString(blockPhoneNumberIndex));
                dataMap.put("call_listener", cursor.getInt(call_listenerIndex) == 1);
                dataMap.put("sim_change_alert", cursor.getInt(simChangeAlertIndex) == 1);
            }

            cursor.close();
        }

        return dataMap;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create Logs Table
            String createLogsTable = "CREATE TABLE Logs (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "event TEXT, " +
                    "details TEXT, " +
                    "date TEXT, " +
                    "time TEXT);";
            db.execSQL(createLogsTable);

            // Create device_info Table
            String createDeviceInfoTable = "CREATE TABLE device_info (" +
                    "admin_enabled INTEGER, " +
                    "stolen INTEGER, " +
                    "invalid_attempts INTEGER, " +
                    "block_phone_no TEXT, " +
                    "call_listener INTEGER, " +
                    "sim_change_alert INTEGER);";
            db.execSQL(createDeviceInfoTable);

            // Insert default values into device_info if the table is empty
            if (isDeviceInfoTableEmpty(db)) {
                ContentValues defaultValues = new ContentValues();
                defaultValues.put("admin_enabled", 0);
                defaultValues.put("stolen", 0); // false
                defaultValues.put("invalid_attempts", 3); //lock on 3 wrong attempts
                defaultValues.put("block_phone_no", "03001234567");
                defaultValues.put("call_listener", 0); // false
                defaultValues.put("sim_change_alert", 0); // false
                db.insert("device_info", null, defaultValues);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS Logs");
            db.execSQL("DROP TABLE IF EXISTS device_info");
            onCreate(db);
        }
        private boolean isDeviceInfoTableEmpty(SQLiteDatabase db) {
            Cursor cursor = db.rawQuery("SELECT 1 FROM device_info", null);
            boolean isEmpty = true;

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    isEmpty = false;
                }
                cursor.close();
            }

            return isEmpty;
        }
    }
}
