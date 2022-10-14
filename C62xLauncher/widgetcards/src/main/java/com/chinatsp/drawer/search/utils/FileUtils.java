package com.chinatsp.drawer.search.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chinatsp.drawer.bean.SearchBean;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
    public static String getFromAssets(Context context, String fileName) {
        String line = "";
        StringBuffer Result = new StringBuffer();
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            while ((line = bufReader.readLine()) != null) {
                Result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.toString();
    }

    public static <T> List<T> stringToList(String json,Class<T> cls){
        Gson gson = new Gson();
        List<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem,cls));
        }
        return list;
    }

    /**
     *模糊搜索
     * @param searchBeanList 数据库数据
     * @param str 搜索字符串
     * @return
     */
    public static List<SearchBean> fuzzySearch(String str,List<SearchBean> searchBeanList){
        List<SearchBean> list = new ArrayList<>();
        //Pattern pattern = Pattern.compile(str);
        Pattern pattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
        for(int i = 0;i<searchBeanList.size();i++){
            Matcher matcher = pattern.matcher(searchBeanList.get(i).getChineseFunction());
            if(matcher.find()){
                list.add(searchBeanList.get(i));
            }
        }
        return list;
    }
    /*
     *打开应用
     * @param packageName包名
     */
    public static void launchApp(Context context, String packageName){
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            context.startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context,"该应用还未下载",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * textview指定位置或字段设置指定颜色
     * @param context 上下文
     * @param wholeStr 全部文字
     * @param keyStr 关键字
     * @param keyStrColor 关键字颜色
     * @return
     */
    public static SpannableStringBuilder fillColor(Context context,String wholeStr, String[] keyStr, int keyStrColor) {
        if (!TextUtils.isEmpty(wholeStr) ) {
            SpannableStringBuilder spBuilder = new SpannableStringBuilder(wholeStr);
            for(int i = 0;i<keyStr.length;i++){
                CharacterStyle charaStyle = new ForegroundColorSpan(context.getResources().getColor(keyStrColor));
               // int start = wholeStr.indexOf(keyStr[i]);
                int start = wholeStr.toLowerCase().indexOf(keyStr[i]);
                int end = start + keyStr[i].length();
                spBuilder.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spBuilder;
        } else {
            return null;

        }

    }

    public static int getLanguage() {
        int type;
        String lang = Locale.getDefault().getLanguage();
        if ("en".equals(lang)) {
            type = 0;//英文
        } else {
            type = 1;//中文
        }
        return type;
    }
    /**
     * 隐藏输入法
     *
     * @param activity 当前页面
     */

    public static void hideSoftInput(Activity activity) {
        if (activity == null || activity.getCurrentFocus() == null) {
            return;
        }
        int times = 0;
        boolean isClosed = false;
        InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        while (!isClosed && times <= 5) {
            times++;
            isClosed = manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
