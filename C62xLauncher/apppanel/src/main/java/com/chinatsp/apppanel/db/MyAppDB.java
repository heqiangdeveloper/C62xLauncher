package com.chinatsp.apppanel.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.anarchy.classifyview.Bean.LocationBean;
import com.chinatsp.apppanel.AppConfigs.Constant;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAppDB extends SQLiteOpenHelper {
    private static final String TAG = "MyAppDB";
    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "myapp.db";
    private static String LOCATION_TABLE = "location";
    private static String DOWNLOAD_TABLE = "download";
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
    public static final String RESERVE1 = "reserve1";//应用的版本号
    public static final String RESERVE2 = "reserve2";//不再提醒时的版本号
    public static final String RESERVE3 = "reserve3";//有更新推送的版本号
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
        String sql_download = "CREATE TABLE " + DOWNLOAD_TABLE + "(" +
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
        db.execSQL(sql_location);
        db.execSQL(sql_download);
    }

    //创建download表
    private void createDownloadTable(){
        String sql_download = "CREATE TABLE " + DOWNLOAD_TABLE + "(" +
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
        db.execSQL(sql_download);
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


    //判断下载表是否存在
    private boolean isDownloadTableExist() {
        boolean isTableExist=true;
        //sqlite_master是sqlite系统表
        Cursor c= db.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name= "
                + "'" + DOWNLOAD_TABLE +"'", null);
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
        List<LocationBean> lists = new ArrayList<>();
        try{
            //select * from (select * from location order by child_index asc) order by parent_index asc
            String sql = "select * from " + "(select * from " + LOCATION_TABLE +
                    " order by " + CHILDINDEX + " asc) order by " + PARENTINDEX + " asc";
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
            deleteLocation();//删除数据库
            //将读取的数据库中的数据打印
            for(LocationBean locationBean : lists){
                locationBean.printLog();
            }
            lists.clear();
            data.clear();
        }

        return data;
    }

    /*
     *获取应用下载的数据
     */
    public List<LocationBean> getDownloadData(){
        List<LocationBean> lists = new ArrayList<>();
        try{
            //select * from download
            String sql = "select * from " + DOWNLOAD_TABLE;
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
                    locationBean.setReserve1(cursor.getString(cursor.getColumnIndex(RESERVE1)));
                    locationBean.setReserve2(cursor.getString(cursor.getColumnIndex(RESERVE2)));
                    locationBean.setReserve3(cursor.getString(cursor.getColumnIndex(RESERVE3)));
                    lists.add(locationBean);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }catch (Exception e){
            Log.d(TAG,"getDownloadData db exception");
        }
        return lists;
    }

    /*
     *根据包名，获取location表的数据
     */
    public List<LocationBean> getPkgLocationData(String pkgName){
        List<LocationBean> lists = new ArrayList<>();
        try{
            //select * from location where package_name = pkgName
            String sql = "select * from " + LOCATION_TABLE + " where " + PACKAGENAMELOCATION + " = " + pkgName;
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
        }catch (Exception e){
            Log.d(TAG,"getDownloadData db exception");
        }
        return lists;
    }

    /*
     *根据包名，获取应用下载的数据
     */
    public List<LocationBean> getPkgDownloadData(String pkgName){
        List<LocationBean> lists = new ArrayList<>();
        try{
            //select * from download where package_name = pkgName
            String sql = "select * from " + DOWNLOAD_TABLE + " where " + PACKAGENAMELOCATION + " = " + pkgName;
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
        }catch (Exception e){
            Log.d(TAG,"getDownloadData db exception");
        }
        return lists;
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

    public synchronized void insertLocation(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        Drawable drawable = locationBean.getImgDrawable();
        if(drawable != null){
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                locationBean.setImgByte(baos.toByteArray());
                bitmap.recycle();
                bitmap = null;
            }catch (Exception e){
                Log.d(TAG,"Exception: " + e);
                return;
            }finally {
                //释放drawable内存
                drawable.setCallback(null);
                drawable = null;
            }
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
        locationBean.getCanuninstalled(),locationBean.getReserve1(),locationBean.getReserve2(), locationBean.getReserve3(),"","",""});
    }

    /*
    * 向下载表插入记录
     */
    public synchronized void insertDownload(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        //判断download表是否存在
        if(!isDownloadTableExist()){
            createDownloadTable();
        }
        Drawable drawable = locationBean.getImgDrawable();
        if(drawable != null){
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                locationBean.setImgByte(baos.toByteArray());
                bitmap.recycle();
                bitmap = null;
            }catch (Exception e){
                Log.d(TAG,"Exception: " + e);
                return;
            }finally {
                //释放drawable内存
                drawable.setCallback(null);
                drawable = null;
            }
        }else if(locationBean.getImgByte() == null){
            return;
        }

        String sql = "INSERT into " + DOWNLOAD_TABLE + "(" +
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
                locationBean.getCanuninstalled(),locationBean.getReserve1(),locationBean.getReserve2(),
                locationBean.getReserve3(),"","",""});
    }

    public synchronized void updateLocation(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        Drawable drawable = locationBean.getImgDrawable();
        if(drawable != null){
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                locationBean.setImgByte(baos.toByteArray());
                bitmap.recycle();
                bitmap = null;
            }catch (Exception e){
                Log.d(TAG,"Exception: " + e);
                return;
            }finally {
                //释放drawable内存
                drawable.setCallback(null);
                drawable = null;
            }
        }else if(locationBean.getImgByte() == null){
            return;
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
    }

    public synchronized void updateDownloadStatusInLocation(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + LOCATION_TABLE + " set " +
                NAME + " = ?," +
                STATUS + " = ?," +
                INSTALLED + " = ?," +
                RESERVE1 + " = ?," +
                RESERVE2 + " = ?," +
                RESERVE3 + " = ?," +
                RESERVE4 + " = ?," +
                RESERVE5 + " = ?," +
                RESERVE6 + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getName(),locationBean.getStatus(),
                locationBean.getInstalled(), locationBean.getReserve1(),locationBean.getReserve2(),
                locationBean.getReserve3(),"","","",locationBean.getPackageName()});
    }

    //更新下载失败至location表
    public synchronized void updateInstalledInLocation(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + LOCATION_TABLE + " set " +
                INSTALLED + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getInstalled(),locationBean.getPackageName()});
    }

    public synchronized void updateDownloadStatusInDownload(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + DOWNLOAD_TABLE + " set " +
                NAME + " = ?," +
                STATUS + " = ?," +
                INSTALLED + " = ?," +
                RESERVE1 + " = ?," +
                RESERVE2 + " = ?," +
                RESERVE3 + " = ?," +
                RESERVE4 + " = ?," +
                RESERVE5 + " = ?," +
                RESERVE6 + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getName(),locationBean.getStatus(),
                locationBean.getInstalled(),locationBean.getReserve1(),locationBean.getReserve2(),
                locationBean.getReserve3(),"","","",locationBean.getPackageName()});
    }

    /*
    * 更新Download表中失败状态
     */
    public synchronized void updateFailDownloadInDownload(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + DOWNLOAD_TABLE + " set " +
                INSTALLED + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getInstalled(), locationBean.getPackageName()});
    }

    /*
     * 更新Download表中可更新的版本号
     */
    public synchronized void updateAppUpdateInDownload(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + DOWNLOAD_TABLE + " set " +
                INSTALLED + " = ?," +
                RESERVE3 + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getInstalled(), locationBean.getReserve3(),locationBean.getPackageName()});
    }

    public synchronized void deleteLocation(String packageName){
        String sql = "delete from " + LOCATION_TABLE + " where " + PACKAGENAMELOCATION + " = '" + packageName + "'";
        db.execSQL(sql);
    }

    public synchronized void deleteDownload(String packageName){
        String sql = "delete from " + DOWNLOAD_TABLE + " where " + PACKAGENAMELOCATION + " = '" + packageName + "'";
        db.execSQL(sql);
    }

    public synchronized void updateTitle(LocationBean locationBean){
        String sql = "update " + LOCATION_TABLE + " set " +
                TITLE + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getTitle(),locationBean.getPackageName()});
    }

    public synchronized void updateIndex(LocationBean locationBean){
        if(locationBean == null){
            return;
        }
        String sql = "update " + LOCATION_TABLE + " set " +
                PARENTINDEX + " = ?," +
                CHILDINDEX + " = ?," +
                NAME + " = ?," +
                TITLE + " = ?," +
                INSTALLED + " = ?," +
                STATUS + " = ?," +
                RESERVE1 + " = ?," +
                RESERVE2 + " = ?," +
                RESERVE3 + " = ?" +
                " where " +
                PACKAGENAMELOCATION + " = ?";
        db.execSQL(sql,new Object[]{locationBean.getParentIndex(),locationBean.getChildIndex(),locationBean.getName(),
                locationBean.getTitle(), locationBean.getInstalled(), locationBean.getStatus(),locationBean.getReserve1(),
                locationBean.getReserve2(), locationBean.getReserve3(), locationBean.getPackageName()});
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
     *返回的非0，则说明存在此应用，否则说明不存在此应用
     */
    public int isExistPackageInDownload(String packageName){
        int num = 0;
        if(!isDownloadTableExist()){
            num = 0;
        }else {
            String sql = "select count(*) from " + DOWNLOAD_TABLE + " where " + PACKAGENAMELOCATION + " = '" + packageName + "'";
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
