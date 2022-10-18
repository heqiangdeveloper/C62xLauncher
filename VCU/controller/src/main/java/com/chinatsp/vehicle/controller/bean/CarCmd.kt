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
class CarCmd(
    @Model model: Int,
    @Action action: Int,
    @IStatus status: Int,
) : BaseCmd(model, action, status), Parcelable {

    var car: Int = ICar.VOID

    var part: Int = IPart.DEFAULT

    /**
     * 空调 吹风方向
     */
    var orien: Int = IOrien.DEFAULT

    var graded: Boolean = false

    var color: String = ""

    constructor(@Action action: Int, @Model model: Int) : this(
        status = IStatus.INIT,
        model = model,
        action = action)

    constructor(parcel: Parcel) : this(
        status = parcel.readInt(),
        action = parcel.readInt(),
        model = parcel.readInt()) {
        car = parcel.readInt()
        part = parcel.readInt()
        orien = parcel.readInt()
        graded = parcel.readByte() != 0.toByte()
        color = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(car)
        parcel.writeInt(part)
        parcel.writeInt(orien)
        parcel.writeByte(if (graded) 1 else 0)
        parcel.writeString(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CarCmd> {
        override fun createFromParcel(parcel: Parcel): CarCmd {
            return CarCmd(parcel)
        }

        override fun newArray(size: Int): Array<CarCmd?> {
            return arrayOfNulls(size)
        }
    }

}