package com.wrms.spraymonitor.app;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    // Initial Configuration
    public static final String DB_NAME = "spray_monitoring";
    private static final int DATABASE_VER = 4;
    private static final String TAG = "DBAdapter";

    private final Context context;

    private DatabaseHelper DBHelper;
    public SQLiteDatabase db = null;


    public static final String TABLE_SYNC_DETAIL = "sync_detail";
    public static final String TABLE_CREDENTIAL = "credential";
    public static final String TABLE_STATE = "state";
    public static final String TABLE_DISTRICT = "district";
    public static final String RATE_LIST = "rate_list";

    public static final String TABLE_TEHSIL = "tehsil";
    public static final String TABLE_MACHINE_ID = "machine_id";
    public static final String TABLE_SPRAY_MONITORING_FORM = "spray_monitoring_form";
    public static final String TABLE_FARMER = "farmer";
    public static final String TABLE_PAYMENT = "payment";
    public static final String TABLE_IMAGE = "image";

    public static final String TABLE_FUEL_IMAGE = "fuel_image";

    public static final String TABLE_VIDEO = "video";
    public static final String TABLE_CROP = "crop";
    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_PRODUCT_CROP_ASSIGNMENT = "product_crop_assignment";
    public static final String TABLE_MEASURING_UNIT = "measuring_unit";
    public static final String TABLE_FIELD_COLLECTOR = "field_collector";
    public static final String TABLE_VILLAGE = "village";
    public static final String TABLE_SPRAYED_PRODUCT = "sprayed_product";
    public static final String TABLE_SPRAY_CROPS = "spray_crops";
    public static final String TABLE_TANK_FILLING = "tank_filling";

    public static final String ID = "id";

    public static final String ON_SPOT = "on_spot";
    public static final String LATER = "later";

    public static final String STATE_ID = "state_id";
    public static final String STATE = "state";
    public static final String DISTRICT = "district";
    public static final String DISTRICT_ID = "district_id";
    public static final String MACHINE = "location";
    public static final String MACHINE_ID = "location_id";

    public static final String DISTRICT_CODE = "district_id";
    public static final String RATE = "rate";

    public static final String MACHINE_START_HOUR = "machine_start_hour";
    public static final String MACHINE_STOP_HOUR = "machine_stop_hour";

    public static final String TEHSIL = "tehsil";
    public static final String TEHSIL_ID = "tehsil_id";
    public static final String ADDRESS = "address";

    public static final String VIDEO_NAME = "video_name";
    public static final String VIDEO_PATH = "video_path";
    public static final String IS_TRANSCODED = "is_transcoded";

    public static final String IMAGE_NAME = "image_name";
    public static final String IMAGE_PATH = "image_path";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String CREATED_DATE_TIME = "created_date_time";


    public static final String IMEI = "imei";
    public static final String URL = "url";

    public static final String SPRAY_MONITORING_ID = "spray_monitoring_id";
    public static final String COUNT_TYPE = "form_type";
    public static final String SENDING_STATUS = "sending_status";

    public static final String SAVE = "Saved";
    public static final String SUBMIT = "Submit";
    public static final String SENT = "Sent";
    public static final String CONFIRM = "Confirm";
    public static final String REJECTED = "Rejected";
    public static final String TRUE = "1";
    public static final String FALSE = "0";

    public static final String CELL_ID = "cell_id";
    public static final String LAC_ID = "lac_id";
    public static final String MCC = "mcc";
    public static final String MNC = "mnc";

    public static final String USER_NAME = "user_name";
    public static final String USERID = "userid";
    public static final String FO_CODE = "fo_code";
    public static final String FO_NAME = "fo_name";
    public static final String MOBILE_NO = "mobile_no";
    public static final String PASSWORD = "password";
    public static final String VERSION = "version";

    public static final String CROP_ID = "crop_id";
    public static final String CROP = "crop_name";
    public static final String CROP_TYPE = "crop_type";

    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_DATA_ID = "product_data_id";
    public static final String PRODUCT = "product_name";
    public static final String MEASURING_UNIT_ID = "measuring_unit_id";
    public static final String MEASURING_UNIT = "measuring_unit_name";
    public static final String MEASURING_UNIT_TYPE = "measuring_unit_type";

    public static final String FARMER_ID = "farmer_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String FARMER_CODE = "farmer_code";
    public static final String BARCODE = "barcode";
    public static final String FARMER_CONTACT = "farmer_contact";
    public static final String VILLAGE = "village";
    public static final String VILLAGE_ID = "village_id";
    public static final String PRODUCT_QTY = "product_qty";
    public static final String TOTAL_ACRA = "total_acra";
    public static final String TOTAL_ACRA_OF_CROP = "total_acra_of_crop";
    public static final String ACRA_COVERED = "acra_covered";
    public static final String ACTUAL_ACRA_COVERED = "actual_acra_covered";
    public static final String PRODUCT_SPRAY_COUNT = "product_spray_count";
    public static final String SPRAYED_QTY = "sprayed_qty";
    public static final String AMOUNT_RECEIVABLE = "amount_receivable";
    public static final String AMOUNT_COLLECTED = "amount_collected";
    public static final String BALANCE_AMOUNT = "balance_amount";
    public static final String PAYMENT_STATUS = "payment_status";
    public static final String COLLECTED_BY = "collected_by";
    public static final String COMPLAINT = "complaint";
    public static final String REMARK = "remark";
    public static final String PAYMENT_COLLECTOR_NAME = "payment_collector_name";

    public static final String FIELD_COLLECTOR_ID = "field_collector_id";
    public static final String FIELD_COLLECTOR = "field_collector";
    public static final String CREATED_BY = "created_by";

    public static final String CREATED_BY_SERVER = "created_by_server";
    public static final String CREATED_BY_USER = "created_by_user";

    public static final String PAYMENT_ID = "payment_id";




    public static final String FUEL_ID = "fuel_id";
    public static final String FUEL_DATE = "fuel_date";
    public static final String MORNING_FUEL_LEVEL = "morning_fuel_level";
    public static final String EVENING_FUEL_LEVEL = "evening_fuel_level";


    public static final String CONSUMED_FUEL = "consumed_fuel";
    public static final String DIESEL_FILLED = "diesel_filled"; //1 for filled,0 for not fielled
    public static final String BILL_NO = "bill_no";
    public static final String BILL_AMOUNT = "bill_amount";
    public static final String FILLED_DIESEL_AMOUNT = "filled_diesel_amount";

    public static final String BILL_NO1 = "bill_no1";
    public static final String BILL_AMOUNT1 = "bill_amount1";
    public static final String FILLED_DIESEL_AMOUNT1 = "filled_diesel_amount1";

    public static final String BILL_NO2 = "bill_no2";
    public static final String BILL_AMOUNT2 = "bill_amount2";
    public static final String FILLED_DIESEL_AMOUNT2 = "filled_diesel_amount2";

    public static final String TRACTOR_LEASED = "tractor_leased";
    public static final String COST_PER_DAY = "cost_per_day";
    public static final String TABLE_FUEL_MANAGER = "fuel_manager";

    public static final String TABLE_RUN_TIME = "run_time";

    public static final String CROP_ACRE = "crop_acre";
    public static final String SPRAY_CROP_ID = "spray_crop_id";

    public static final String TANK_FILLING_CROP = "tank_filling_crop";
    public static final String TANK_FILLING_START_TIME = "tank_filling_start_time";
    public static final String TANK_FILLING_STOP_TIME = "tank_filling_stop_time";
    public static final String ACRE_COVERED_BY_TANK = "acre_covered_by_tank";
    public static final String TANK_FILLING_COUNT = "tank_filling_count";
    public static final String TANK_FILLING_ID = "tank_filling_id";

    private static final String CREATE_FUEL_MANAGER = "CREATE TABLE " + TABLE_FUEL_MANAGER + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FUEL_ID + " TEXT NOT NULL,"
            + FUEL_DATE + " TEXT NOT NULL,"
            + MACHINE_ID + " TEXT,"
            + MORNING_FUEL_LEVEL + " TEXT,"
            + EVENING_FUEL_LEVEL + " TEXT,"
            + CONSUMED_FUEL + " TEXT,"
            + DIESEL_FILLED + " TEXT,"
            + BILL_NO + " TEXT,"
            + BILL_AMOUNT + " TEXT,"
            + FILLED_DIESEL_AMOUNT + " TEXT,"

            + BILL_NO1 + " TEXT,"
            + BILL_AMOUNT1 + " TEXT,"
            + FILLED_DIESEL_AMOUNT1 + " TEXT,"

            + BILL_NO2 + " TEXT,"
            + BILL_AMOUNT2 + " TEXT,"
            + FILLED_DIESEL_AMOUNT2 + " TEXT,"

            + TRACTOR_LEASED + " TEXT,"
            + COST_PER_DAY + " TEXT,"
            + REMARK + " TEXT,"
            + CREATED_DATE_TIME + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";


    private static final String CREATE_RUN_TIME = "CREATE TABLE " + TABLE_RUN_TIME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FUEL_ID + " TEXT NOT NULL,"
            + FUEL_DATE + " TEXT NOT NULL,"
            + MACHINE_ID + " TEXT,"
            + MACHINE_START_HOUR + " TEXT,"
            + MACHINE_STOP_HOUR + " TEXT,"
            + CREATED_DATE_TIME + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";


    private static final String CREATE_FARMER = "CREATE TABLE " + TABLE_FARMER + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FARMER_ID + " TEXT NOT NULL,"
            + FARMER_CONTACT + " TEXT NOT NULL UNIQUE,"
            + FIRST_NAME + " TEXT,"
            + LAST_NAME + " TEXT,"
            + FARMER_CODE + " TEXT,"
            + VILLAGE_ID + " TEXT,"
            + ADDRESS + " TEXT,"
            + CROP_ID + " TEXT,"
            + TOTAL_ACRA + " TEXT,"
            + CREATED_BY + " TEXT,"
            + CREATED_DATE_TIME + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";

    private static final String CREATE_PAYMENT = "CREATE TABLE " + TABLE_PAYMENT + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PAYMENT_ID + " TEXT NOT NULL,"
            + FARMER_ID + " TEXT,"
            + SPRAY_MONITORING_ID + " TEXT NOT NULL,"
            + AMOUNT_COLLECTED + " TEXT NOT NULL,"
            + BALANCE_AMOUNT + " TEXT,"
            + CREATED_DATE_TIME + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";

    private static final String CREATE_SPRAY_MONITORING_FORM = "CREATE TABLE " + TABLE_SPRAY_MONITORING_FORM + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SPRAY_MONITORING_ID + " TEXT NOT NULL,"
            + MACHINE_ID + " TEXT,"
            + FARMER_ID + " TEXT,"
            + FIRST_NAME + " TEXT,"
            + FARMER_CONTACT + " TEXT,"
            + STATE_ID + " TEXT ,"
            + DISTRICT_ID + " TEXT,"
            + TEHSIL_ID + " TEXT,"
            + VILLAGE + " TEXT,"
            + CROP_ID + " TEXT,"
            + PRODUCT_ID + " TEXT,"
            + MEASURING_UNIT_ID + " TEXT,"
            + PRODUCT_QTY + " TEXT,"
            + BARCODE + " TEXT,"
            + TOTAL_ACRA + " TEXT,"
            + TOTAL_ACRA_OF_CROP + " TEXT,"
            + ACRA_COVERED + " TEXT,"
            + ACTUAL_ACRA_COVERED + " TEXT,"
            + PRODUCT_SPRAY_COUNT + " TEXT,"
            + SPRAYED_QTY + " TEXT,"
            + AMOUNT_RECEIVABLE + " TEXT,"
            + AMOUNT_COLLECTED + " TEXT,"
            + BALANCE_AMOUNT + " TEXT,"
            + COLLECTED_BY + " TEXT,"
            + COMPLAINT + " TEXT,"
            + REMARK + " TEXT,"
            + LAT + " TEXT,"
            + LON + " TEXT,"
            + CELL_ID + " TEXT ,"
            + LAC_ID + " TEXT ,"
            + MCC + " TEXT ,"
            + MNC + " TEXT,"
            + CREATED_DATE_TIME + " TEXT NOT NULL,"
            + PAYMENT_STATUS + " TEXT ,"
            + PAYMENT_COLLECTOR_NAME + " TEXT,"
            + CROP_TYPE + " TEXT,"
            + SENDING_STATUS + " TEXT);";

    private static final String CREATE_TANK_FILLING = "CREATE TABLE " + TABLE_TANK_FILLING + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SPRAY_MONITORING_ID + " TEXT NOT NULL,"
            + TANK_FILLING_ID + " TEXT NOT NULL,"
            + TANK_FILLING_CROP + " TEXT NOT NULL,"
            + ACRE_COVERED_BY_TANK + " TEXT NOT NULL,"
            + TANK_FILLING_START_TIME + " TEXT,"
            + TANK_FILLING_STOP_TIME + " TEXT,"
            + TANK_FILLING_COUNT + " TEXT);";

    private static final String CREATE_SPARYED_PRODUCT = "CREATE TABLE " + TABLE_SPRAYED_PRODUCT + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TANK_FILLING_ID + " TEXT NOT NULL,"
            + PRODUCT_DATA_ID + " TEXT NOT NULL UNIQUE,"
            + PRODUCT_ID + " TEXT NOT NULL,"
            + MEASURING_UNIT_ID + " TEXT,"
            + PRODUCT_QTY + " TEXT);";

    private static final String CREATE_SPRAY_CROPS = "CREATE TABLE " + TABLE_SPRAY_CROPS + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SPRAY_MONITORING_ID + " TEXT NOT NULL,"
            + SPRAY_CROP_ID + " TEXT NOT NULL UNIQUE,"
            + CROP_ID + " TEXT NOT NULL,"
            + CROP_ACRE + " TEXT);";

    private static final String CREATE_PRODUCT_CROP_ASSIGNMENT = "CREATE TABLE " + TABLE_PRODUCT_CROP_ASSIGNMENT + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRODUCT_ID + " TEXT NOT NULL, "
            + CROP_ID + " TEXT NOT NULL);";


    private static final String CREATE_SYNC_DETAIL = "CREATE TABLE " + TABLE_SYNC_DETAIL + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CREATED_DATE_TIME + " TEXT NOT NULL, "
            + VERSION + " TEXT NOT NULL);";

    private static final String CREATE_CREDENTIAL = "CREATE TABLE " + TABLE_CREDENTIAL + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CREATED_DATE_TIME + " TEXT NOT NULL, "
            + USERID + " TEXT NOT NULL,"
            + USER_NAME + " TEXT NOT NULL,"
            + FO_CODE + " TEXT NOT NULL,"
            + IMEI + " TEXT NOT NULL,"
            + FO_NAME + " TEXT ,"
            + MOBILE_NO + " TEXT ,"
            + PASSWORD + " TEXT NOT NULL);";

    private static final String CREATE_STATE = "CREATE TABLE " + TABLE_STATE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STATE_ID + " TEXT NOT NULL,"
            + STATE + " TEXT NOT NULL);";


    private static final String CREATE_DISTRICT = "CREATE TABLE " + TABLE_DISTRICT + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DISTRICT_ID + " TEXT NOT NULL,"
            + DISTRICT + " TEXT NOT NULL,"
            + CROP_TYPE + " TEXT NOT NULL,"
            + STATE_ID + " TEXT NOT NULL);";

    private static final String CREATE_RATE_LIST = "CREATE TABLE " + RATE_LIST + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DISTRICT_CODE + " TEXT NOT NULL,"
            + RATE + " TEXT NOT NULL,"
            + CROP_TYPE + " TEXT NOT NULL);";


    private static final String CREATE_TEHSIL = "CREATE TABLE " + TABLE_TEHSIL + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEHSIL_ID + " TEXT NOT NULL,"
            + TEHSIL + " TEXT NOT NULL,"
            + DISTRICT_ID + " TEXT NOT NULL);";

    private static final String CREATE_VILLAGE = "CREATE TABLE " + TABLE_VILLAGE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VILLAGE_ID + " TEXT NOT NULL,"
            + VILLAGE + " TEXT NOT NULL,"
            + TEHSIL_ID + " TEXT NOT NULL);";


    private static final String CREATE_MACHINE = "CREATE TABLE " + TABLE_MACHINE_ID + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MACHINE_ID + " TEXT NOT NULL,"
            + MACHINE + " TEXT NOT NULL,"
            + CROP_TYPE + " TEXT NOT NULL);";

    private static final String CREATE_IMAGE = "CREATE TABLE " + TABLE_IMAGE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IMAGE_NAME + " TEXT NOT NULL,"
            + IMAGE_PATH + " TEXT NOT NULL,"
            + LAT + " TEXT,"
            + LON + " TEXT,"
            + CELL_ID + " TEXT ,"
            + LAC_ID + " TEXT ,"
            + MCC + " TEXT ,"
            + MNC + " TEXT,"
            + CREATED_DATE_TIME + " TEXT, "
            + SPRAY_MONITORING_ID + " TEXT NOT NULL,"
            + COUNT_TYPE + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";

    private static final String CREATE_FUEL_IMAGE = "CREATE TABLE " + TABLE_FUEL_IMAGE + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IMAGE_NAME + " TEXT NOT NULL,"
            + IMAGE_PATH + " TEXT NOT NULL,"
            + LAT + " TEXT,"
            + LON + " TEXT,"
            + CREATED_DATE_TIME + " TEXT, "
            + FUEL_ID + " TEXT NOT NULL,"
            + MACHINE_ID + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";

    private static final String CREATE_VIDEO = "CREATE TABLE " + TABLE_VIDEO + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VIDEO_NAME + " TEXT NOT NULL,"
            + VIDEO_PATH + " TEXT NOT NULL,"
            + LAT + " TEXT,"
            + LON + " TEXT,"
            + CELL_ID + " TEXT ,"
            + LAC_ID + " TEXT ,"
            + MCC + " TEXT ,"
            + MNC + " TEXT,"
            + CREATED_DATE_TIME + " TEXT, "
            + SPRAY_MONITORING_ID + " TEXT NOT NULL,"
            + COUNT_TYPE + " TEXT NOT NULL,"
            + IS_TRANSCODED + " TEXT NOT NULL,"
            + SENDING_STATUS + " TEXT);";

    private static final String CREATE_CROP = "CREATE TABLE " + TABLE_CROP + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CROP_ID + " TEXT NOT NULL,"
            + CROP + " TEXT NOT NULL,"
            + CROP_TYPE + " TEXT NOT NULL);";

    private static final String CREATE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRODUCT_ID + " TEXT NOT NULL,"
            + PRODUCT + " TEXT NOT NULL,"
            + MEASURING_UNIT_ID + " TEXT NOT NULL);";

    private static final String CREATE_PRODUCT_MEASURING_UNIT = "CREATE TABLE " + TABLE_MEASURING_UNIT + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + MEASURING_UNIT_ID + " TEXT NOT NULL,"
            + MEASURING_UNIT + " TEXT NOT NULL,"
            + MEASURING_UNIT_TYPE + " TEXT NOT NULL);";

    private static final String CREATE_FIELD_COLLECTOR = "CREATE TABLE " + TABLE_FIELD_COLLECTOR + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FIELD_COLLECTOR_ID + " TEXT NOT NULL,"
            + FIELD_COLLECTOR + " TEXT NOT NULL);";


    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_SPRAY_MONITORING_FORM);
            db.execSQL(CREATE_CREDENTIAL);
            db.execSQL(CREATE_STATE);
            db.execSQL(CREATE_DISTRICT);
            db.execSQL(CREATE_TEHSIL);
            db.execSQL(CREATE_MACHINE);
            db.execSQL(CREATE_IMAGE);
            db.execSQL(CREATE_FUEL_IMAGE);
            db.execSQL(CREATE_VIDEO);
            db.execSQL(CREATE_CROP);
            db.execSQL(CREATE_PRODUCT);
            db.execSQL(CREATE_PRODUCT_MEASURING_UNIT);
            db.execSQL(CREATE_FIELD_COLLECTOR);
            db.execSQL(CREATE_FARMER);
            db.execSQL(CREATE_VILLAGE);
            db.execSQL(CREATE_SYNC_DETAIL);
            db.execSQL(CREATE_PRODUCT_CROP_ASSIGNMENT);
            db.execSQL(CREATE_SPARYED_PRODUCT);
            db.execSQL(CREATE_PAYMENT);
            db.execSQL(CREATE_FUEL_MANAGER);
            db.execSQL(CREATE_RUN_TIME);
            db.execSQL(CREATE_SPRAY_CROPS);
            db.execSQL(CREATE_TANK_FILLING);
            db.execSQL(CREATE_RATE_LIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            /**
             * case count indicates updated database in this version
             */
            switch (oldVersion + 1) {
                case 2:
                    db.execSQL("ALTER TABLE " + TABLE_PAYMENT + " ADD COLUMN "
                            + FARMER_ID + " TEXT");
                case 3:
                    db.execSQL("ALTER TABLE " + TABLE_TANK_FILLING + " ADD COLUMN "
                            + TANK_FILLING_START_TIME + " TEXT");
                    db.execSQL("ALTER TABLE " + TABLE_TANK_FILLING + " ADD COLUMN "
                            + TANK_FILLING_STOP_TIME + " TEXT");

                case 4:
                    db.execSQL("ALTER TABLE " + TABLE_CREDENTIAL + " ADD COLUMN "
                            + FO_NAME + " TEXT");
                    db.execSQL("ALTER TABLE " + TABLE_CREDENTIAL + " ADD COLUMN "
                            + MOBILE_NO + " TEXT");
                case 5:
                case 6:
                case 7:
                case 8:
            }

			/*db.execSQL("ALTER TABLE " + TABLE_PAYMENT + " ADD COLUMN "
                    + FARMER_ID + " TEXT");*/

            Log.d(TAG, "after upgrade logic, at version " + newVersion);

        }
    }

    public SQLiteDatabase getSQLiteDatabase() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        return db;
    }

    public void deletefromtable(String tablename) {
        db.delete(tablename, null, null);
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public Cursor getFarmerList() {
        return db.query(TABLE_FARMER, null, null, null, null, null, null, null);
    }

    public Cursor getFarmerById(String farmerId) {
        return db.query(TABLE_FARMER, null, FARMER_ID + " = '" + farmerId + "'", null, null, null, null, null);
    }

    public Cursor getFarmerByContact(String farmerContact) {
        return db.query(TABLE_FARMER, null, FARMER_CONTACT + " = '" + farmerContact + "'", null, null, null, null, null);
    }

    public Cursor getFarmerCreatedByUser() {
        return db.query(TABLE_FARMER, null, CREATED_BY + " = '" + CREATED_BY_USER + "'", null, null, null, null, null);
    }

    public Cursor getSubmittedFarmer() {
        return db.query(TABLE_FARMER, null, SENDING_STATUS + " = '" + SUBMIT + "'", null, null, null, null, null);
    }

    public Cursor getSavedFarmer() {
        return db.query(TABLE_FARMER, null, SENDING_STATUS + " = '" + SAVE + "'", null, null, null, null, null);
    }

    public Cursor getSavedConfirmed() {
        return db.query(TABLE_FARMER, null, SENDING_STATUS + " = '" + CONFIRM + "'", null, null, null, null, null);
    }

    public Cursor getSavedRejected() {
        return db.query(TABLE_FARMER, null, SENDING_STATUS + " = '" + REJECTED + "'", null, null, null, null, null);
    }

    public Cursor getFormByID(String Id) {
        return db.query(TABLE_SPRAY_MONITORING_FORM, new String[]{ID, SPRAY_MONITORING_ID,
                MACHINE_ID, FARMER_ID, FIRST_NAME, FARMER_CONTACT, STATE_ID, DISTRICT_ID,
                TEHSIL_ID, VILLAGE, CROP_ID, PRODUCT_ID, MEASURING_UNIT_ID, PRODUCT_QTY, TOTAL_ACRA, ACRA_COVERED, TOTAL_ACRA_OF_CROP, ACTUAL_ACRA_COVERED, PRODUCT_SPRAY_COUNT, SPRAYED_QTY,
                AMOUNT_RECEIVABLE, AMOUNT_COLLECTED, BALANCE_AMOUNT, COLLECTED_BY, COMPLAINT, REMARK, LAT, LON, CELL_ID, LAC_ID,
                MCC, MNC, CREATED_DATE_TIME, SENDING_STATUS, BARCODE, PAYMENT_STATUS, PAYMENT_COLLECTOR_NAME,CROP_TYPE}, SPRAY_MONITORING_ID + "= '" + Id + "'", null, null, null, null, null);
    }

    public Cursor getSubmittedForm() {
        return db.query(TABLE_SPRAY_MONITORING_FORM, new String[]{ID, SPRAY_MONITORING_ID,
                MACHINE_ID, FARMER_ID, FIRST_NAME, FARMER_CONTACT, STATE_ID, DISTRICT_ID,
                TEHSIL_ID, VILLAGE, CROP_ID, PRODUCT_ID, MEASURING_UNIT_ID, PRODUCT_QTY, TOTAL_ACRA, TOTAL_ACRA_OF_CROP, ACRA_COVERED, ACTUAL_ACRA_COVERED, PRODUCT_SPRAY_COUNT, SPRAYED_QTY,
                AMOUNT_RECEIVABLE, AMOUNT_COLLECTED, BALANCE_AMOUNT, COLLECTED_BY, COMPLAINT, REMARK, LAT, LON, CELL_ID, LAC_ID,
                MCC, MNC, CREATED_DATE_TIME, SENDING_STATUS, BARCODE, PAYMENT_STATUS, PAYMENT_COLLECTOR_NAME,CROP_TYPE}, SENDING_STATUS + "= '" + SUBMIT + "'", null, null, null, null, null);
    }

    public Cursor getFormID() {
        return db.query(TABLE_SPRAY_MONITORING_FORM, new String[]{ID, SPRAY_MONITORING_ID}, null, null, SPRAY_MONITORING_ID, null, null, null);
    }


    public Cursor getCredential() {
        Cursor cursor = db.query(TABLE_CREDENTIAL, new String[]{ID, USERID, PASSWORD, USER_NAME, FO_CODE, IMEI, CREATED_DATE_TIME,FO_NAME,MOBILE_NO}, null, null, null, null, null, null);
        return cursor;
    }

    public boolean isAuthenticated(String userName, String password) {
        Cursor cursor = db.query(TABLE_CREDENTIAL, new String[]{ID, USERID, USER_NAME, PASSWORD, FO_CODE, CREATED_DATE_TIME,FO_NAME,MOBILE_NO}, USER_NAME + "= '" + userName + "' AND " + PASSWORD + " = '" + password + "'", null, null, null, null, null);
        boolean isAuthenticated = cursor.getCount() > 0 ? true : false;
        return isAuthenticated;
    }

    public String getMachineNameById(String id) {
        String name = id;
        Cursor cursor = db.query(TABLE_MACHINE_ID, new String[]{ID, MACHINE_ID, MACHINE,CROP_TYPE}, MACHINE_ID + " = '" + id + "'", null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.MACHINE));
        }
        cursor.close();
        return name;
    }

    public Cursor getMachineByName(String name) {
        return db.query(TABLE_MACHINE_ID, new String[]{ID, MACHINE_ID, MACHINE,CROP_TYPE}, MACHINE + " = '" + name + "'", null, null, null, null, null);
    }

    public String getStateNameById(String id) {
        String name = "";
        Cursor cursor = db.query(TABLE_STATE, new String[]{ID, STATE_ID, STATE}, STATE_ID + " = '" + id + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.STATE));
        }
        cursor.close();
        return name;
    }


    public String getTehsilNameById(String id) {
        String name = "";
        Cursor cursor = db.query(TABLE_TEHSIL, new String[]{ID, TEHSIL_ID, TEHSIL}, TEHSIL_ID + " = '" + id + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.TEHSIL));
        }
        cursor.close();
        return name;
    }

    public String getDistrictNameById(String id) {
        String name = "";
        Cursor cursor = db.query(TABLE_DISTRICT, new String[]{ID, DISTRICT_ID, DISTRICT,CROP_TYPE}, DISTRICT_ID + " = '" + id + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.DISTRICT));
        }
        cursor.close();
        return name;
    }

    public String getRateByDistCropType(String dist,String cropType) {
        String rate = "";
        Cursor cursor = db.query(RATE_LIST, new String[]{ RATE}, "DISTRICT_ID = ? AND CROP_TYPE = ?",
                new String[] { dist, cropType }, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            rate = cursor.getString(cursor.getColumnIndex(DBAdapter.RATE));
        }
        cursor.close();
        return rate;
    }

    public String getCropNameById(String id) {
        String name = "";
        Cursor cursor = db.query(TABLE_CROP, new String[]{ID, CROP_ID, CROP,CROP_TYPE}, CROP_ID + " = '" + id + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.CROP));
        }
        cursor.close();
        return name;
    }

    public String getProductNameById(String id) {
        String name = "";
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{ID, PRODUCT_ID, PRODUCT}, PRODUCT_ID + " = '" + id + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.PRODUCT));
        }
        cursor.close();
        return name;
    }

    public String getMeasureUnitNameById(String id) {
        String name = "";
        Cursor cursor = db.query(TABLE_MEASURING_UNIT, new String[]{ID, MEASURING_UNIT_ID, MEASURING_UNIT}, MEASURING_UNIT_ID + " = '" + id + "'", null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(DBAdapter.MEASURING_UNIT));
        }
        cursor.close();
        return name;
    }

    public Cursor getAllMachine() {
        return db.query(TABLE_MACHINE_ID, new String[]{ID, MACHINE_ID, MACHINE,CROP_TYPE}, null, null, null, null, null, null);
    }

    public Cursor getAllMachineByCrop(String id) {
        return db.query(TABLE_MACHINE_ID, new String[]{ID, MACHINE_ID, MACHINE, CROP_TYPE}, CROP_TYPE + " = '" + id + "'", null, null, null, null, null);
    }

    public Cursor getAllState() {
        return db.query(TABLE_STATE, new String[]{ID, STATE_ID, STATE}, null, null, null, null, null, null);
    }

    public Cursor getAllDistrict() {
        return db.query(TABLE_DISTRICT, new String[]{ID, DISTRICT, DISTRICT_ID,CROP_TYPE, STATE_ID}, null, null, null, null, null, null);
    }

    public Cursor getDistrictByCrop(String cropT) {
        return db.query(TABLE_DISTRICT, new String[]{ID, DISTRICT, DISTRICT_ID,CROP_TYPE, STATE_ID}, CROP_TYPE + " = '" + cropT + "'", null, null, null, null, null);
    }

    public Cursor getDistrictByState(String id) {
        return db.query(TABLE_DISTRICT, new String[]{ID, DISTRICT, DISTRICT_ID,CROP_TYPE, STATE_ID}, STATE_ID + " = '" + id + "'", null, null, null, null, null);
    }

    public Cursor getTehsilByDistrict(String id) {
        return db.query(TABLE_TEHSIL, new String[]{ID, TEHSIL, TEHSIL_ID, DISTRICT_ID}, DISTRICT_ID + " = '" + id + "'", null, null, null, null, null);
    }

    public Cursor getAllMachineIdCount() {
        return db.query(TABLE_MACHINE_ID, new String[]{ID}, null, null, null, null, null, null);
    }

    public Cursor getSubmittedImage() {
        return db.query(TABLE_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SENDING_STATUS + " = '" + SUBMIT + "'", null, null, null, null, null);
    }

    public Cursor getSubmittedFuelImage() {
        return db.query(TABLE_FUEL_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, FUEL_ID,
                MACHINE_ID, SENDING_STATUS}, SENDING_STATUS + " = '" + SUBMIT + "'", null, null, null, null, null);
    }

    public Cursor getAllFuelImage() {
        return db.query(TABLE_FUEL_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, FUEL_ID,
                MACHINE_ID, SENDING_STATUS}, null, null, null, null, null, null);
    }

    public Cursor getImageByIdCount(String id, String countType, String imageName) {
        return db.query(TABLE_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "' AND " + COUNT_TYPE + " ='" + countType + "' AND " + IMAGE_NAME + "='" + imageName + "'", null, null, null, null, null);
    }

    public Cursor getFuelImageByIdCount(String id, String countType, String imageName) {
        return db.query(TABLE_FUEL_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, FUEL_ID,
                MACHINE_ID, SENDING_STATUS}, FUEL_ID + " = '" + id + "' AND " + MACHINE_ID + " ='" + countType + "' AND " + IMAGE_NAME + "='" + imageName + "'", null, null, null, null, null);
    }

    public Cursor getImageByFormId(String id) {
        return db.query(TABLE_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "'", null, null, null, null, null);
    }

    public Cursor getFuelImageByFormId(String id) {
        return db.query(TABLE_FUEL_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, FUEL_ID,
                MACHINE_ID, SENDING_STATUS}, FUEL_ID + " = '" + id + "'", null, null, null, null, null);
    }

    public Cursor getImageByFormIdAndName(String id, String imgName) {
        return db.query(TABLE_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "' AND " + IMAGE_NAME + " = '" + imgName + "'", null, null, null, null, null);
    }

    public Cursor getFuelImageByFormIdAndName(String id, String imgName) {
        return db.query(TABLE_FUEL_IMAGE, new String[]{ID, IMAGE_NAME, IMAGE_PATH, LAT, LON, CREATED_DATE_TIME, FUEL_ID,
                MACHINE_ID, SENDING_STATUS}, FUEL_ID + " = '" + id + "' AND " + IMAGE_NAME + " = '" + imgName + "'", null, null, null, null, null);
    }

    public Cursor getSubmittedVideo() {
        return db.query(TABLE_VIDEO, new String[]{ID, VIDEO_NAME, VIDEO_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, IS_TRANSCODED, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SENDING_STATUS + " = '" + SUBMIT + "'", null, null, null, null, null);
    }

    public Cursor getVideoByIdDate(String id, String date) {
        return db.query(TABLE_VIDEO, new String[]{ID, VIDEO_NAME, VIDEO_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, IS_TRANSCODED, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "' AND " + CREATED_DATE_TIME + " ='" + date + "'", null, null, null, null, null);
    }

    public Cursor checkVideoForTankfilling(String id, String fillingCount, String videoName) {
        Cursor cursor = db.query(TABLE_VIDEO, new String[]{ID, VIDEO_NAME, VIDEO_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, IS_TRANSCODED, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "' AND " + COUNT_TYPE + " ='" + fillingCount + "' AND " + VIDEO_NAME + " ='" + videoName + "'", null, null, null, null, null);
        return cursor;
    }

    public Cursor videoByIdAndName(String id, String videoName) {
        Cursor cursor = db.query(TABLE_VIDEO, new String[]{ID, VIDEO_NAME, VIDEO_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, IS_TRANSCODED, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "' AND " + VIDEO_NAME + " ='" + videoName + "'", null, null, null, null, null);
        return cursor;
    }

    public Cursor getVideoByFormId(String id) {
        return db.query(TABLE_VIDEO, new String[]{ID, VIDEO_NAME, VIDEO_PATH, LAT, LON, CREATED_DATE_TIME, SPRAY_MONITORING_ID,
                COUNT_TYPE, IS_TRANSCODED, SENDING_STATUS, CELL_ID, LAC_ID, MCC, MNC}, SPRAY_MONITORING_ID + " = '" + id + "'", null, null, null, null, null);
    }

    public Cursor getFieldCollector() {
        return db.query(TABLE_FIELD_COLLECTOR, new String[]{ID, FIELD_COLLECTOR_ID, FIELD_COLLECTOR}, null, null, null, null, null, null);
    }

    public Cursor getAllCrop() {
        return db.query(TABLE_CROP, new String[]{ID, CROP_ID, CROP,CROP_TYPE}, null, null, null, null, null, null);
    }

    public Cursor getAllCropByType(String type_crop) {
        return db.query(TABLE_CROP, new String[]{ID, CROP_ID, CROP,CROP_TYPE}, CROP_TYPE + " ='" + type_crop + "'", null, null, null, null, null);
    }

    public Cursor getAllProduct() {
        return db.query(TABLE_PRODUCT, new String[]{ID, PRODUCT_ID, PRODUCT, MEASURING_UNIT_ID}, null, null, PRODUCT_ID, null, null, null);
    }

    public Cursor getSyncDetail() {
        return db.query(TABLE_SYNC_DETAIL, null, null, null, null, null, null, null);
    }

    public Cursor getProductById(String productId) {
        return db.query(TABLE_PRODUCT, new String[]{ID, PRODUCT_ID, PRODUCT, MEASURING_UNIT_ID}, PRODUCT_ID + " ='" + productId + "'", null, null, null, null, null);
    }

    public Cursor getProductByCropId(String cropId) {
        return db.query(TABLE_PRODUCT_CROP_ASSIGNMENT, new String[]{ID, PRODUCT_ID, CROP_ID}, CROP_ID + " ='" + cropId + "'", null, null, null, null, null);
    }

    public String getUomNameById(String id) {
        Cursor cursor = db.query(TABLE_MEASURING_UNIT, new String[]{ID, MEASURING_UNIT_ID, MEASURING_UNIT, MEASURING_UNIT_TYPE}, MEASURING_UNIT_ID + " ='" + id + "'", null, null, null, null, null);
        String uomName = "No uom For This uom_id";
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            uomName = cursor.getString(cursor.getColumnIndex(MEASURING_UNIT));
        }
        cursor.close();
        return uomName;
    }

    public Cursor getUom() {
        return db.query(TABLE_MEASURING_UNIT, null, null, null, null, null, null, null);
    }

    public Cursor getVillageByTehsilId(String tehsilId) {
        return db.query(TABLE_VILLAGE, null, TEHSIL_ID + " ='" + tehsilId + "'", null, null, null, null, null);
    }

    public Cursor getVillageById(String villageId) {
        return db.query(TABLE_VILLAGE, null, VILLAGE_ID + " ='" + villageId + "'", null, null, null, null, null);
    }

    public Cursor getTehsilById(String tehsilId) {
        return db.query(TABLE_TEHSIL, null, TEHSIL_ID + " ='" + tehsilId + "'", null, null, null, null, null);
    }

    public Cursor getDistrictById(String districtId) {
        return db.query(TABLE_DISTRICT, null, DISTRICT_ID + " ='" + districtId + "'", null, null, null, null, null);
    }

    public Cursor getStateById(String stateId) {
        return db.query(TABLE_STATE, null, STATE_ID + " ='" + stateId + "'", null, null, null, null, null);
    }

    public Cursor getSprayedProduct(String tankFillingId) {
        return db.query(TABLE_SPRAYED_PRODUCT, new String[]{PRODUCT_DATA_ID}, TANK_FILLING_ID + " ='" + tankFillingId + "'", null, null, null, null, null);
    }

    public Cursor getSprayedProductById(String productDataId) {
        return db.query(TABLE_SPRAYED_PRODUCT, null, PRODUCT_DATA_ID + " ='" + productDataId + "'", null, null, null, null, null);
    }

    public Cursor getSprayPayment(String sprayId) {
        return db.query(TABLE_PAYMENT, null, SPRAY_MONITORING_ID + " ='" + sprayId + "'", null, null, null, null, null);
    }

    public Cursor getSubmittedPayments() {
        return db.query(TABLE_PAYMENT, null, SENDING_STATUS + " ='" + SUBMIT + "'", null, null, null, null, null);
    }

    public Cursor getSprayPayment(String sprayId, String dateTime) {
        return db.query(TABLE_PAYMENT, null, SPRAY_MONITORING_ID + " ='" + sprayId + "' AND " + CREATED_DATE_TIME + " ='" + dateTime + "'", null, null, null, null, null);
    }

    public Cursor getFuelById(String fuelId) {
        return db.query(TABLE_FUEL_MANAGER, null, FUEL_ID + " ='" + fuelId + "'", null, null, null, null, null);
    }

    public Cursor getSubmittedFuel() {
        return db.query(TABLE_FUEL_MANAGER, new String[]{FUEL_ID}, SENDING_STATUS + " ='" + SUBMIT + "'", null, FUEL_DATE, null, null, null);
    }


    public Cursor getAllFuelDetail() {
        return db.query(TABLE_FUEL_MANAGER, null, null, null, null, null, null, null);
    }

    public Cursor getFuelByDate(String fuelDate) {
        return db.query(TABLE_FUEL_MANAGER, null, FUEL_DATE + " ='" + fuelDate + "'", null, null, null, null, null);
    }


    public Cursor getRunFuelById(String fuelId) {
        return db.query(TABLE_RUN_TIME, null, FUEL_ID + " ='" + fuelId + "'", null, null, null, null, null);
    }

    public Cursor getRunSubmittedFuel() {
        return db.query(TABLE_RUN_TIME, new String[]{FUEL_ID}, SENDING_STATUS + " ='" + SUBMIT + "'", null, FUEL_DATE, null, null, null);
    }


    public Cursor getRunAllFuelDetail() {
        return db.query(TABLE_RUN_TIME, null, null, null, null, null, null, null);
    }

    public Cursor getRunFuelByDate(String fuelDate) {
        return db.query(TABLE_RUN_TIME, null, FUEL_DATE + " ='" + fuelDate + "'", null, null, null, null, null);
    }




    public Cursor getSprayCrop(String sprayId) {
        return db.query(TABLE_SPRAY_CROPS, new String[]{SPRAY_CROP_ID}, SPRAY_MONITORING_ID + " ='" + sprayId + "'", null, null, null, null, null);
    }

    public Cursor getSprayCropById(String sprayCropId) {
        return db.query(TABLE_SPRAY_CROPS, null, SPRAY_CROP_ID + " ='" + sprayCropId + "'", null, null, null, null, null);
    }

    public Cursor getTankFilling(String sprayId) {
        return db.query(TABLE_TANK_FILLING, new String[]{TANK_FILLING_ID}, SPRAY_MONITORING_ID + " ='" + sprayId + "'", null, null, null, null, null);
    }

    public Cursor getTankFillingById(String tankFillingId) {
        return db.query(TABLE_TANK_FILLING, null, TANK_FILLING_ID + " ='" + tankFillingId + "'", null, null, null, null, null);
    }


}
