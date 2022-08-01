package com.chinatsp.apppanel.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.chinatsp.apppanel.bean.LocationBean;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAppDB extends SQLiteOpenHelper {
    private static final String TAG = "MyAppDB";
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "myapp.db";
    private static String LOCATION_TABLE = "location";
    //public static final String ID = "_id";//主键，PK，自增长
    public static final String PARENTINDEX = "parent_index";//父位置
    public static final String CHILDINDEX = "child_index";//子位置
    public static final String TITLE = "title";//名称
    public static final String PACKAGENAMELOCATION = "package_name";//包名
    public static final String IMAGE = "image";//应用图标
    public static final String NAME = "name";//应用名
    public static final String ADDBTN = "addbtn";//是否是添加按钮 0否，1是
    public static final String STATUS = "status";//状态 0默认，1下载中，2安装中，3继续，4下载失败
    public static final String PRIORITY = "priority";//优先级
    public static final String INSTALLED = "installed";//是否已安装 0未安装，1已安装
    public static final String CANUNINSTALLED = "canuninstalled";//是否可卸载 0不支持，1支持
    public static final String RESERVE1 = "reserve1";//预留字段1
    public static final String RESERVE2 = "reserve2";//预留字段2
    public static final String RESERVE3 = "reserve3";//预留字段3
    public static final String RESERVE4 = "reserve4";//预留字段4
    public static final String RESERVE5 = "reserve5";//预留字段5
    public static final String RESERVE6 = "reserve6";//预留字段6

    private static String INFO_TABLE = "info";
    public static final String PACKAGENAMEINFO = "package_name";//包名，主键，PK
//    public static final String IMAGE = "image";//应用图标
//    public static final String NAME = "name";//应用名
    public static final String REMAIN1 = "remain1";//预留字段1
    public static final String REMAIN2 = "remain2";//预留字段2
    public static final String REMAIN3 = "remain3";//预留字段3
    public static final String REMAIN4 = "remain4";//预留字段4
    public static final String REMAIN5 = "remain5";//预留字段5
    public static final String REMAIN6 = "remain6";//预留字段6

    private SQLiteDatabase db;
    public MyAppDB(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_location = "CREATE TABLE " + LOCATION_TABLE + "(" +
                //ID + " INTEGER PRIMARY KEY autoincrement," +
                PARENTINDEX + " INTEGER," +
                CHILDINDEX + " INTEGER," +
                TITLE + " text," +
                PACKAGENAMELOCATION + " text," +
                IMAGE + " BLOB," +
                NAME + " text," +
                ADDBTN + " INTEGER," +
                STATUS + " INTEGER," +
                PRIORITY + " INTEGER," +
                INSTALLED + " INTEGER," +
                CANUNINSTALLED + " INTEGER," +
                RESERVE1 + " text," +
                RESERVE2 + " text," +
                RESERVE3 + " text," +
                RESERVE4 + " text," +
                RESERVE5 + " text," +
                RESERVE6 + " text" +
                ")";
//        String sql_info = "CREATE TABLE " + INFO_TABLE + "(" +
//                PACKAGENAMEINFO + " text not null PRIMARY KEY," +
//                IMAGE + " BLOB," +
//                NAME + " text," +
//                REMAIN1 + " text," +
//                REMAIN2 + " text," +
//                REMAIN3 + " text," +
//                REMAIN4 + " text," +
//                REMAIN5 + " text," +
//                REMAIN6 + " text" +
//                ")";
//        db.execSQL(sql_info);
        db.execSQL(sql_location);
    }

    //判断表是否存在
    private boolean isTableExist() {
        boolean isTableExist=true;
        //sqlite_master是sqlite系统表
        Cursor c= db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name= "
                + "'" + LOCATION_TABLE +"'", null);
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
            String sql = "select count(*) from " + LOCATION_TABLE;
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
    public List<List<LocationBean>> getData1(){
        List<List<LocationBean>> data = new ArrayList<>();
        try{
            List<LocationBean> lists = new ArrayList<>();
            //select * from (select * from location order by child_index asc) order by parent_index asc
            String sql = "select * from " + "(select * from " + LOCATION_TABLE + " order by " +
                    CHILDINDEX + " asc) order by " + PARENTINDEX + " asc";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            if(null != cursor && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    LocationBean locationBean = new LocationBean();
                    locationBean.setParentIndex(cursor.getInt(cursor.getColumnIndex(PARENTINDEX)));
                    locationBean.setChildIndex(cursor.getInt(cursor.getColumnIndex(CHILDINDEX)));
                    locationBean.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                    locationBean.setPackageName(cursor.getString(cursor.getColumnIndex(PACKAGENAMELOCATION)));
                    locationBean.setImgByte(cursor.getBlob(cursor.getColumnIndex(IMAGE)));
                    locationBean.setImgDrawable(null);
                    locationBean.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                    locationBean.setAddBtn(cursor.getInt(cursor.getColumnIndex(ADDBTN)));
                    locationBean.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
                    locationBean.setPriority(cursor.getInt(cursor.getColumnIndex(PRIORITY)));
                    locationBean.setInstalled(cursor.getInt(cursor.getColumnIndex(INSTALLED)));
                    locationBean.setCanuninstalled(cursor.getInt(cursor.getColumnIndex(CANUNINSTALLED)));
                    lists.add(locationBean);
                    cursor.moveToNext();
                }
            }
            cursor.close();

            int lastParentIndex = -1;
            List<LocationBean> inner = null;
            LocationBean locationBean;
            int parentIndex = -1;
            for(int i = 0; i < lists.size(); i++){
                locationBean = lists.get(i);
                parentIndex = locationBean.getParentIndex();
                if(parentIndex == lastParentIndex){//parentIndex == lastParentIndex说明是同一个文件夹下的应用
                    if(locationBean.getChildIndex() == 0){
                        inner = new ArrayList<>();
                    }
                    inner.add(locationBean);
                    lastParentIndex = parentIndex;
                    if(i == lists.size() - 1){//最后一个
                        data.add(inner);
                    }
                }else {
                    //parentIndex != lastParentIndex说明是一个新的文件夹或应用
                    if(inner != null) {
                        data.add(inner);
                        inner = null;
                    }
                    if(locationBean.getChildIndex() == -1){//如果childIndex=-1说明不是文件夹
                        List<LocationBean> inner2 = new ArrayList<>();
                        inner2.add(locationBean);
                        data.add(inner2);
                    }else {
                        if(locationBean.getChildIndex() == 0){//如果childIndex=0说明是文件夹
                            inner = new ArrayList<>();
                        }
                        inner.add(locationBean);
                    }
                    lastParentIndex = parentIndex;
                }
            }
        }catch (Exception e){
            Log.d(TAG,"read db exception");
            data.clear();
        }

        return data;
    }

    /*
    * 获取所有的标题信息
     */
    public List<String> getAllTitles(){
        List<String> titleLists = new ArrayList<>();
        //select distinct title from location where title like '文件夹%' order by title asc
        String sql = "select distinct " + TITLE + " from " + LOCATION_TABLE +
                " where " + TITLE + " like '文件夹%' order by " + TITLE + " asc";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToFirst();
        if(null != cursor && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                titleLists.add(cursor.getString(cursor.getColumnIndex(TITLE)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return titleLists;
    }

    /*
    * 获取最大的parentIndex值
     */
    public int getMaxParentIndex(){
        int num = -1;
        if(!isTableExist()){
            num = -1;
        }else {
            String sql = "select max(" + PARENTINDEX + ") from " + LOCATION_TABLE;
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            if(cursor != null){
                num = cursor.getInt(0);
                cursor.close();
            }else {
                num = -1;
            }
        }
        return num;
    }

    public void insertLocation(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        if(locationBean.getImgDrawable() != null){
            Drawable drawable = locationBean.getImgDrawable();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            locationBean.setImgByte(baos.toByteArray());
        }else if(locationBean.getImgByte() == null){
            return;
        }

        String sql = "INSERT into " + LOCATION_TABLE + "(" +
                PARENTINDEX + "," +
                CHILDINDEX + "," +
                TITLE + "," +
                PACKAGENAMELOCATION + "," +
                IMAGE + "," +
                NAME + "," +
                ADDBTN + "," +
                STATUS + "," +
                PRIORITY + "," +
                INSTALLED + "," +
                CANUNINSTALLED + "," +
                RESERVE1 + "," +
                RESERVE2 + "," +
                RESERVE3 + "," +
                RESERVE4 + "," +
                RESERVE5 + "," +
                RESERVE6 + ")" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(sql,new Object[]{locationBean.getParentIndex(),locationBean.getChildIndex(),locationBean.getTitle(),
        locationBean.getPackageName(),locationBean.getImgByte(),locationBean.getName(), locationBean.getAddBtn(),
        locationBean.getStatus(),locationBean.getPriority(), locationBean.getInstalled(),
        locationBean.getCanuninstalled(),"","","","","",""});
    }

    public void updateLocation(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        if(locationBean.getImgByte() == null){
            Drawable drawable = locationBean.getImgDrawable();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            locationBean.setImgByte(baos.toByteArray());
        }
        String sql = "update " + LOCATION_TABLE + " set " +
                PARENTINDEX + " = ?," +
                CHILDINDEX + " = ?," +
                TITLE + " = ?," +
                IMAGE + " = ?," +
                NAME + " = ?," +
                ADDBTN + " = ?," +
                STATUS + " = ?," +
                PRIORITY + " = ?," +
                INSTALLED + " = ?," +
                CANUNINSTALLED + " = ?," +
                RESERVE1 + " = ?," +
                RESERVE2 + " = ?," +
                RESERVE3 + " = ?," +
                RESERVE4 + " = ?," +
                RESERVE5 + " = ?," +
                RESERVE6 + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getParentIndex(),locationBean.getChildIndex(),locationBean.getTitle(),
                locationBean.getImgByte(),locationBean.getName(),locationBean.getAddBtn(),locationBean.getStatus(),
                locationBean.getPriority(),locationBean.getInstalled(),locationBean.getCanuninstalled(),"","","","","","",
                locationBean.getPackageName()});
//        Log.d("mysql","parentIndex = " + locationBean.getParentIndex() + ",childIndex = " +
//                locationBean.getChildIndex() + ",package = " + locationBean.getPackageName());
    }

    public void deleteLocation(String packageName){
        String sql = "delete from " + LOCATION_TABLE + " where " + PACKAGENAMELOCATION + " = '" + packageName + "'";
        db.execSQL(sql);
    }

    public void updateTitle(LocationBean locationBean){
        String sql = "update " + LOCATION_TABLE + " set " +
                TITLE + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getTitle(),locationBean.getPackageName()});
    }

    public void updateIndex(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + LOCATION_TABLE + " set " +
                PARENTINDEX + " = ?," +
                CHILDINDEX + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getParentIndex(),locationBean.getChildIndex(),locationBean.getPackageName()});
    }

    /*
    *返回的非0，则说明存在此应用，否则说明不存在此应用
     */
    public int isExistPackage(String packageName){
        int num = 0;
        if(!isTableExist()){
            num = 0;
        }else {
            String sql = "select count(*) from " + LOCATION_TABLE + " where " + PACKAGENAMELOCATION + " = '" + packageName + "'";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            if(cursor != null){
                num = cursor.getInt(0);
                cursor.close();
            }else {
                num = 0;
            }
        }
        return num;
    }

    /*
    *  删除Location表的所有记录
     */
    public void deleteLocation(){
        String sql = "delete from " + LOCATION_TABLE;
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
