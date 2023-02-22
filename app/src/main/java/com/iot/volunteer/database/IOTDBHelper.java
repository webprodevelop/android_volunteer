package com.iot.volunteer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iot.volunteer.model.ItemNews;
import com.iot.volunteer.model.ItemFence;
import com.iot.volunteer.model.ItemHeartRate;
import com.iot.volunteer.model.ItemNotification;
import com.iot.volunteer.model.ItemPaidService;
import com.iot.volunteer.model.ItemSensorInfo;
import com.iot.volunteer.model.ItemWatchInfo;

import java.util.ArrayList;
import java.util.List;

public class IOTDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "manage_iot.db";
    private static final String TABLE_RECOMMENDED = "recommended_table";
    private static final String TABLE_KNOWLEDGE = "knowledge_table";
    private static final String TABLE_SENSOR = "sensor_table";
    private static final String TABLE_WATCH = "watch_table";
    private static final String TABLE_NOTIFICATION = "notification_table";
    private static final String TABLE_HEART_RATE = "heart_rate_table";
    private static final String TABLE_PAID_SERVICE = "paid_service_table";
    private static final String TABLE_FENCE = "fence_table";
    private static final int DATABASE_VERSION = 6;

    private static final String COLUMN_NEWS_ID = "_id";
    private static final String COLUMN_NEWS_CREATETIME = "create_time";
    private static final String COLUMN_NEWS_UPDATETIME = "update_time";
    private static final String COLUMN_NEWS_RELEASETIME = "release_time";
    private static final String COLUMN_NEWS_STATUS = "status";
    private static final String COLUMN_NEWS_TITLE = "title";
    private static final String COLUMN_NEWS_CONTENT = "content";
    private static final String COLUMN_NEWS_PICTURE = "picture";
    private static final String COLUMN_NEWS_NEWSTYPE = "news_type";
    private static final String COLUMN_NEWS_PUBLISHTO = "publish_to";
    private static final String COLUMN_NEWS_NEWSBRANCH = "news_branch";
    private static final String COLUMN_NEWS_READCNT = "read_cnt";

    private static final String COLUMN_SENSOR_ID = "_id";
    private static final String COLUMN_SENSOR_SERIAL = "serial";
    private static final String COLUMN_SENSOR_IS_MANAGER = "is_manager";
    private static final String COLUMN_SENSOR_SERVICE_START = "service_start";
    private static final String COLUMN_SENSOR_SERVICE_END = "service_end";
    private static final String COLUMN_SENSOR_NET_STATUS = "net_status";
    private static final String COLUMN_SENSOR_TYPE = "sensor_type";
    private static final String COLUMN_SENSOR_CONTACT_NAME = "contact_name";
    private static final String COLUMN_SENSOR_CONTACT_PHONE = "contact_phone";
    private static final String COLUMN_SENSOR_LABEL = "location_label";
    private static final String COLUMN_SENSOR_LAT = "lat";
    private static final String COLUMN_SENSOR_LON = "lon";
    private static final String COLUMN_SENSOR_PROVINCE = "province";
    private static final String COLUMN_SENSOR_CITY = "city";
    private static final String COLUMN_SENSOR_DISTRICT = "district";
    private static final String COLUMN_SENSOR_ADDRESS = "address";
    private static final String COLUMN_SENSOR_BATTERY_STATUS = "battery_status";
    private static final String COLUMN_SENSOR_ALARM_STATUS = "alarm_status";

    private static final String COLUMN_WATCH_ID = "_id";
    private static final String COLUMN_WATCH_SERIAL = "serial";
    private static final String COLUMN_WATCH_IS_MANAGER = "is_manager";
    private static final String COLUMN_WATCH_SERVICE_START = "service_start";
    private static final String COLUMN_WATCH_SERVICE_END = "service_end";
    private static final String COLUMN_WATCH_NET_STATUS = "net_status";
    private static final String COLUMN_WATCH_NAME = "name";
    private static final String COLUMN_WATCH_PHONE = "phone";
    private static final String COLUMN_WATCH_SEX = "sex";
    private static final String COLUMN_WATCH_BIRTHDAY = "birthday";
    private static final String COLUMN_WATCH_TALL = "tall";
    private static final String COLUMN_WATCH_WEIGHT = "weight";
    private static final String COLUMN_WATCH_BLOOD = "blood";
    private static final String COLUMN_WATCH_ILL_HISTORY = "ill_history";
    private static final String COLUMN_WATCH_LAT = "lat";
    private static final String COLUMN_WATCH_LON = "lon";
    private static final String COLUMN_WATCH_PROVINCE = "province";
    private static final String COLUMN_WATCH_CITY = "city";
    private static final String COLUMN_WATCH_DISTRICT = "district";
    private static final String COLUMN_WATCH_ADDRESS = "address";
    private static final String COLUMN_WATCH_CHARGE_STATUS = "charge_status";
    private static final String COLUMN_WATCH_CONTACT1_NAME = "sos_contact1_name";
    private static final String COLUMN_WATCH_CONTACT1_PHONE = "sos_contact1_phone";
    private static final String COLUMN_WATCH_CONTACT2_NAME = "sos_contact2_name";
    private static final String COLUMN_WATCH_CONTACT2_PHONE = "sos_contact2_phone";
    private static final String COLUMN_WATCH_CONTACT3_NAME = "sos_contact3_name";
    private static final String COLUMN_WATCH_CONTACT3_PHONE = "sos_contact3_phone";
    private static final String COLUMN_WATCH_HEART_RATE_HIGH = "heart_rate_high";
    private static final String COLUMN_WATCH_HEART_RATE_LOW = "heart_rate_low";
    private static final String COLUMN_WATCH_POS_UPDATE_MODE = "pos_update_mode";

    private static final String COLUMN_NOTIFICATION_ID = "id";
    private static final String COLUMN_NOTIFICATION_TITLE = "title";
    private static final String COLUMN_NOTIFICATION_CONTENT = "content";
    private static final String COLUMN_NOTIFICATION_DATA = "task_id";
    private static final String COLUMN_NOTIFICATION_TYPE = "type";
    private static final String COLUMN_NOTIFICATION_TIME = "time";
    private static final String COLUMN_NOTIFICATION_IS_READ = "is_read";
    private static final String COLUMN_NOTIFICATION_TASK_STATUS = "task_status";
    private static final String COLUMN_NOTIFICATION_NOTICE_TYPE = "notice_type";

    private static final String COLUMN_HEART_RATE_WATCHID = "_id";
    private static final String COLUMN_HEART_RATE_DATE = "check_date";
    private static final String COLUMN_HEART_RATE_TIME = "check_time";
    private static final String COLUMN_HEART_RATE = "heart_rate";

    private static final String COLUMN_PAID_SERVICE_ORDER_ID = "_id";
    private static final String COLUMN_PAID_SERVICE_DEVICE_ID = "item_id";
    private static final String COLUMN_PAID_SERVICE_DEVICE_TYPE = "item_type";
    private static final String COLUMN_PAID_SERVICE_USER = "mobile";
    private static final String COLUMN_PAID_SERVICE_AMOUNT = "amount";
    private static final String COLUMN_PAID_SERVICE_PAY_TYPE = "pay_type";
    private static final String COLUMN_PAID_SERVICE_YEARS = "service_years";
    private static final String COLUMN_PAID_SERVICE_START = "service_start";
    private static final String COLUMN_PAID_SERVICE_END = "service_end";

    private static final String COLUMN_FENCE_ID = "_id";
    private static final String COLUMN_FENCE_NAME = "name";
    private static final String COLUMN_FENCE_ADDRESS = "address";
    private static final String COLUMN_FENCE_LAT = "lat";
    private static final String COLUMN_FENCE_LON = "lot";
    private static final String COLUMN_FENCE_RADIUS = "radius";
    private static final String COLUMN_FENCE_TERMS = "terms";

    private Context context;

    public IOTDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_RECOMMENDED = "CREATE TABLE " + TABLE_RECOMMENDED + "("
                + COLUMN_NEWS_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NEWS_CREATETIME + " TEXT,"
                + COLUMN_NEWS_UPDATETIME + " TEXT,"
                + COLUMN_NEWS_RELEASETIME + " TEXT,"
                + COLUMN_NEWS_STATUS + " TEXT,"
                + COLUMN_NEWS_TITLE + " TEXT,"
                + COLUMN_NEWS_CONTENT + " TEXT,"
                + COLUMN_NEWS_PICTURE + " TEXT,"
                + COLUMN_NEWS_NEWSTYPE + " TEXT,"
                + COLUMN_NEWS_PUBLISHTO + " TEXT,"
                + COLUMN_NEWS_NEWSBRANCH + " TEXT,"
                + COLUMN_NEWS_READCNT + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_RECOMMENDED);

        String CREATE_TABLE_KNOWLEDGE = "CREATE TABLE " + TABLE_KNOWLEDGE + "("
                + COLUMN_NEWS_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NEWS_CREATETIME + " TEXT,"
                + COLUMN_NEWS_UPDATETIME + " TEXT,"
                + COLUMN_NEWS_RELEASETIME + " TEXT,"
                + COLUMN_NEWS_STATUS + " TEXT,"
                + COLUMN_NEWS_TITLE + " TEXT,"
                + COLUMN_NEWS_CONTENT + " TEXT,"
                + COLUMN_NEWS_PICTURE + " TEXT,"
                + COLUMN_NEWS_NEWSTYPE + " TEXT,"
                + COLUMN_NEWS_PUBLISHTO + " TEXT,"
                + COLUMN_NEWS_NEWSBRANCH + " TEXT,"
                + COLUMN_NEWS_READCNT + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_KNOWLEDGE);

        String CREATE_TABLE_SENSOR = "CREATE TABLE " + TABLE_SENSOR + "("
                + COLUMN_SENSOR_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_SENSOR_SERIAL + " TEXT,"
                + COLUMN_SENSOR_IS_MANAGER + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_SERVICE_START + " TEXT,"
                + COLUMN_SENSOR_SERVICE_END + " TEXT,"
                + COLUMN_SENSOR_NET_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_TYPE + " TEXT,"
                + COLUMN_SENSOR_CONTACT_NAME + " TEXT,"
                + COLUMN_SENSOR_CONTACT_PHONE + " TEXT,"
                + COLUMN_SENSOR_LABEL + " TEXT,"
                + COLUMN_SENSOR_LAT + " TEXT,"
                + COLUMN_SENSOR_LON + " TEXT,"
                + COLUMN_SENSOR_PROVINCE + " TEXT,"
                + COLUMN_SENSOR_CITY + " TEXT,"
                + COLUMN_SENSOR_DISTRICT + " TEXT,"
                + COLUMN_SENSOR_ADDRESS + " TEXT,"
                + COLUMN_SENSOR_BATTERY_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_ALARM_STATUS + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_SENSOR);

        String CREATE_TABLE_WATCH = "CREATE TABLE " + TABLE_WATCH + "("
                + COLUMN_WATCH_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_WATCH_SERIAL + " TEXT,"
                + COLUMN_WATCH_IS_MANAGER + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_SERVICE_START + " TEXT,"
                + COLUMN_WATCH_SERVICE_END + " TEXT,"
                + COLUMN_WATCH_NET_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_NAME + " TEXT,"
                + COLUMN_WATCH_PHONE + " TEXT,"
                + COLUMN_WATCH_SEX + " INTEGER DEFAULT -1,"
                + COLUMN_WATCH_BIRTHDAY + " TEXT,"
                + COLUMN_WATCH_TALL + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_WEIGHT + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_BLOOD + " TEXT,"
                + COLUMN_WATCH_ILL_HISTORY + " TEXT,"
                + COLUMN_WATCH_LAT + " TEXT,"
                + COLUMN_WATCH_LON + " TEXT,"
                + COLUMN_WATCH_PROVINCE + " TEXT,"
                + COLUMN_WATCH_CITY + " TEXT,"
                + COLUMN_WATCH_DISTRICT + " TEXT,"
                + COLUMN_WATCH_ADDRESS + " TEXT,"
                + COLUMN_WATCH_CHARGE_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_CONTACT1_NAME + " TEXT,"
                + COLUMN_WATCH_CONTACT1_PHONE + " TEXT,"
                + COLUMN_WATCH_CONTACT2_NAME + " TEXT,"
                + COLUMN_WATCH_CONTACT2_PHONE + " TEXT,"
                + COLUMN_WATCH_CONTACT3_NAME + " TEXT,"
                + COLUMN_WATCH_CONTACT3_PHONE + " TEXT,"
                + COLUMN_WATCH_HEART_RATE_HIGH + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_HEART_RATE_LOW + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_POS_UPDATE_MODE + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_WATCH);

        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + "("
                + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIFICATION_TITLE + " TEXT,"
                + COLUMN_NOTIFICATION_CONTENT + " TEXT,"
                + COLUMN_NOTIFICATION_DATA + " TEXT,"
                + COLUMN_NOTIFICATION_TYPE + " INTEGER DEFAULT 0,"
                + COLUMN_NOTIFICATION_TIME + " LONG DEFAULT 0,"
                + COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0,"
                + COLUMN_NOTIFICATION_TASK_STATUS + " TEXT,"
                + COLUMN_NOTIFICATION_NOTICE_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_NOTIFICATION);

        String CREATE_TABLE_HEART_RATE = "CREATE TABLE " + TABLE_HEART_RATE + "("
                + COLUMN_HEART_RATE_WATCHID + " INTEGER,"
                + COLUMN_HEART_RATE_DATE + " TEXT,"
                + COLUMN_HEART_RATE_TIME + " LONG PRIMARY KEY,"
                + COLUMN_HEART_RATE + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_HEART_RATE);

        String CREATE_TABLE_PAID_SERVICE = "CREATE TABLE " + TABLE_PAID_SERVICE + "("
                + COLUMN_PAID_SERVICE_ORDER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PAID_SERVICE_DEVICE_ID + " INTEGER,"
                + COLUMN_PAID_SERVICE_DEVICE_TYPE + " INTEGER,"
                + COLUMN_PAID_SERVICE_USER + " TEXT,"
                + COLUMN_PAID_SERVICE_AMOUNT + " INTEGER,"
                + COLUMN_PAID_SERVICE_PAY_TYPE + " INTEGER,"
                + COLUMN_PAID_SERVICE_YEARS + " INTEGER,"
                + COLUMN_PAID_SERVICE_START + " TEXT,"
                + COLUMN_PAID_SERVICE_END + " TEXT)";

        db.execSQL(CREATE_TABLE_PAID_SERVICE);

        String CREATE_TABLE_FENCE = "CREATE TABLE " + TABLE_FENCE + "("
                + COLUMN_FENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FENCE_NAME + " TEXT,"
                + COLUMN_FENCE_ADDRESS + " TEXT,"
                + COLUMN_FENCE_LAT + " TEXT,"
                + COLUMN_FENCE_LON + " TEXT,"
                + COLUMN_FENCE_RADIUS + " INTEGER,"
                + COLUMN_FENCE_TERMS + " TEXT)";

        db.execSQL(CREATE_TABLE_FENCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMENDED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KNOWLEDGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEART_RATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAID_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FENCE);

        onCreate(db);
    }

    public void addRecommendedEntry(ItemNews recommendedEntry) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NEWS_ID, recommendedEntry.id);
        contentValues.put(COLUMN_NEWS_CREATETIME, recommendedEntry.createTime);
        contentValues.put(COLUMN_NEWS_UPDATETIME, recommendedEntry.updateTime);
        contentValues.put(COLUMN_NEWS_RELEASETIME, recommendedEntry.releaseTime);
        contentValues.put(COLUMN_NEWS_STATUS, recommendedEntry.status);
        contentValues.put(COLUMN_NEWS_TITLE, recommendedEntry.title);
        contentValues.put(COLUMN_NEWS_CONTENT, recommendedEntry.content);
        contentValues.put(COLUMN_NEWS_PICTURE, recommendedEntry.picture);
        contentValues.put(COLUMN_NEWS_NEWSTYPE, recommendedEntry.newsType);
        contentValues.put(COLUMN_NEWS_PUBLISHTO, recommendedEntry.publishTo);
        contentValues.put(COLUMN_NEWS_NEWSBRANCH, recommendedEntry.newsBranch);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_RECOMMENDED, null, contentValues);
    }

    public void addKnowledgeEntry(ItemNews knowledgeEntry) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NEWS_ID, knowledgeEntry.id);
        contentValues.put(COLUMN_NEWS_CREATETIME, knowledgeEntry.createTime);
        contentValues.put(COLUMN_NEWS_UPDATETIME, knowledgeEntry.updateTime);
        contentValues.put(COLUMN_NEWS_RELEASETIME, knowledgeEntry.releaseTime);
        contentValues.put(COLUMN_NEWS_STATUS, knowledgeEntry.status);
        contentValues.put(COLUMN_NEWS_TITLE, knowledgeEntry.title);
        contentValues.put(COLUMN_NEWS_CONTENT, knowledgeEntry.content);
        contentValues.put(COLUMN_NEWS_PICTURE, knowledgeEntry.picture);
        contentValues.put(COLUMN_NEWS_NEWSTYPE, knowledgeEntry.newsType);
        contentValues.put(COLUMN_NEWS_PUBLISHTO, knowledgeEntry.publishTo);
        contentValues.put(COLUMN_NEWS_NEWSBRANCH, knowledgeEntry.newsBranch);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_KNOWLEDGE, null, contentValues);
    }

    public void addSensorEntry(ItemSensorInfo sensorEntry) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SENSOR_ID, sensorEntry.id);
        contentValues.put(COLUMN_SENSOR_SERIAL, sensorEntry.serial);
        contentValues.put(COLUMN_SENSOR_IS_MANAGER, sensorEntry.isManager);
        contentValues.put(COLUMN_SENSOR_SERVICE_START, sensorEntry.serviceStartDate);
        contentValues.put(COLUMN_SENSOR_SERVICE_END, sensorEntry.serviceEndDate);
        contentValues.put(COLUMN_SENSOR_NET_STATUS, sensorEntry.netStatus);
        contentValues.put(COLUMN_SENSOR_TYPE, sensorEntry.sensorType);
        contentValues.put(COLUMN_SENSOR_CONTACT_NAME, sensorEntry.contactName);
        contentValues.put(COLUMN_SENSOR_CONTACT_PHONE, sensorEntry.contactPhone);
        contentValues.put(COLUMN_SENSOR_LABEL, sensorEntry.locationLabel);
        contentValues.put(COLUMN_SENSOR_LAT, sensorEntry.lat);
        contentValues.put(COLUMN_SENSOR_LON, sensorEntry.lon);
        contentValues.put(COLUMN_SENSOR_PROVINCE, sensorEntry.province);
        contentValues.put(COLUMN_SENSOR_CITY, sensorEntry.city);
        contentValues.put(COLUMN_SENSOR_DISTRICT, sensorEntry.district);
        contentValues.put(COLUMN_SENSOR_ADDRESS, sensorEntry.address);
        contentValues.put(COLUMN_SENSOR_BATTERY_STATUS, sensorEntry.batteryStatus);
        contentValues.put(COLUMN_SENSOR_ALARM_STATUS, sensorEntry.alarmStatus);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_SENSOR, null, contentValues);
    }

    public void addWatchEntry(ItemWatchInfo watchEntry) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WATCH_ID, watchEntry.id);
        contentValues.put(COLUMN_WATCH_SERIAL, watchEntry.serial);
        contentValues.put(COLUMN_WATCH_IS_MANAGER, watchEntry.isManager);
        contentValues.put(COLUMN_WATCH_SERVICE_START, watchEntry.serviceStartDate);
        contentValues.put(COLUMN_WATCH_SERVICE_END, watchEntry.serviceEndDate);
        contentValues.put(COLUMN_WATCH_NET_STATUS, watchEntry.netStatus);
        contentValues.put(COLUMN_WATCH_NAME, watchEntry.name);
        contentValues.put(COLUMN_WATCH_PHONE, watchEntry.phone);
        contentValues.put(COLUMN_WATCH_SEX, watchEntry.sex);
        contentValues.put(COLUMN_WATCH_BIRTHDAY, watchEntry.birthday);
        contentValues.put(COLUMN_WATCH_TALL, watchEntry.tall);
        contentValues.put(COLUMN_WATCH_WEIGHT, watchEntry.weight);
        contentValues.put(COLUMN_WATCH_BLOOD, watchEntry.blood);
        contentValues.put(COLUMN_WATCH_ILL_HISTORY, watchEntry.ill_history);
        contentValues.put(COLUMN_WATCH_LAT, watchEntry.lat);
        contentValues.put(COLUMN_WATCH_LON, watchEntry.lon);
        contentValues.put(COLUMN_WATCH_PROVINCE, watchEntry.province);
        contentValues.put(COLUMN_WATCH_CITY, watchEntry.city);
        contentValues.put(COLUMN_WATCH_DISTRICT, watchEntry.district);
        contentValues.put(COLUMN_WATCH_ADDRESS, watchEntry.address);
        contentValues.put(COLUMN_WATCH_CHARGE_STATUS, watchEntry.chargeStatus);
        contentValues.put(COLUMN_WATCH_CONTACT1_NAME, watchEntry.sosContactName1);
        contentValues.put(COLUMN_WATCH_CONTACT1_PHONE, watchEntry.sosContactPhone1);
        contentValues.put(COLUMN_WATCH_CONTACT2_NAME, watchEntry.sosContactName2);
        contentValues.put(COLUMN_WATCH_CONTACT2_PHONE, watchEntry.sosContactPhone2);
        contentValues.put(COLUMN_WATCH_CONTACT3_NAME, watchEntry.sosContactName3);
        contentValues.put(COLUMN_WATCH_CONTACT3_PHONE, watchEntry.sosContactPhone3);
        contentValues.put(COLUMN_WATCH_HEART_RATE_HIGH, watchEntry.highRate);
        contentValues.put(COLUMN_WATCH_HEART_RATE_LOW, watchEntry.lowRate);
        contentValues.put(COLUMN_WATCH_POS_UPDATE_MODE, watchEntry.posUpdateMode);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_WATCH, null, contentValues);
    }

    public void addNotificationEntry(ItemNotification notificationEntry) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTIFICATION_ID, notificationEntry.id);
        contentValues.put(COLUMN_NOTIFICATION_TITLE, notificationEntry.title);
        contentValues.put(COLUMN_NOTIFICATION_CONTENT, notificationEntry.content);
        contentValues.put(COLUMN_NOTIFICATION_DATA, notificationEntry.taskId);
        contentValues.put(COLUMN_NOTIFICATION_TYPE, notificationEntry.type);
        contentValues.put(COLUMN_NOTIFICATION_TIME, notificationEntry.time);
        contentValues.put(COLUMN_NOTIFICATION_IS_READ, notificationEntry.isRead);
        contentValues.put(COLUMN_NOTIFICATION_TASK_STATUS, notificationEntry.taskStatus);
        contentValues.put(COLUMN_NOTIFICATION_NOTICE_TYPE, notificationEntry.noticeType);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NOTIFICATION, null, contentValues);
    }

    public void addHeartRateEntry(ItemHeartRate heartRateEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_HEART_RATE_WATCHID, heartRateEntry.watchId);
        contentValues.put(COLUMN_HEART_RATE_DATE, heartRateEntry.checkDate);
        contentValues.put(COLUMN_HEART_RATE_TIME, heartRateEntry.checkTime);
        contentValues.put(COLUMN_HEART_RATE, heartRateEntry.heartRate);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_HEART_RATE, null, contentValues);
    }

    public void addPaidServiceEntry(ItemPaidService paidServiceEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PAID_SERVICE_ORDER_ID, paidServiceEntry.orderId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_ID, paidServiceEntry.deviceId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_TYPE, paidServiceEntry.type);
        contentValues.put(COLUMN_PAID_SERVICE_USER, paidServiceEntry.userPhone);
        contentValues.put(COLUMN_PAID_SERVICE_AMOUNT, paidServiceEntry.amount);
        contentValues.put(COLUMN_PAID_SERVICE_PAY_TYPE, paidServiceEntry.payType);
        contentValues.put(COLUMN_PAID_SERVICE_YEARS, paidServiceEntry.serviceYears);
        contentValues.put(COLUMN_PAID_SERVICE_START, paidServiceEntry.serviceStartDate);
        contentValues.put(COLUMN_PAID_SERVICE_END, paidServiceEntry.serviceEndDate);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_PAID_SERVICE, null, contentValues);
    }

    public long addFenceEntry(ItemFence fenceEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FENCE_NAME, fenceEntry.name);
        contentValues.put(COLUMN_FENCE_ADDRESS, fenceEntry.address);
        contentValues.put(COLUMN_FENCE_LAT, fenceEntry.lat);
        contentValues.put(COLUMN_FENCE_LON, fenceEntry.lon);
        contentValues.put(COLUMN_FENCE_RADIUS, fenceEntry.radius);
        contentValues.put(COLUMN_FENCE_TERMS, fenceEntry.strGuardTimeList);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long id = sqLiteDatabase.insert(TABLE_FENCE, null, contentValues);

        return (int)id;
    }

    public void clearAll() {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_SENSOR);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_WATCH);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NOTIFICATION);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_HEART_RATE);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_PAID_SERVICE);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_FENCE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecommendedEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_RECOMMENDED, COLUMN_NEWS_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteKnowledgeEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_KNOWLEDGE, COLUMN_NEWS_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteSensorEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_SENSOR, COLUMN_SENSOR_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteWatchEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_WATCH, COLUMN_WATCH_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteNotificationEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteHeartRateEntry(int watchId) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_HEART_RATE, COLUMN_HEART_RATE_WATCHID + " = ?", new String[]{String.valueOf(watchId)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deletePaidServiceEntry(int orderId) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_PAID_SERVICE, COLUMN_PAID_SERVICE_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteFenceEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_FENCE, COLUMN_FENCE_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void updateRecommendedEntry(ItemNews oldRecommendedEntry, ItemNews newRecommendedEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NEWS_ID, newRecommendedEntry.id);
        contentValues.put(COLUMN_NEWS_CREATETIME, newRecommendedEntry.createTime);
        contentValues.put(COLUMN_NEWS_UPDATETIME, newRecommendedEntry.updateTime);
        contentValues.put(COLUMN_NEWS_RELEASETIME, newRecommendedEntry.releaseTime);
        contentValues.put(COLUMN_NEWS_STATUS, newRecommendedEntry.status);
        contentValues.put(COLUMN_NEWS_TITLE, newRecommendedEntry.title);
        contentValues.put(COLUMN_NEWS_CONTENT, newRecommendedEntry.content);
        contentValues.put(COLUMN_NEWS_PICTURE, newRecommendedEntry.picture);
        contentValues.put(COLUMN_NEWS_NEWSTYPE, newRecommendedEntry.newsType);
        contentValues.put(COLUMN_NEWS_PUBLISHTO, newRecommendedEntry.publishTo);
        contentValues.put(COLUMN_NEWS_NEWSBRANCH, newRecommendedEntry.newsBranch);
        contentValues.put(COLUMN_NEWS_READCNT, newRecommendedEntry.readCnt);

        sqLiteDatabase.update(TABLE_RECOMMENDED, contentValues, COLUMN_NEWS_ID + " = ?",
                new String[]{oldRecommendedEntry.id + ""});
    }

    public void updateKnowledgeEntry(ItemNews oldKnowledgeEntry, ItemNews newKnowledgeEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NEWS_ID, newKnowledgeEntry.id);
        contentValues.put(COLUMN_NEWS_CREATETIME, newKnowledgeEntry.createTime);
        contentValues.put(COLUMN_NEWS_UPDATETIME, newKnowledgeEntry.updateTime);
        contentValues.put(COLUMN_NEWS_RELEASETIME, newKnowledgeEntry.releaseTime);
        contentValues.put(COLUMN_NEWS_STATUS, newKnowledgeEntry.status);
        contentValues.put(COLUMN_NEWS_TITLE, newKnowledgeEntry.title);
        contentValues.put(COLUMN_NEWS_CONTENT, newKnowledgeEntry.content);
        contentValues.put(COLUMN_NEWS_PICTURE, newKnowledgeEntry.picture);
        contentValues.put(COLUMN_NEWS_NEWSTYPE, newKnowledgeEntry.newsType);
        contentValues.put(COLUMN_NEWS_PUBLISHTO, newKnowledgeEntry.publishTo);
        contentValues.put(COLUMN_NEWS_NEWSBRANCH, newKnowledgeEntry.newsBranch);
        contentValues.put(COLUMN_NEWS_READCNT, newKnowledgeEntry.readCnt);

        sqLiteDatabase.update(TABLE_KNOWLEDGE, contentValues, COLUMN_NEWS_ID + " = ?",
                new String[]{oldKnowledgeEntry.id + ""});
    }

    public void updateSensorEntry(ItemSensorInfo oldSensorEntry, ItemSensorInfo newSensorEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SENSOR_ID, newSensorEntry.id);
        contentValues.put(COLUMN_SENSOR_SERIAL, newSensorEntry.serial);
        contentValues.put(COLUMN_SENSOR_IS_MANAGER, newSensorEntry.isManager);
        contentValues.put(COLUMN_SENSOR_SERVICE_START, newSensorEntry.serviceStartDate);
        contentValues.put(COLUMN_SENSOR_SERVICE_END, newSensorEntry.serviceEndDate);
        contentValues.put(COLUMN_SENSOR_NET_STATUS, newSensorEntry.netStatus);
        contentValues.put(COLUMN_SENSOR_TYPE, newSensorEntry.sensorType);
        contentValues.put(COLUMN_SENSOR_CONTACT_NAME, newSensorEntry.contactName);
        contentValues.put(COLUMN_SENSOR_CONTACT_PHONE, newSensorEntry.contactPhone);
        contentValues.put(COLUMN_SENSOR_LABEL, newSensorEntry.locationLabel);
        contentValues.put(COLUMN_SENSOR_LAT, newSensorEntry.lat);
        contentValues.put(COLUMN_SENSOR_LON, newSensorEntry.lon);
        contentValues.put(COLUMN_SENSOR_PROVINCE, newSensorEntry.province);
        contentValues.put(COLUMN_SENSOR_CITY, newSensorEntry.city);
        contentValues.put(COLUMN_SENSOR_DISTRICT, newSensorEntry.district);
        contentValues.put(COLUMN_SENSOR_ADDRESS, newSensorEntry.address);
        contentValues.put(COLUMN_SENSOR_BATTERY_STATUS, newSensorEntry.batteryStatus);
        contentValues.put(COLUMN_SENSOR_ALARM_STATUS, newSensorEntry.alarmStatus);

        sqLiteDatabase.update(TABLE_SENSOR, contentValues, COLUMN_SENSOR_ID + " = ?",
                new String[]{oldSensorEntry.id + ""});
    }

    public void updateWatchEntry(ItemWatchInfo oldWatchEntry, ItemWatchInfo newWatchEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_WATCH_ID, newWatchEntry.id);
        contentValues.put(COLUMN_WATCH_SERIAL, newWatchEntry.serial);
        contentValues.put(COLUMN_WATCH_IS_MANAGER, newWatchEntry.isManager);
        contentValues.put(COLUMN_WATCH_SERVICE_START, newWatchEntry.serviceStartDate);
        contentValues.put(COLUMN_WATCH_SERVICE_END, newWatchEntry.serviceEndDate);
        contentValues.put(COLUMN_WATCH_NET_STATUS, newWatchEntry.netStatus);
        contentValues.put(COLUMN_WATCH_NAME, newWatchEntry.name);
        contentValues.put(COLUMN_WATCH_PHONE, newWatchEntry.phone);
        contentValues.put(COLUMN_WATCH_SEX, newWatchEntry.sex);
        contentValues.put(COLUMN_WATCH_BIRTHDAY, newWatchEntry.birthday);
        contentValues.put(COLUMN_WATCH_TALL, newWatchEntry.tall);
        contentValues.put(COLUMN_WATCH_WEIGHT, newWatchEntry.weight);
        contentValues.put(COLUMN_WATCH_BLOOD, newWatchEntry.blood);
        contentValues.put(COLUMN_WATCH_ILL_HISTORY, newWatchEntry.ill_history);
        contentValues.put(COLUMN_WATCH_LAT, newWatchEntry.lat);
        contentValues.put(COLUMN_WATCH_LON, newWatchEntry.lon);
        contentValues.put(COLUMN_WATCH_PROVINCE, newWatchEntry.province);
        contentValues.put(COLUMN_WATCH_CITY, newWatchEntry.city);
        contentValues.put(COLUMN_WATCH_DISTRICT, newWatchEntry.district);
        contentValues.put(COLUMN_WATCH_ADDRESS, newWatchEntry.address);
        contentValues.put(COLUMN_WATCH_CHARGE_STATUS, newWatchEntry.chargeStatus);
        contentValues.put(COLUMN_WATCH_CONTACT1_NAME, newWatchEntry.sosContactName1);
        contentValues.put(COLUMN_WATCH_CONTACT1_PHONE, newWatchEntry.sosContactPhone1);
        contentValues.put(COLUMN_WATCH_CONTACT2_NAME, newWatchEntry.sosContactName2);
        contentValues.put(COLUMN_WATCH_CONTACT2_PHONE, newWatchEntry.sosContactPhone2);
        contentValues.put(COLUMN_WATCH_CONTACT3_NAME, newWatchEntry.sosContactName3);
        contentValues.put(COLUMN_WATCH_CONTACT3_PHONE, newWatchEntry.sosContactPhone3);
        contentValues.put(COLUMN_WATCH_HEART_RATE_HIGH, newWatchEntry.highRate);
        contentValues.put(COLUMN_WATCH_HEART_RATE_LOW, newWatchEntry.lowRate);
        contentValues.put(COLUMN_WATCH_POS_UPDATE_MODE, newWatchEntry.posUpdateMode);

        sqLiteDatabase.update(TABLE_WATCH, contentValues, COLUMN_WATCH_ID + " = ?",
                new String[]{oldWatchEntry.id + ""});
    }

    public void updateNotificationEntry(ItemNotification oldNotificationEntry, ItemNotification newNotificationEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTIFICATION_ID, newNotificationEntry.id);
        contentValues.put(COLUMN_NOTIFICATION_TITLE, newNotificationEntry.title);
        contentValues.put(COLUMN_NOTIFICATION_CONTENT, newNotificationEntry.content);
        contentValues.put(COLUMN_NOTIFICATION_DATA, newNotificationEntry.taskId);
        contentValues.put(COLUMN_NOTIFICATION_TYPE, newNotificationEntry.type);
        contentValues.put(COLUMN_NOTIFICATION_TIME, newNotificationEntry.time);
        contentValues.put(COLUMN_NOTIFICATION_IS_READ, newNotificationEntry.isRead);
        contentValues.put(COLUMN_NOTIFICATION_TASK_STATUS, newNotificationEntry.taskStatus);
        contentValues.put(COLUMN_NOTIFICATION_NOTICE_TYPE, newNotificationEntry.noticeType);

        sqLiteDatabase.update(TABLE_NOTIFICATION, contentValues, COLUMN_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(oldNotificationEntry.id)});
    }

    public void updateHeartRateEntry(ItemHeartRate oldHeartRateEntry, ItemHeartRate newHeartRateEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_HEART_RATE_WATCHID, newHeartRateEntry.watchId);
        contentValues.put(COLUMN_HEART_RATE_DATE, newHeartRateEntry.checkDate);
        contentValues.put(COLUMN_HEART_RATE_TIME, newHeartRateEntry.checkTime);
        contentValues.put(COLUMN_HEART_RATE, newHeartRateEntry.heartRate);

        sqLiteDatabase.update(TABLE_HEART_RATE, contentValues, COLUMN_HEART_RATE_TIME + " = ?",
                new String[]{oldHeartRateEntry.checkTime + ""});
    }

    public void updatePaidServiceEntry(ItemPaidService oldPaidServiceEntry, ItemPaidService newPaidServiceEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PAID_SERVICE_ORDER_ID, newPaidServiceEntry.orderId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_ID, newPaidServiceEntry.deviceId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_TYPE, newPaidServiceEntry.type);
        contentValues.put(COLUMN_PAID_SERVICE_USER, newPaidServiceEntry.userPhone);
        contentValues.put(COLUMN_PAID_SERVICE_AMOUNT, newPaidServiceEntry.amount);
        contentValues.put(COLUMN_PAID_SERVICE_PAY_TYPE, newPaidServiceEntry.payType);
        contentValues.put(COLUMN_PAID_SERVICE_YEARS, newPaidServiceEntry.serviceYears);
        contentValues.put(COLUMN_PAID_SERVICE_START, newPaidServiceEntry.serviceStartDate);
        contentValues.put(COLUMN_PAID_SERVICE_END, newPaidServiceEntry.serviceEndDate);

        sqLiteDatabase.update(TABLE_PAID_SERVICE, contentValues, COLUMN_PAID_SERVICE_ORDER_ID + " = ?",
                new String[]{oldPaidServiceEntry.orderId + ""});
    }

    public void updateFenceEntry(ItemFence oldFenceEntry, ItemFence newFenceEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FENCE_NAME, newFenceEntry.name);
        contentValues.put(COLUMN_FENCE_ADDRESS, newFenceEntry.address);
        contentValues.put(COLUMN_FENCE_LAT, newFenceEntry.lat);
        contentValues.put(COLUMN_FENCE_LON, newFenceEntry.lon);
        contentValues.put(COLUMN_FENCE_RADIUS, newFenceEntry.radius);
        contentValues.put(COLUMN_FENCE_TERMS, newFenceEntry.strGuardTimeList);

        sqLiteDatabase.update(TABLE_PAID_SERVICE, contentValues, COLUMN_FENCE_ID + " = ?",
                new String[]{oldFenceEntry.id + ""});
    }

    public ItemNews findRecommendedEntry(int id) {
        String query = "Select * FROM " + TABLE_RECOMMENDED + " WHERE " + COLUMN_NEWS_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemNews recommendedEntry = new ItemNews();
        if (cursor.moveToFirst()) {
            recommendedEntry.id = cursor.getInt(0);
            recommendedEntry.createTime = cursor.getString(1);
            recommendedEntry.updateTime = cursor.getString(2);
            recommendedEntry.releaseTime = cursor.getString(3);
            recommendedEntry.status = cursor.getString(4);
            recommendedEntry.title = cursor.getString(5);
            recommendedEntry.content = cursor.getString(6);
            recommendedEntry.picture = cursor.getString(7);
            recommendedEntry.newsType = cursor.getString(8);
            recommendedEntry.publishTo = cursor.getString(9);
            recommendedEntry.newsBranch = cursor.getString(10);
            recommendedEntry.readCnt = cursor.getInt(11);
            cursor.close();
        } else {
            recommendedEntry = null;
        }
        return recommendedEntry;
    }

    public ItemNews findKnowledgeEntry(int id) {
        String query = "Select * FROM " + TABLE_KNOWLEDGE + " WHERE " + COLUMN_NEWS_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemNews knowledgeEntry = new ItemNews();
        if (cursor.moveToFirst()) {
            knowledgeEntry.id = cursor.getInt(0);
            knowledgeEntry.createTime = cursor.getString(1);
            knowledgeEntry.updateTime = cursor.getString(2);
            knowledgeEntry.releaseTime = cursor.getString(3);
            knowledgeEntry.status = cursor.getString(4);
            knowledgeEntry.title = cursor.getString(5);
            knowledgeEntry.content = cursor.getString(6);
            knowledgeEntry.picture = cursor.getString(7);
            knowledgeEntry.newsType = cursor.getString(8);
            knowledgeEntry.publishTo = cursor.getString(9);
            knowledgeEntry.newsBranch = cursor.getString(10);
            knowledgeEntry.readCnt = cursor.getInt(11);
            cursor.close();
        } else {
            knowledgeEntry = null;
        }
        return knowledgeEntry;
    }

    public ItemSensorInfo findSensorEntry(int id) {
        String query = "Select * FROM " + TABLE_SENSOR + " WHERE " + COLUMN_SENSOR_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemSensorInfo sensorEntry = new ItemSensorInfo();
        if (cursor.moveToFirst()) {
            sensorEntry.id = cursor.getInt(0);
            sensorEntry.serial = cursor.getString(1);
            sensorEntry.isManager = (cursor.getInt(2) == 1) ? true : false;
            sensorEntry.serviceStartDate = cursor.getString(3);
            sensorEntry.serviceEndDate = cursor.getString(4);
            sensorEntry.netStatus = (cursor.getInt(5) == 1) ? true : false;
            sensorEntry.sensorType = cursor.getString(6);
            sensorEntry.contactName = cursor.getString(7);
            sensorEntry.contactPhone = cursor.getString(8);
            sensorEntry.locationLabel = cursor.getString(9);
            sensorEntry.lat = cursor.getString(10);
            sensorEntry.lon = cursor.getString(11);
            sensorEntry.province = cursor.getString(12);
            sensorEntry.city = cursor.getString(13);
            sensorEntry.district = cursor.getString(14);
            sensorEntry.address = cursor.getString(15);
            sensorEntry.batteryStatus = (cursor.getInt(16) == 1) ? true : false;
            sensorEntry.alarmStatus = (cursor.getInt(17) == 1) ? true : false;

            cursor.close();
        } else {
            sensorEntry = null;
        }
        return sensorEntry;
    }

    public ItemWatchInfo findWatchEntry(int id) {
        String query = "Select * FROM " + TABLE_WATCH + " WHERE " + COLUMN_WATCH_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemWatchInfo watchEntry = new ItemWatchInfo();
        if (cursor.moveToFirst()) {
            watchEntry.id = cursor.getInt(0);
            watchEntry.serial = cursor.getString(1);
            watchEntry.isManager = (cursor.getInt(2) == 1) ? true : false;
            watchEntry.serviceStartDate = cursor.getString(3);
            watchEntry.serviceEndDate = cursor.getString(4);
            watchEntry.netStatus = (cursor.getInt(5) == 1) ? true : false;
            watchEntry.name = cursor.getString(6);
            watchEntry.phone = cursor.getString(7);
            watchEntry.sex = cursor.getInt(8);
            watchEntry.birthday = cursor.getString(9);
            watchEntry.tall = cursor.getInt(10);
            watchEntry.weight = cursor.getInt(11);
            watchEntry.blood = cursor.getString(12);
            watchEntry.ill_history = cursor.getString(13);
            watchEntry.lat = cursor.getString(14);
            watchEntry.lon = cursor.getString(15);
            watchEntry.province = cursor.getString(16);
            watchEntry.city = cursor.getString(17);
            watchEntry.district = cursor.getString(18);
            watchEntry.address = cursor.getString(19);
            watchEntry.chargeStatus = cursor.getInt(20);
            watchEntry.sosContactName1 = cursor.getString(21);
            watchEntry.sosContactPhone1 = cursor.getString(22);
            watchEntry.sosContactName2 = cursor.getString(23);
            watchEntry.sosContactPhone2 = cursor.getString(24);
            watchEntry.sosContactName3 = cursor.getString(25);
            watchEntry.sosContactPhone3 = cursor.getString(26);
            watchEntry.highRate = cursor.getInt(27);
            watchEntry.lowRate = cursor.getInt(28);
            watchEntry.posUpdateMode = cursor.getInt(29);

            cursor.close();
        } else {
            watchEntry = null;
        }
        return watchEntry;
    }

    public ItemNotification findNotificationEntry(int id) {
        String query = "Select * FROM " + TABLE_NOTIFICATION + " WHERE " + COLUMN_NOTIFICATION_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemNotification notificationEntry = new ItemNotification();
        if (cursor.moveToFirst()) {
            notificationEntry.id = cursor.getInt(0);
            notificationEntry.title = cursor.getString(1);
            notificationEntry.content = cursor.getString(2);
            notificationEntry.taskId = cursor.getString(3);
            notificationEntry.type = cursor.getString(4);
            notificationEntry.time = cursor.getLong(5);
            notificationEntry.isRead = cursor.getInt(6);
            notificationEntry.taskStatus = cursor.getString(7);
            notificationEntry.noticeType = cursor.getString(8);

            cursor.close();
        } else {
            notificationEntry = null;
        }
        return notificationEntry;
    }

    public ItemHeartRate findHeartRateEntry(long checkTime) {
        String query = "Select * FROM " + TABLE_HEART_RATE + " WHERE " + COLUMN_HEART_RATE_TIME + "=" + checkTime;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemHeartRate heartRateEntry = new ItemHeartRate();
        if (cursor.moveToFirst()) {
            heartRateEntry.watchId = cursor.getInt(0);
            heartRateEntry.checkDate = cursor.getString(1);
            heartRateEntry.checkTime = cursor.getLong(2);
            heartRateEntry.heartRate = cursor.getInt(3);

            cursor.close();
        } else {
            heartRateEntry = null;
        }
        return heartRateEntry;
    }

    public ItemPaidService findPaidServieEntry(int orderId) {
        String query = "Select * FROM " + TABLE_PAID_SERVICE + " WHERE " + COLUMN_PAID_SERVICE_ORDER_ID + "=" + orderId;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemPaidService paidServiceEntry = new ItemPaidService();
        if (cursor.moveToFirst()) {
            paidServiceEntry.orderId = cursor.getInt(0);
            paidServiceEntry.deviceId = cursor.getInt(1);
            paidServiceEntry.type = cursor.getInt(2);
            paidServiceEntry.userPhone = cursor.getString(3);
            paidServiceEntry.amount = cursor.getInt(4);
            paidServiceEntry.payType = cursor.getInt(5);
            paidServiceEntry.serviceYears = cursor.getInt(6);
            paidServiceEntry.serviceStartDate = cursor.getString(7);
            paidServiceEntry.serviceEndDate = cursor.getString(8);

            cursor.close();
        } else {
            paidServiceEntry = null;
        }
        return paidServiceEntry;
    }

    public ItemPaidService findPaidServieEntryByDeviceId(int deviceId) {
        String query = "Select * FROM " + TABLE_PAID_SERVICE + " WHERE " + COLUMN_PAID_SERVICE_DEVICE_ID + "=" + deviceId;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemPaidService paidServiceEntry = new ItemPaidService();
        if (cursor.moveToFirst()) {
            paidServiceEntry.orderId = cursor.getInt(0);
            paidServiceEntry.deviceId = cursor.getInt(1);
            paidServiceEntry.type = cursor.getInt(2);
            paidServiceEntry.userPhone = cursor.getString(3);
            paidServiceEntry.amount = cursor.getInt(4);
            paidServiceEntry.payType = cursor.getInt(5);
            paidServiceEntry.serviceYears = cursor.getInt(6);
            paidServiceEntry.serviceStartDate = cursor.getString(7);
            paidServiceEntry.serviceEndDate = cursor.getString(8);

            cursor.close();
        } else {
            paidServiceEntry = null;
        }
        return paidServiceEntry;
    }

    public ItemFence findFenceEntry(int id) {
        String query = "Select * FROM " + TABLE_FENCE + " WHERE " + COLUMN_FENCE_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemFence itemFence = new ItemFence();
        if (cursor.moveToFirst()) {
            itemFence.id = cursor.getInt(0);
            itemFence.name = cursor.getString(1);
            itemFence.address = cursor.getString(2);
            itemFence.lat = cursor.getString(3);
            itemFence.lon = cursor.getString(4);
            itemFence.radius = cursor.getInt(5);
            itemFence.strGuardTimeList = cursor.getString(6);

            cursor.close();
        } else {
            itemFence = null;
        }
        return itemFence;
    }

    public ArrayList<ItemHeartRate> getHeartRateEntries(int watchId) {
        ArrayList<ItemHeartRate> entryList = new ArrayList<ItemHeartRate>();
        String query = "Select * FROM " + TABLE_HEART_RATE + " WHERE " + COLUMN_HEART_RATE_WATCHID + "=" + watchId;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemHeartRate heartRateEntry = new ItemHeartRate();
                heartRateEntry.watchId = cursor.getInt(0);
                heartRateEntry.checkDate = cursor.getString(1);
                heartRateEntry.checkTime = cursor.getLong(2);
                heartRateEntry.heartRate = cursor.getInt(3);

                entryList.add(heartRateEntry);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public int getMaxHeartRate(int watchId, String strDate) {
        String query = "Select MAX(" + COLUMN_HEART_RATE +") AS max_heart_rate FROM " + TABLE_HEART_RATE + " WHERE " + COLUMN_HEART_RATE_WATCHID + "=" + watchId + " AND " + COLUMN_HEART_RATE_DATE + "='" + strDate + "'";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int maxId = cursor.getInt(cursor.getColumnIndex("max_heart_rate"));
            return maxId;
        }

        return 0;
    }

    public int getMinHeartRate(int watchId, String strDate) {
        String query = "Select MIN(" + COLUMN_HEART_RATE +") AS min_heart_rate FROM " + TABLE_HEART_RATE + " WHERE " + COLUMN_HEART_RATE_WATCHID + "=" + watchId + " AND " + COLUMN_HEART_RATE_DATE + "='" + strDate + "'";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int minId = cursor.getInt(cursor.getColumnIndex("min_heart_rate"));
            return minId;
        }

        return 0;
    }

    public int getRecentHeartRate(int watchId) {
        String query = "Select MAX(" + COLUMN_HEART_RATE_TIME +") AS recent_time," + COLUMN_HEART_RATE + " FROM " + TABLE_HEART_RATE + " WHERE " + COLUMN_HEART_RATE_WATCHID + "=" + watchId;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int recentHeartRate = cursor.getInt(cursor.getColumnIndex(COLUMN_HEART_RATE));
            return recentHeartRate;
        }

        return 0;
    }

    public ArrayList<ItemHeartRate> getDayHeartRateEntries(int watchId, String strDate) {
        ArrayList<ItemHeartRate> entryList = new ArrayList<ItemHeartRate>();
        String query = "Select * FROM " + TABLE_HEART_RATE + " WHERE " + COLUMN_HEART_RATE_WATCHID + "=" + watchId + " AND " + COLUMN_HEART_RATE_DATE + "='" + strDate + "' ORDER BY " + COLUMN_HEART_RATE_TIME;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemHeartRate heartRateEntry = new ItemHeartRate();
                heartRateEntry.watchId = cursor.getInt(0);
                heartRateEntry.checkDate = cursor.getString(1);
                heartRateEntry.checkTime = cursor.getLong(2);
                heartRateEntry.heartRate = cursor.getInt(3);

                entryList.add(heartRateEntry);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public List<ItemNews> getAllRecommendedEntries() {
        List<ItemNews> entryList = new ArrayList<ItemNews>();
        // Select all query
        String query = "Select * FROM " + TABLE_RECOMMENDED;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemNews recommendedEntry = new ItemNews();
                recommendedEntry.id = cursor.getInt(0);
                recommendedEntry.createTime = cursor.getString(1);
                recommendedEntry.updateTime = cursor.getString(2);
                recommendedEntry.releaseTime = cursor.getString(3);
                recommendedEntry.status = cursor.getString(4);
                recommendedEntry.title = cursor.getString(5);
                recommendedEntry.content = cursor.getString(6);
                recommendedEntry.picture = cursor.getString(7);
                recommendedEntry.newsType = cursor.getString(8);
                recommendedEntry.publishTo = cursor.getString(9);
                recommendedEntry.newsBranch = cursor.getString(10);
                recommendedEntry.readCnt = cursor.getInt(11);

                entryList.add(recommendedEntry);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public List<ItemNews> getAllKnowledgeEntries() {
        List<ItemNews> entryList = new ArrayList<ItemNews>();
        // Select all query
        String query = "Select * FROM " + TABLE_KNOWLEDGE;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemNews knowledgeEntry = new ItemNews();
                knowledgeEntry.id = cursor.getInt(0);
                knowledgeEntry.createTime = cursor.getString(1);
                knowledgeEntry.updateTime = cursor.getString(2);
                knowledgeEntry.releaseTime = cursor.getString(3);
                knowledgeEntry.status = cursor.getString(4);
                knowledgeEntry.title = cursor.getString(5);
                knowledgeEntry.content = cursor.getString(6);
                knowledgeEntry.picture = cursor.getString(7);
                knowledgeEntry.newsType = cursor.getString(8);
                knowledgeEntry.publishTo = cursor.getString(9);
                knowledgeEntry.newsBranch = cursor.getString(10);
                knowledgeEntry.readCnt = cursor.getInt(11);

                entryList.add(knowledgeEntry);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public ArrayList<ItemSensorInfo> getAllSensorEntries() {
        ArrayList<ItemSensorInfo> entryList = new ArrayList<ItemSensorInfo>();
        // Select all query
        String query = "Select * FROM " + TABLE_SENSOR;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemSensorInfo itemSensorInfo = new ItemSensorInfo();
                itemSensorInfo.id = cursor.getInt(0);
                itemSensorInfo.serial = cursor.getString(1);
                itemSensorInfo.isManager = (cursor.getInt(2) == 1) ? true : false;
                itemSensorInfo.serviceStartDate = cursor.getString(3);
                itemSensorInfo.serviceEndDate = cursor.getString(4);
                itemSensorInfo.netStatus = (cursor.getInt(5) == 1) ? true : false;
                itemSensorInfo.sensorType = cursor.getString(6);
                itemSensorInfo.contactName = cursor.getString(7);
                itemSensorInfo.contactPhone = cursor.getString(8);
                itemSensorInfo.locationLabel = cursor.getString(9);
                itemSensorInfo.lat = cursor.getString(10);
                itemSensorInfo.lon = cursor.getString(11);
                itemSensorInfo.province = cursor.getString(12);
                itemSensorInfo.city = cursor.getString(13);
                itemSensorInfo.district = cursor.getString(14);
                itemSensorInfo.address = cursor.getString(15);
                itemSensorInfo.batteryStatus = (cursor.getInt(16) == 1) ? true : false;
                itemSensorInfo.alarmStatus = (cursor.getInt(17) == 1) ? true : false;

                entryList.add(itemSensorInfo);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public ArrayList<ItemWatchInfo> getAllWatchEntries() {
        ArrayList<ItemWatchInfo> entryList = new ArrayList<ItemWatchInfo>();
        // Select all query
        String query = "Select * FROM " + TABLE_WATCH;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemWatchInfo itemWatchInfo = new ItemWatchInfo();
                itemWatchInfo.id = cursor.getInt(0);
                itemWatchInfo.serial = cursor.getString(1);
                itemWatchInfo.isManager = (cursor.getInt(2) == 1) ? true : false;
                itemWatchInfo.serviceStartDate = cursor.getString(3);
                itemWatchInfo.serviceEndDate = cursor.getString(4);
                itemWatchInfo.netStatus = (cursor.getInt(5) == 1) ? true : false;
                itemWatchInfo.name = cursor.getString(6);
                itemWatchInfo.phone = cursor.getString(7);
                itemWatchInfo.sex = cursor.getInt(8);
                itemWatchInfo.birthday = cursor.getString(9);
                itemWatchInfo.tall = cursor.getInt(10);
                itemWatchInfo.weight = cursor.getInt(11);
                itemWatchInfo.blood = cursor.getString(12);
                itemWatchInfo.ill_history = cursor.getString(13);
                itemWatchInfo.lat = cursor.getString(14);
                itemWatchInfo.lon = cursor.getString(15);
                itemWatchInfo.province = cursor.getString(16);
                itemWatchInfo.city = cursor.getString(17);
                itemWatchInfo.district = cursor.getString(18);
                itemWatchInfo.address = cursor.getString(19);
                itemWatchInfo.chargeStatus = cursor.getInt(20);
                itemWatchInfo.sosContactName1 = cursor.getString(21);
                itemWatchInfo.sosContactPhone1 = cursor.getString(22);
                itemWatchInfo.sosContactName2 = cursor.getString(23);
                itemWatchInfo.sosContactPhone2 = cursor.getString(24);
                itemWatchInfo.sosContactName3 = cursor.getString(25);
                itemWatchInfo.sosContactPhone3 = cursor.getString(26);
                itemWatchInfo.highRate = cursor.getInt(27);
                itemWatchInfo.lowRate = cursor.getInt(28);
                itemWatchInfo.posUpdateMode = cursor.getInt(29);

                entryList.add(itemWatchInfo);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public ArrayList<ItemNotification> getAllNotificationEntries() {
        ArrayList<ItemNotification> entryList = new ArrayList<>();
        // Select all query
        String query = "Select * FROM " + TABLE_NOTIFICATION  + " ORDER BY " + COLUMN_NOTIFICATION_IS_READ + " DESC, " + COLUMN_NOTIFICATION_TIME + " DESC";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemNotification itemNotification = new ItemNotification();
                itemNotification.id = cursor.getInt(0);
                itemNotification.title = cursor.getString(1);
                itemNotification.content = cursor.getString(2);
                itemNotification.taskId = cursor.getString(3);
                itemNotification.type = cursor.getString(4);
                itemNotification.time = cursor.getLong(5);
                itemNotification.isRead = cursor.getInt(6);
                itemNotification.taskStatus = cursor.getString(7);
                itemNotification.noticeType = cursor.getString(8);

                entryList.add(itemNotification);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public ArrayList<ItemPaidService> getPaidServiceEntries() {
        ArrayList<ItemPaidService> entryList = new ArrayList<>();
        // Select all query
        String query = "Select * FROM " + TABLE_PAID_SERVICE;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemPaidService itemPaidService = new ItemPaidService();
                itemPaidService.orderId = cursor.getInt(0);
                itemPaidService.deviceId = cursor.getInt(1);
                itemPaidService.type = cursor.getInt(2);
                itemPaidService.userPhone = cursor.getString(3);
                itemPaidService.amount = cursor.getInt(4);
                itemPaidService.payType = cursor.getInt(5);
                itemPaidService.serviceYears = cursor.getInt(6);
                itemPaidService.serviceStartDate = cursor.getString(7);
                itemPaidService.serviceEndDate = cursor.getString(8);

                entryList.add(itemPaidService);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public ArrayList<ItemFence> getFenceEntries() {
        ArrayList<ItemFence> entryList = new ArrayList<>();
        // Select all query
        String query = "Select * FROM " + TABLE_FENCE;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                ItemFence itemFence = new ItemFence();
                itemFence.id = cursor.getInt(0);
                itemFence.name = cursor.getString(1);
                itemFence.address = cursor.getString(2);
                itemFence.lat = cursor.getString(3);
                itemFence.lon = cursor.getString(4);
                itemFence.radius = cursor.getInt(5);
                itemFence.strGuardTimeList = cursor.getString(6);

                entryList.add(itemFence);

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return entryList;
    }

    public boolean isNotificationAlarm() {
        String query = "Select * FROM " + TABLE_NOTIFICATION;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(query, null);
            boolean hasNext = cursor.moveToFirst();
            while(hasNext) {
                boolean isRead = (cursor.getInt(6) == 1) ? true : false;;

                if (!isRead) {
                    return true;
                }

                hasNext = cursor.moveToNext();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
}
