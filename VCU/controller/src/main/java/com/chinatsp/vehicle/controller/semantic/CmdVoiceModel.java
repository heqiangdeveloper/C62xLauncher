package com.chinatsp.vehicle.controller.semantic;

import android.os.Parcel;
import android.os.Parcelable;

public class CmdVoiceModel implements Parcelable {
    public int id;
    public String text;
    public int hide;
    public String response;
    public String uMsg;
    public int module;

    public CmdVoiceModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.uMsg);
        dest.writeString(this.text);
        dest.writeInt(this.hide);
        dest.writeString(this.response);
        dest.writeInt(this.module);
    }

    protected CmdVoiceModel(Parcel in) {

        this.id = in.readInt();
        this.uMsg = in.readString();
        this.text = in.readString();
        this.hide = in.readInt();
        this.response = in.readString();
        this.module = in.readInt();
    }

    public static final Creator<CmdVoiceModel> CREATOR = new Creator<CmdVoiceModel>() {
        @Override
        public CmdVoiceModel createFromParcel(Parcel source) {
            return new CmdVoiceModel(source);
        }

        @Override
        public CmdVoiceModel[] newArray(int size) {
            return new CmdVoiceModel[size];
        }
    };

    @Override
    public String toString() {
        return "CmdVoiceModel{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", hide=" + hide +
                ", response='" + response + '\'' +
                ", uMsg='" + uMsg + '\'' +
                '}';
    }
}
