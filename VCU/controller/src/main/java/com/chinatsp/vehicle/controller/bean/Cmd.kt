package com.chinatsp.vehicle.controller.bean

import android.os.Parcel
import android.os.Parcelable
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.annotation.Model
import java.util.*

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/17 15:38
 * @desc   :
 * @version: 1.0
 */
data class Cmd(
    val serial: String = UUID.randomUUID().toString(),
    @Action val action: Int,
    @Model val model: Int,
    @IStatus var status: Int
) : Parcelable {

    var message: String = ""

    constructor(
        @Action action: Int,
        @Model model: Int,
        @IStatus status: Int = IStatus.INIT,
        message: String
    ) : this(action = action, model = model, status = status) {
        this.message = message
    }

    constructor(parcel: Parcel) : this(
        action = parcel.readInt(),
        model = parcel.readInt(),
        status = parcel.readInt(),
        serial = parcel.readString() ?: ""
    ) {
        message = parcel.readString() ?: ""
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(action)
        dest.writeInt(model)
        dest.writeInt(status)
        dest.writeString(serial)
        dest.writeString(message)
    }

    override fun toString(): String {
        return "Cmd(serial='$serial', action=$action, model=$model, status=$status, message='$message')"
    }


    companion object CREATOR : Parcelable.Creator<Cmd> {
        override fun createFromParcel(parcel: Parcel): Cmd {
            return Cmd(parcel)
        }

        override fun newArray(size: Int): Array<Cmd?> {
            return arrayOfNulls(size)
        }
    }

}