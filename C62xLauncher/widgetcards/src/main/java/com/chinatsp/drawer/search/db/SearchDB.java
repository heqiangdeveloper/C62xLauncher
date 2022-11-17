package com.chinatsp.drawer.search.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chinatsp.drawer.bean.SearchBean;
import com.chinatsp.drawer.bean.SearchHistoricalBean;
import com.chinatsp.drawer.search.utils.FileUtils;


import java.util.ArrayList;
import java.util.List;

public class SearchDB extends SQLiteOpenHelper {
    private static final String TAG = "SearchDB";
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "search.db";
    private static String SEARCH_TABLE = "search";
    private static String SEARCH_TABLE_ENGLISH = "search_english";
    private static String SEARCH_HISTORICAL_TABLE = "historical";
    public static final String ID = "id";//主键，自增长
    public static final String CONTENT = "content";//搜索内容
    public static final String MODELNAME = "model_name";//模块名
    public static final String CHINESEFUNCTION = "chinese_function";//中文功能名
    public static final String CHINESEFUNCTIONLEVEL = "chinese_function_level";//中文功能层次名
    public static final String ENGLISHFUNCTION = "english_function";//英文功能名
    public static final String ENGLISHFUNCTIONLEVEL = "english_function_level";//英文功能层次名
    public static final String INTENTACTION = "intent_action";//跳转action
    public static final String INTENTINTERFACE = "intent_interface";//跳转接口
    public static final String DATAVERSION = "data_version";//数据版本号
    public static final String CARVERSION = "car_version";//车型号
    private SQLiteDatabase db;

