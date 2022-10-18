package com.chinatsp.vehicle.controller.bean

import android.os.Parcel
import android.os.Parcelable
import com.chinatsp.vehicle.controller.annotation.*

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

    var air: Int = IAir.DEFAULT

    var part: Int = IPart.DEFAULT

    /**
     * 空调 吹风方向
     */
    var orien: Int = IOrien.DEFAULT

    var graded: Boolean = false

    constructor(@Action action: Int) : this(
        model = Model.CABIN_AIR,
        status = IStatus.INIT,
        action = action)

    constructor(parcel: Parcel) : this(
        status = parcel.readInt(),
        action = parcel.readInt(),
        model = parcel.readInt()) {
        air = parcel.readInt()
        part = parcel.readInt()
        orien = parcel.readInt()
        graded = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(air)
        parcel.writeInt(part)
        parcel.writeInt(orien)
        parcel.writeByte(if (graded) 1 else 0)
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