package com.chinatsp.vehicle.controller.bean

import android.os.Parcel
import android.os.Parcelable
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.annotation.IWindDire
import com.chinatsp.vehicle.controller.annotation.Model

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 13:47
 * @desc   :
 * @version: 1.0
 */
class AirCmd(
    @Model model: Int,
    @Action action: Int,
    @IStatus status: Int,
) : BaseCmd(model, action, status), Parcelable {

    var temp: Boolean = false

    var wind: Boolean = false

    var graded: Boolean = false

    var direct: Boolean = false

    var windDire: Int = IWindDire.FOOT

    constructor(parcel: Parcel) : this(
        status = parcel.readInt(),
        action = parcel.readInt(),
        model = parcel.readInt()) {
        temp = parcel.readByte() != 0.toByte()
        wind = parcel.readByte() != 0.toByte()
        graded = parcel.readByte() != 0.toByte()
        direct = parcel.readByte() != 0.toByte()
        windDire = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeByte(if (temp) 1 else 0)
        parcel.writeByte(if (wind) 1 else 0)
        parcel.writeByte(if (graded) 1 else 0)
        parcel.writeByte(if (direct) 1 else 0)
        parcel.writeInt(windDire)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AirCmd> {
        override fun createFromParcel(parcel: Parcel): AirCmd {
            return AirCmd(parcel)
        }

        override fun newArray(size: Int): Array<AirCmd?> {
            return arrayOfNulls(size)
        }
    }


}