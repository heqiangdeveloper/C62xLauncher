package com.chinatsp.vehicle.controller.bean

import android.os.Parcel
import android.os.Parcelable
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IStatus
import com.chinatsp.vehicle.controller.annotation.Model
import com.chinatsp.vehicle.controller.semantic.Slots

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/17 15:38
 * @desc   :
 * @version: 1.0
 */
open class BaseCmd(
    @Model val model: Int = Model.INVALID,
    @Action val action: Int = Action.VOID,
    @IStatus var status: Int = IStatus.INIT,
) : Parcelable {

    /**
     * 增加 或 减少的步长
     * 当且仅池 action 为 Action.PLUS 或 Action.MINUS 时有效
     */
    var step: Int = 1

    /**
     * 设置固定值
     * 当且仅池 action 为 Action.FIXED 时有效
     */
    var value: Int = 1

    var slots: Slots? = null

    var message: String = ""

    constructor(@Model model: Int, @Action action: Int, slots: Slots) : this(action = action,
        model = model) {
        this.slots = slots
    }

    constructor(parcel: Parcel) : this(
        status = parcel.readInt(),
        action = parcel.readInt(),
        model = parcel.readInt()) {
        step = parcel.readInt()
        value = parcel.readInt()
        slots = parcel.readParcelable(Slots::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(status)
        parcel.writeInt(action)
        parcel.writeInt(model)
        parcel.writeInt(step)
        parcel.writeInt(value)
        parcel.writeParcelable(slots, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "BaseCmd(model=$model, action=$action, status=$status, step=$step, value=$value, slots=$slots, message='$message')"
    }

    companion object CREATOR : Parcelable.Creator<BaseCmd> {
        override fun createFromParcel(parcel: Parcel): BaseCmd {
            return BaseCmd(parcel)
        }

        override fun newArray(size: Int): Array<BaseCmd?> {
            return arrayOfNulls(size)
        }
    }



}