    public SearchDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_location = "CREATE TABLE " + SEARCH_TABLE + "(" +
                //ID + " INTEGER PRIMARY KEY autoincrement," +
                MODELNAME + " text," +
                CHINESEFUNCTION + " text," +
                CHINESEFUNCTIONLEVEL + " text," +
                ENGLISHFUNCTION + " text," +
                ENGLISHFUNCTIONLEVEL + " text," +
                INTENTACTION + " text," +
                INTENTINTERFACE + " text," +
                DATAVERSION + " text," +
                CARVERSION + " text" +
                ")";
        String sql_search_english = "CREATE TABLE " + SEARCH_TABLE_ENGLISH + "(" +
                //ID + " INTEGER PRIMARY KEY autoincrement," +
                MODELNAME + " text," +
                CHINESEFUNCTION + " text," +
                CHINESEFUNCTIONLEVEL + " text," +
                ENGLISHFUNCTION + " text," +
                ENGLISHFUNCTIONLEVEL + " text," +
                INTENTACTION + " text," +
                INTENTINTERFACE + " text," +
                DATAVERSION + " text," +
                CARVERSION + " text" +
                ")";
        String sql_historical = "CREATE TABLE " + SEARCH_HISTORICAL_TABLE + "(" +
                CONTENT + " text" +
                ")";
        db.execSQL(sql_historical);
        db.execSQL(sql_location);
        db.execSQL(sql_search_english);
    }

    public void createSearchTable() {
        if (FileUtils.getLanguage() == 1) {
            String sql_location = "CREATE TABLE " + SEARCH_TABLE + "(" +
                    //ID + " INTEGER PRIMARY KEY autoincrement," +
                    MODELNAME + " text," +
                    CHINESEFUNCTION + " text," +
                    CHINESEFUNCTIONLEVEL + " text," +
                    ENGLISHFUNCTION + " text," +
                    ENGLISHFUNCTIONLEVEL + " text," +
                    INTENTACTION + " text," +
                    INTENTINTERFACE + " text," +
                    DATAVERSION + " text," +
                    CARVERSION + " text" +
                    ")";
            db.execSQL(sql_location);
        } else {
            String sql_search_english = "CREATE TABLE " + SEARCH_TABLE_ENGLISH + "(" +
                    //ID + " INTEGER PRIMARY KEY autoincrement," +
                    MODELNAME + " text," +
                    CHINESEFUNCTION + " text," +
                    CHINESEFUNCTIONLEVEL + " text," +
                    ENGLISHFUNCTION + " text," +
                    ENGLISHFUNCTIONLEVEL + " text," +
                    INTENTACTION + " text," +
                    INTENTINTERFACE + " text," +
                    DATAVERSION + " text," +
                    CARVERSION + " text" +
                    ")";
            db.execSQL(sql_search_english);
        }
    }

    //判断表是否存在
    public boolean isTableExist() {
        boolean isTableExist = true;
        String table;
        if (FileUtils.getLanguage() == 1) {
            table = SEARCH_TABLE;
        } else {
            table = SEARCH_TABLE_ENGLISH;
        }
        //sqlite_master是sqlite系统表
        Cursor c = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name= "
                + "'" + table + "'", null);
        if (null != c && c.moveToFirst()) {
            if (c.getInt(0) == 0) {
                isTableExist = false;
            }
        }
        c.close();
        return isTableExist;
    }

    //判断表是否存在
    public boolean isTableHistoricalExist() {
        boolean isTableExist = true;
        //sqlite_master是sqlite系统表
        Cursor c = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name= "
                + "'" + SEARCH_HISTORICAL_TABLE + "'", null);
        if (null != c && c.moveToFirst()) {
            if (c.getInt(0) == 0) {
                isTableExist = false;
            }
        }
        c.close();
        return isTableExist;
    }

    /*
     *  统计Location表的总条数
     */
    public int countLocation() {
        int count = 0;
        String table;
        if (FileUtils.getLanguage() == 1) {
            table = SEARCH_TABLE;
        } else {
            table = SEARCH_TABLE_ENGLISH;
        }
        if (isTableExist()) {
            String sql = "select count(*) from " + table;
            Cursor cursor = db.rawQuery(sql, null);
            if (null != cursor && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /*
     *获取数据
     */
    public List<SearchBean> getData() {
        String table;
        if (FileUtils.getLanguage() == 1) {
            table = SEARCH_TABLE;
        } else {
            table = SEARCH_TABLE_ENGLISH;
        }
        List<SearchBean> data = new ArrayList<>();
        List<SearchBean> lists = new ArrayList<>();
        try {
            String sql = "select distinct * from " + table;
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            if (null != cursor && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    SearchBean searchBean = new SearchBean();
                    searchBean.setModelName(cursor.getString(cursor.getColumnIndex(MODELNAME)));
                    searchBean.setChineseFunction(cursor.getString(cursor.getColumnIndex(CHINESEFUNCTION)));
                    searchBean.setChineseFunctionLevel(cursor.getString(cursor.getColumnIndex(CHINESEFUNCTIONLEVEL)));
                    searchBean.setEnglishFunction(cursor.getString(cursor.getColumnIndex(ENGLISHFUNCTION)));
                    searchBean.setEnglishFunctionLevel(cursor.getString(cursor.getColumnIndex(ENGLISHFUNCTIONLEVEL)));
                    searchBean.setIntentAction(cursor.getString(cursor.getColumnIndex(INTENTACTION)));
                    searchBean.setIntentInterface(cursor.getString(cursor.getColumnIndex(INTENTINTERFACE)));
                    searchBean.setDataVersion(cursor.getString(cursor.getColumnIndex(DATAVERSION)));
                    searchBean.setCarVersion(cursor.getString(cursor.getColumnIndex(CARVERSION)));
                    cursor.moveToNext();
                    data.add(searchBean);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, "read db exception");
            deleteLocation();//删除数据库
            //将读取的数据库中的数据打印
            for (SearchBean searchBean : lists) {
                searchBean.printLog();
            }
            lists.clear();
            data.clear();
        }

        return data;
    }

    /*
     *获取模糊搜索数据
     */
    public List<SearchBean> getData1(String str) {
        String table;
        String language;
        List<SearchBean> data = new ArrayList<>();
        if (str.contains("%")) {
            return data;
        }
        if (FileUtils.getLanguage() == 1) {
            table = SEARCH_TABLE;
            language = CHINESEFUNCTION;
        } else {
            table = SEARCH_TABLE_ENGLISH;
            language = ENGLISHFUNCTION;
        }
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            if (i == str.length() - 1) {
                s += str.charAt(i);
            } else {
                s += str.charAt(i) + "%";
            }
        }
        List<SearchBean> lists = new ArrayList<>();
        try {
            String sql = "select distinct * from " + table + " where " + language + "  like '%\\" + s + "%' escape \'\\\'";
            Log.d(TAG, "getData1 SQL "+sql);
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            if (null != cursor && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    SearchBean searchBean = new SearchBean();
                    searchBean.setModelName(cursor.getString(cursor.getColumnIndex(MODELNAME)));
                    searchBean.setChineseFunction(cursor.getString(cursor.getColumnIndex(CHINESEFUNCTION)));
                    searchBean.setChineseFunctionLevel(cursor.getString(cursor.getColumnIndex(CHINESEFUNCTIONLEVEL)));
                    searchBean.setEnglishFunction(cursor.getString(cursor.getColumnIndex(ENGLISHFUNCTION)));
                    searchBean.setEnglishFunctionLevel(cursor.getString(cursor.getColumnIndex(ENGLISHFUNCTIONLEVEL)));
                    searchBean.setIntentAction(cursor.getString(cursor.getColumnIndex(INTENTACTION)));
                    searchBean.setIntentInterface(cursor.getString(cursor.getColumnIndex(INTENTINTERFACE)));
                    searchBean.setDataVersion(cursor.getString(cursor.getColumnIndex(DATAVERSION)));
                    searchBean.setCarVersion(cursor.getString(cursor.getColumnIndex(CARVERSION)));
                    cursor.moveToNext();
                    data.add(searchBean);
                }
            }
            cursor.close();

        } catch (Exception e) {
            Log.d(TAG, "read db exception");
            deleteLocation();//删除数据库
            //将读取的数据库中的数据打印
            for (SearchBean searchBean : lists) {
                searchBean.printLog();
            }
            lists.clear();
            data.clear();
        }

        return data;
    }

    /*
     *获取历史搜索数据
     */
    public List<SearchHistoricalBean> getHistoricalData() {
        List<SearchHistoricalBean> data = new ArrayList<>();
        List<SearchHistoricalBean> lists = new ArrayList<>();
        try {
            String sql = "select distinct * from " + SEARCH_HISTORICAL_TABLE;
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            if (null != cursor && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    SearchHistoricalBean searchBean = new SearchHistoricalBean();
                    searchBean.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
                    cursor.moveToNext();
                    data.add(searchBean);
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, "read db exception");
        }

        return data;
    }


    public synchronized void insertSearch(SearchBean searchBean) {
        if (searchBean == null) {
            return;
        }
        //判断download表是否存在
        if (!isTableExist()) {
            createSearchTable();
        }
        String table;
        if (FileUtils.getLanguage() == 1) {
            table = SEARCH_TABLE;
        } else {
            table = SEARCH_TABLE_ENGLISH;
        }
        try {
            String sql = "INSERT into " + table + "(" +
                    MODELNAME + "," +
                    CHINESEFUNCTION + "," +
                    CHINESEFUNCTIONLEVEL + "," +
                    ENGLISHFUNCTION + "," +
                    ENGLISHFUNCTIONLEVEL + "," +
                    INTENTACTION + "," +
                    INTENTINTERFACE + "," +
                    DATAVERSION + "," +
                    CARVERSION + ")" +
                    " values(?,?,?,?,?,?,?,?,?)";
            db.execSQL(sql, new Object[]{searchBean.getModelName(), searchBean.getChineseFunction(), searchBean.getChineseFunctionLevel(),
                    searchBean.getEnglishFunction(), searchBean.getEnglishFunctionLevel(), searchBean.getIntentAction(), searchBean.getIntentInterface(),
                    searchBean.getDataVersion(), searchBean.getCarVersion()});
        } catch (Exception e) {
            Log.d(TAG, "SEARCH insert db exception: " + e);
        }
    }

    /**
     * 新增搜索历史记录
     *
     * @param bean
     */
    public synchronized void insertSearchHistorical(String bean) {
        if (bean == null) {
            return;
        }
        try {
            if (!isTableHistoricalExist()) {
                String sql_historical = "CREATE TABLE " + SEARCH_HISTORICAL_TABLE + "(" +
                        CONTENT + " text" +
                        ")";
                db.execSQL(sql_historical);
            }
            String sql = "INSERT into " + SEARCH_HISTORICAL_TABLE + "(" +
                    CONTENT + ")" +
                    " values(?)";
            db.execSQL(sql, new Object[]{bean});
        } catch (Exception e) {
            Log.d(TAG, "historical db exception");
        }
    }

    /*
     *  删除Location表的所有记录
     */
    public synchronized void deleteLocation() {
        String table;
        if (FileUtils.getLanguage() == 1) {
            table = SEARCH_TABLE;
        } else {
            table = SEARCH_TABLE_ENGLISH;
        }
        String sql = "delete from " + table;
        db.execSQL(sql);
    }


    /**
     * 删除历史搜索表
     */
    public synchronized void deleteHistorical() {
        String sql = "delete from " + SEARCH_HISTORICAL_TABLE;
        db.execSQL(sql);
    }

    /**
     * 删除单个记录
     *
     * @param content
     */
    public synchronized void deleteCountHistorical(String content) {
        String sql = "delete from " + SEARCH_HISTORICAL_TABLE + " where " + CONTENT + " = '" + content + "'";
        db.execSQL(sql);
    }

    /*
     *  删除历史搜索表第一条
     */
    public synchronized void deleteLocation1() {
        String sql = "delete from " + SEARCH_HISTORICAL_TABLE + " where " + CONTENT + " = (select " + CONTENT + " from " + SEARCH_HISTORICAL_TABLE + " limit 1)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
