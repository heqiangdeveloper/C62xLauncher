package com.chinatsp.vehicle.controller.data;

import android.os.Parcel;
import android.os.Parcelable;

public final class Semantic implements Parcelable {

    public Slots slots;

    public static class Slots implements Parcelable{

        public String temperature = "";
        public String fanSpeed = "";
        /**
         * "productName": "小t"
         */
        public String productName = "";
//        public DateBean startDate;
//
//        public PosRankBean posRank;
//        public PageRankBean pageRank;

        public String routeCondition;

        public String content = "";

        public String contentType = "";

        public String receiver = "";

        public String money = "";


        /**
         * "insType": "OPEN"
         */
        public String insType = "";

        public String tag = "";

        /**
         * "waveband": "fm"
         */
        public String waveband = "";

        /**
         * 93.8
         */
        public String code = "";

        /**
         * "category": "方向盘按键"
         */
        public String category = "";

        /**
         * 亮度，音量调节中的具体值
         */
        public String series = "";

        public String area = "";
        public String lang = "";
        public String tags = "";
        public String version = "";

        public String name = "";
        //空调中的模式
        public String mode = "";
        public String modeValue = "";

        /**
         * "fuzzyPart": "4379"
         */
        public String fuzzyPart = "";
        /**
         * "headNum": "189"
         */
        public String headNum = "";
        public String action = "";
        //public String nameValue;
        //氛围灯颜色
        public String color = "";

        //空调吹面，吹脚
        public String airflowDirection = "";

        public static class DateBean {
            /**
             * date : 2019-04-09
             * dateOrig : 明天
             * type : DT_BASIC
             */
            public String date;
            public String dateOrig;
            public String type;
        }

        public static class PosRankBean {
            public String direct;
            public String offset;
            public String ref;
            public String type;
        }

        public static class PageRankBean {
            public String direct;
            public String offset;
            public String ref;
            public String type;
        }

        protected Slots(Parcel in) {
            temperature = in.readString();
            fanSpeed = in.readString();
            productName = in.readString();
            routeCondition = in.readString();
            content = in.readString();
            contentType = in.readString();
            receiver = in.readString();
            money = in.readString();
            insType = in.readString();
            tag = in.readString();
            waveband = in.readString();
            code = in.readString();
            category = in.readString();
            series = in.readString();
            area = in.readString();
            lang = in.readString();
            tags = in.readString();
            version = in.readString();
            name = in.readString();
            mode = in.readString();
            modeValue = in.readString();
            fuzzyPart = in.readString();
            headNum = in.readString();
            action = in.readString();
            color = in.readString();
            airflowDirection = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(temperature);
            dest.writeString(fanSpeed);
            dest.writeString(productName);
            dest.writeString(routeCondition);
            dest.writeString(content);
            dest.writeString(contentType);
            dest.writeString(receiver);
            dest.writeString(money);
            dest.writeString(insType);
            dest.writeString(tag);
            dest.writeString(waveband);
            dest.writeString(code);
            dest.writeString(category);
            dest.writeString(series);
            dest.writeString(area);
            dest.writeString(lang);
            dest.writeString(tags);
            dest.writeString(version);
            dest.writeString(name);
            dest.writeString(mode);
            dest.writeString(modeValue);
            dest.writeString(fuzzyPart);
            dest.writeString(headNum);
            dest.writeString(action);
            dest.writeString(color);
            dest.writeString(airflowDirection);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Slots> CREATOR = new Creator<Slots>() {
            @Override
            public Slots createFromParcel(Parcel in) {
                return new Slots(in);
            }

            @Override
            public Slots[] newArray(int size) {
                return new Slots[size];
            }
        };
    }

    private Semantic(Parcel in) {
        slots = in.readParcelable(Slots.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(slots, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Semantic> CREATOR = new Creator<Semantic>() {
        @Override
        public Semantic createFromParcel(Parcel in) {
            return new Semantic(in);
        }

        @Override
        public Semantic[] newArray(int size) {
            return new Semantic[size];
        }
    };
}
