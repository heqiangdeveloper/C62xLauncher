package com.chinatsp.vehicle.controller.semantic;

import android.os.Parcel;
import android.os.Parcelable;

public class NlpVoiceModel implements Parcelable {
    public String service = "";
    public String operation = "";
    public Slots slots;
    public String text = "";
    public String dataEntity = "";
    public String response = "";
    public int direction;
    public int isOuting;

    public NlpVoiceModel() {

    }

    protected NlpVoiceModel(Parcel in) {
        service = in.readString();
        operation = in.readString();
        slots = in.readParcelable(Slots.class.getClassLoader());
        text = in.readString();
        dataEntity = in.readString();
        response = in.readString();
        direction = in.readInt();
        isOuting = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service);
        dest.writeString(operation);
        dest.writeParcelable(slots, flags);
        dest.writeString(text);
        dest.writeString(dataEntity);
        dest.writeString(response);
        dest.writeInt(direction);
        dest.writeInt(isOuting);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NlpVoiceModel> CREATOR = new Creator<NlpVoiceModel>() {
        @Override
        public NlpVoiceModel createFromParcel(Parcel in) {
            return new NlpVoiceModel(in);
        }

        @Override
        public NlpVoiceModel[] newArray(int size) {
            return new NlpVoiceModel[size];
        }
    };
}
