package com.chinatsp.drawer.search.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.chinatsp.drawer.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;

public class SearchDB extends SQLiteOpenHelper {
    private static final String TAG = "SearchDB";
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "search.db";
    private static String SEARCH_TABLE = "search";
    public static final String ID = "id";//主键，自增长
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
    public SearchDB(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
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
        db.execSQL(sql_location);
    }

    //判断表是否存在
    public boolean isTableExist() {
        boolean isTableExist=true;
        //sqlite_master是sqlite系统表
        Cursor c= db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name= "
                + "'" + SEARCH_TABLE +"'", null);
        if(null != c && c.moveToFirst()){
            if (c.getInt(0)==0) {
                isTableExist=false;
            }
        }
        c.close();
        return isTableExist;
    }

    /*
     *  统计Location表的总条数
     */
    public int countLocation(){
        int count = 0;
        if(isTableExist()){
            String sql = "select count(*) from " + SEARCH_TABLE;
            Cursor cursor = db.rawQuery(sql,null);
            if(null != cursor && cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /*
     *获取数据
     */
    public List<SearchBean> getData(){
       List<SearchBean> data = new ArrayList<>();
        List<SearchBean> lists = new ArrayList<>();
        try{
            String sql =  "select distinct * from " + SEARCH_TABLE ;
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            if(null != cursor && cursor.moveToFirst()) {
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
        }catch (Exception e){
            Log.d(TAG,"read db exception");
            deleteLocation();//删除数据库
            //将读取的数据库中的数据打印
            for(SearchBean searchBean : lists){
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
    public List<SearchBean> getData1(String str){
        String s = "";
        for(int i =0; i <str.length();i++){
            if(i == str.length() -1){
                s += str.charAt(i);
            }else {
                s += str.charAt(i) + "%";
            }
        }
        List<SearchBean> data = new ArrayList<>();
        List<SearchBean> lists = new ArrayList<>();
        try{
            String sql =  "select distinct * from " + SEARCH_TABLE + " where "+CHINESEFUNCTION+"  like '%" + s + "%'";

            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            if(null != cursor && cursor.moveToFirst()) {
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

        }catch (Exception e){
            Log.d(TAG,"read db exception");
            deleteLocation();//删除数据库
            //将读取的数据库中的数据打印
            for(SearchBean searchBean : lists){
                searchBean.printLog();
            }
            lists.clear();
            data.clear();
        }

        return data;
    }

    public void  insertSearch(SearchBean searchBean){
        if(searchBean == null){
            return;
        }
        String sql = "INSERT into " + SEARCH_TABLE + "(" +
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
        db.execSQL(sql,new Object[]{searchBean.getModelName(),searchBean.getChineseFunction(),searchBean.getChineseFunctionLevel(),
                searchBean.getEnglishFunction(),searchBean.getEnglishFunctionLevel(),searchBean.getIntentAction(), searchBean.getIntentInterface(),
                searchBean.getDataVersion(),searchBean.getCarVersion()});
    }

    /*
     *  删除Location表的所有记录
     */
    public void deleteLocation(){
        String sql = "delete from " + SEARCH_TABLE;
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
