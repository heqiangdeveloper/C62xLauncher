package com.chinatsp.vehicle.controller.bean

import android.os.Parcel
import android.os.Parcelable
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IAct
import com.chinatsp.vehicle.controller.annotation.ICar
import com.chinatsp.vehicle.controller.annotation.Model

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/10/10 13:47
 * @desc   :
 * @version: 1.0
 */
class CarCmd(@Model model: Int, @Action action: Int) : BaseCmd(model, action), Parcelable {

    var car: Int = ICar.VOID

    var act: Int = IAct.VOID

    var graded: Boolean = false

    var color: String = ""

    constructor(parcel: Parcel) : this(model = parcel.readInt(), action = parcel.readInt()) {
        fromParcel(parcel)
        car = parcel.readInt()
        act = parcel.readInt()
        graded = parcel.readByte() != 0.toByte()
        color = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(car)
        parcel.writeInt(act)
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