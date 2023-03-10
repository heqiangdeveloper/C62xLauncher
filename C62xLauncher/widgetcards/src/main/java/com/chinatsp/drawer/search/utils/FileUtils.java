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
     *????????????
     * @param searchBeanList ???????????????
     * @param str ???????????????
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
     *????????????
     * @param packageName??????
     */
    public static void launchApp(Context context, String packageName){
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            context.startActivity(intent);
        }catch (Exception e){
            Toast.makeText(context,"?????????????????????",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * textview???????????????????????????????????????
     * @param context ?????????
     * @param wholeStr ????????????
     * @param keyStr ?????????
     * @param keyStrColor ???????????????
     * @return
     */
    public static SpannableStringBuilder fillColor(Context context,String wholeStr, String[] keyStr, int keyStrColor) {
        if (!TextUtils.isEmpty(wholeStr) ) {

            SpannableStringBuilder spBuilder = new SpannableStringBuilder(wholeStr);
            for(int i = 0;i<keyStr.length;i++){
                CharacterStyle charaStyle = new ForegroundColorSpan(context.getResources().getColor(keyStrColor));
               // int start = wholeStr.indexOf(keyStr[i]);
                String str = wholeStr.toLowerCase();
                int start = str.indexOf(keyStr[i].toLowerCase());
                if (start < 0) {
                    continue;
                }
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
            type = 0;//??????
        } else {
            type = 1;//??????
        }
        return type;
    }
    /**
     * ???????????????
     *
     * @param activity ????????????
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
    /**
     * ??????str1?????????str2?????????
     * @param str1
     * @param str2
     * @return counter
     */
    private static int counter = 0;
    public static int countStr(String str1, String str2) {
        if (str1.indexOf(str2) == -1) {
            return 0;
        } else if (str1.indexOf(str2) != -1) {
            counter++;
            countStr(str1.substring(str1.indexOf(str2) +
                    str2.length()), str2);
            return counter;
        }
        return 0;
    }
}
