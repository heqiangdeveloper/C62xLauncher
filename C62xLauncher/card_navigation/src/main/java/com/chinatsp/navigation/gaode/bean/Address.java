package com.chinatsp.navigation.gaode.bean;

import android.util.Log;

import org.json.JSONObject;

public class Address {

    private String address;
    private String provinceName;
    private int resultCode;
    private double longitude;
    private String provinceCode;
    private String myLocationName;
    private double latitude;
    private String errorMessage;
    private String districtCode;
    private String cityCode;
    private String fullAddress;
    private String cityName;
    private String countryName;
    private String poiName;
    private String districtName;
    private String countryCode;

    public String getAddress() {
        return address;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public int getResultCode() {
        return resultCode;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getMyLocationName() {
        return myLocationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getPoiName() {
        return poiName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public static Address parseFrom(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Address address = new Address();
        address.myLocationName = jsonObject.optString("myLocationName");
        address.poiName = jsonObject.optString("poiName");
        address.countryName = jsonObject.optString("countryName");
        address.countryCode = jsonObject.optString("countryCode");
        address.provinceName = jsonObject.optString("provinceName");
        address.cityName = jsonObject.optString("cityName");
        address.cityCode = jsonObject.optString("cityCode");
        address.districtCode = jsonObject.optString("districtCode");
        address.fullAddress = jsonObject.optString("fullAddress");
        address.address = jsonObject.optString("address");
        address.districtName = jsonObject.optString("districtName");
        address.longitude = jsonObject.optDouble("longitude");
        address.longitude = jsonObject.optDouble("longitude");
        return address;
    }

    @Override
    public String toString() {
        return "Address{" +
                "address='" + address + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", resultCode=" + resultCode +
                ", longitude=" + longitude +
                ", provinceCode='" + provinceCode + '\'' +
                ", myLocationName='" + myLocationName + '\'' +
                ", latitude=" + latitude +
                ", errorMessage='" + errorMessage + '\'' +
                ", districtCode='" + districtCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", poiName='" + poiName + '\'' +
                ", districtName='" + districtName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}
