package com.chinatsp.vehicle.controller.bean

import android.os.Parcel
import android.os.Parcelable
import com.chinatsp.vehicle.controller.annotation.Action
import com.chinatsp.vehicle.controller.annotation.IPart
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
    @Model val model: Int = Model.VOID,
    @Action val action: Int = Action.VOID,
    @IStatus var status: Int = IStatus.INIT,
) : Parcelable {

    var part: Int = IPart.VOID

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

    var option: Int = -1

    var expect: Int = -1

    var lfExpect: Int = -1

    var rfExpect: Int = -1

    var lbExpect: Int = -1

    var rbExpect: Int = -1

    /**
     * 命令执行次数
     */
    var exeCount: Int = 1

    var message: String = ""

    var slots: Slots? = null

    constructor(parcel: Parcel) : this(model = parcel.readInt(), action = parcel.readInt()) {
        fromParcel(parcel)
    }

    fun fromParcel(parcel: Parcel): BaseCmd {
        status = parcel.readInt()
        part = parcel.readInt()
        step = parcel.readInt()
        value = parcel.readInt()
        option = parcel.readInt()
        expect = parcel.readInt()
        lfExpect = parcel.readInt()
        rfExpect = parcel.readInt()
        lbExpect = parcel.readInt()
        rbExpect = parcel.readInt()
        exeCount = parcel.readInt()
        message = parcel.readString().toString()
        slots = parcel.readParcelable(Slots::class.java.classLoader)
        return this
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(model)
        parcel.writeInt(action)
        parcel.writeInt(status)

        parcel.writeInt(part)
        parcel.writeInt(step)
        parcel.writeInt(value)
        parcel.writeInt(option)
        parcel.writeInt(expect)
        parcel.writeInt(lfExpect)
        parcel.writeInt(rfExpect)
        parcel.writeInt(lbExpect)
        parcel.writeInt(rbExpect)
        parcel.writeInt(exeCount)
        parcel.writeString(message)
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