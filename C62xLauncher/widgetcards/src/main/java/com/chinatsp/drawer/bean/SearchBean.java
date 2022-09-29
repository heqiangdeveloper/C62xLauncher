package com.chinatsp.drawer.bean;

import android.util.Log;

public class SearchBean {
    private String modelName;
    private String chineseFunction;
    private String chineseFunctionLevel;
    private String englishFunction;
    private String englishFunctionLevel;
    private String intentAction;
    private String intentInterface;
    private String dataVersion;
    private String carVersion;
    private final String TAG = "LocationBean";
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getChineseFunction() {
        return chineseFunction;
    }

    public void setChineseFunction(String chineseFunction) {
        this.chineseFunction = chineseFunction;
    }

    public String getChineseFunctionLevel() {
        return chineseFunctionLevel;
    }

    public void setChineseFunctionLevel(String chineseFunctionLevel) {
        this.chineseFunctionLevel = chineseFunctionLevel;
    }

    public String getEnglishFunction() {
        return englishFunction;
    }

    public void setEnglishFunction(String englishFunction) {
        this.englishFunction = englishFunction;
    }

    public String getEnglishFunctionLevel() {
        return englishFunctionLevel;
    }

    public void setEnglishFunctionLevel(String englishFunctionLevel) {
        this.englishFunctionLevel = englishFunctionLevel;
    }

    public String getIntentAction() {
        return intentAction;
    }

    public void setIntentAction(String intentAction) {
        this.intentAction = intentAction;
    }

    public String getIntentInterface() {
        return intentInterface;
    }

    public void setIntentInterface(String intentInterface) {
        this.intentInterface = intentInterface;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getCarVersion() {
        return carVersion;
    }

    public void setCarVersion(String carVersion) {
        this.carVersion = carVersion;
    }
    public void printLog(){
        Log.d(TAG,"modelName = " + modelName + ",chineseFunction = " + chineseFunction + ",chineseFunctionLevel = " +
                chineseFunctionLevel + ",englishFunction = " + englishFunction+ ",englishFunctionLevel = " + englishFunctionLevel+
                ",intentAction = " + intentAction+ ",intentInterface = " + intentInterface+ ",dataVersion = " + dataVersion+ ",carVersion = " +
                carVersion);
    }
}
