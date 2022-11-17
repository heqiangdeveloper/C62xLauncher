package com.chinatsp.vehicle.controller.utils

interface Keywords {
    companion object {
        const val SET = "SET"
        const val OPEN = "OPEN"
        const val LAUNCH = "LAUNCH"
        const val CLOSE = "CLOSE"
        const val QUERY = "QUERY"
        const val CHAIR = "座椅"
        const val HEAT = "加热"
        const val KNEAD = "按摩"
        const val VENTILATE = "通风"
        const val CHAIR_HEAT = "座椅加热"
        const val CHAIR_VENTILATE = "座椅通风"

        const val ENDURANCE_MILEAGE = "续航里程"

        const val MAINTAIN_MILEAGE = "保养"

        const val AVERAGE_FUEL_CONSUMPTION = "平均油耗"

        const val FUEL_CONSUMPTION = "油耗"

        const val TIRE_PRESSURE = "胎压"

        const val REMAINING = "油量"

        const val KM = "公里"

        const val PANORAMA = "360"

        const val VIEW_TRANS = "VIEW_TRANS"




        /**
         * 操作指令，表示为打开的操作数组
         */
        val OPT_OPENS = arrayOf(LAUNCH, OPEN, SET, "COLLECT")

        /**
         * 操作指令，表示为关闭的操作数组
         */
        val OPT_CLOSES = arrayOf("EXIT", "CLOSE")
        val AIR_MODES = arrayOf("制冷", "压缩机")
        val SKYLIGHTS = arrayOf("天窗", "天窗透气", "天窗通风", "打开透气", "天窗换气", "天窗翘起")
        const val SKYLIGHT = "天窗"
        const val SKYLIGHT_OPEN = "天窗透气"
        const val SKYLIGHT_OPEN2 = "天窗通风"
        const val SKYLIGHT_OPEN3 = "打开透气"
        const val SKYLIGHT_OPEN4 = "天窗换气"
        const val SKYLIGHT_OPEN5 = "天窗翘起"
        const val SKYLIGHT_MODE = "透气模式"
        const val ABAT_VENT = "遮阳帘"
        const val DRIVING_MODE = "驾驶模式"
        const val LAMP = "氛围灯"
        val DIPPED_HEAD_LIGHT = arrayOf("近光灯", "小灯", "灯光")
        val DISTANT_HEAD_LIGHT = arrayOf("远光灯", "大灯")
        val FOG_LIGHT: String get() = "雾灯"
        val SIDE_LIGHT: String get() = "位置灯"
        val AMBIENTS = arrayOf("氛围灯", "前排氛围灯", "后排氛围灯")
        const val LAMPLIGHTPLUS = "PLUS"
        const val LAMPLIGHTMINUS = "MINUS"
        const val LAMPLIGHTPLUS_1 = "高"
        const val LAMPLIGHTPLUS_2 = "低"
        val WINDOWS = arrayOf("车窗", "窗户")
        val WINDOW_ALL = arrayOf("所有窗户", "所有车窗", "窗户", "车窗")
        val L_F = arrayOf("主驾", "左前", "前左")
        val L_R = arrayOf("左后", "后左")
        val R_F = arrayOf("副驾", "右前", "前右")
        val R_R = arrayOf("右后", "后右")

        val L_C = arrayOf("左边", "左排")
        val R_C = arrayOf("右边", "右排")

        val F_R = arrayOf("前边", "前排")
        val B_R = arrayOf("后边", "后排", "乘客")

        const val WINDOW_LEFT_FRONT = "左前车窗"
        const val WINDOW_RIGHT_FRONT = "右前车窗"
        const val WINDOW_LEFT_FRONT2 = "主驾车窗"
        const val WINDOW_RIGHT_FRONT2 = "副驾车窗"
        const val WINDOW_LEFT_BACK = "左后车窗"
        const val WINDOW_RIGHT_BACK = "右后车窗"
        const val WINDOW_BACK = "后车窗"
        const val WINDOW = "车窗"
        const val WINDOW_VENTILATE = "车窗透气"
        const val WINDOW_FRONT = "前车窗"
        const val WINDOW_FRONT2 = "前排"
        const val WINDOW_BACK2 = "后排"
        const val WINDOW_LEFT_BACK2 = "后排左车窗"
        const val WINDOW_RIGHT_BACK2 = "后排右车窗"
        val LOUVER = arrayOf("天窗", "遮阳帘")
        const val PARK_AUTO = "自动泊车"
        const val SLIDE_DOOR_LEFT = "左侧滑门"
        const val SLIDE_DOOR_RIGHT = "右侧滑门"
        const val SLIDE_DOOR_CENTER = "中滑门"
        const val SLIDE_DOOR_ALL = "所有滑门"
        const val MIRROR = "后视镜"
        const val REFUEL_MODE = "加油模式"
        const val REFUEL_TEXT = "加油"
        const val OIL_SHROUD = "油箱盖"
        const val OIL_SHROUD_TEXT = "油箱"
        val OIL_SHROUDS = arrayOf("油箱盖", "油箱")
        val HOODS = arrayOf("前罩", "前罩盖", "前舱盖", "引擎盖", "电动前罩")
        val TRUNKS = arrayOf("电动后备厢", "后背门", "电动后背门", "电动尾门", "尾门",
            "后备箱", "电动后备箱", "尾箱", "电动尾箱", "背门", "电动背门")
//        val WIPERS = arrayOf("雨刮", "雨刷", "前雨刮", "前雨刮器", "雨刮器", "前雨刷", "前雨刷器", "雨刷器")
        val WIPERS = arrayOf("雨刮", "雨刷", "雨刮器", "雨刷器")
        val REAR_WIPERS = arrayOf("后雨刮", "后雨刮器", "后雨刷器", "后雨刷")
        val TIRE_PRESSURE_MONITORS = arrayOf("胎压监测", "胎压")
        val SMOKES = arrayOf("抽烟", "抽烟模式", "小欧我要抽烟")
        const val WIRELESS_CHARGING = "无线充电"
        val IDLE_START_AND_STOP = arrayOf("怠速启停", "智能启停")
        val AUTO_HEAD_LIGHTS = arrayOf("大灯")
        val LIGHTS = arrayOf("近光灯", "远光灯", "位置灯")
        val FOG_LIGHTS = arrayOf("雾灯", "后雾灯")
        const val MODE_DRIVE = "驾驶模式"
        const val MODE_ECO = "经济"
        const val MODE_SPORT = "运动"
        const val MODE_COMFORT = "舒适"
        const val MODE_TRACK = "赛道"
        val MODE_NAMES = arrayOf(MODE_SPORT, MODE_COMFORT, MODE_ECO, MODE_TRACK)
        val L_F_WINDOW = arrayOf("左前车窗", "前左车窗", "主驾车窗", "左前窗户", "前左窗户", "主驾窗户")
        val R_F_WINDOW = arrayOf("右前车窗", "前右车窗", "副驾车窗", "右前窗户", "副驾窗户", "前右窗户")

        val L_R_WINDOW = arrayOf("左后车窗", "后左车窗", "左后窗户", "后左窗户")
        val R_R_WINDOW = arrayOf("右后车窗", "后右车窗", "右后窗户", "后右窗户")

        val L_WINDOW = arrayOf("左车窗", "左窗户")
        val R_WINDOW = arrayOf("右车窗", "右窗户")

        val F_WINDOW = arrayOf("前车窗", "前窗户")
        val B_WINDOW = arrayOf("后车窗", "后窗户")


        val VOICE_VENT = arrayOf("车外声浪")
        val WHEELS = arrayOf("方向盘", "方向")

        /**
         * 二次交互，肯定词汇
         */
        val SECOND_AFFIMS = arrayOf("确定", "好的", "好啊", "可以", "需要", "同意")

        /**
         * 二次交互，否定词汇
         */
        val SECOND_NEGATIVES = arrayOf("取消", "退下", "不行", "不好", "不用")

        //加油模式 免唤醒退出
        val OIL_MODE_MVWACTION = arrayOf("退下")
        const val NAME_VALUE_HALF = "二分之一"
        const val NAME_VALUE_ONE_THIRD = "三分之一"
        const val NAME_VALUE_QUARTER = "四分之一"
//        const val VENTILATE = "透气"

        //------------ 可见即可说热词 start ------------
        //设置左侧导航菜单
        val HOTWORDS_MENU =
            arrayOf("快捷", "车况", "小欧", "座舱", "灯光", "EaglePilot", "车窗", "门锁", "连接", "显示", "声音", "系统")

        //快捷界面
        val HOTWORDS_FASTCONTROL = arrayOf("打开朋友模式", "关闭朋友模式", "打开洗车模式", "关闭洗车模式")

        //------------- 可见即可说热词 end -------------
        val PACKAGES = arrayOf("com.chinatsp.dvrcamera", "com.onstyle.track") //

        //太热了
        const val MINUS_MORE = "MINUS_MORE"

        //有点热
        const val MINUS_LITTLE = "MINUS_LITTLE"

        // 太冷了
        const val PLUS_MORE = "PLUS_MORE"

        //有点冷
        const val PLUS_LITTLE = "PLUS_LITTLE"

        // 温度增高/风量增高
        const val PLUS = "PLUS"

        // 温度降低/风量降低
        const val MINUS = "MINUS"

        // 温度设为中档
        const val MEDIUM = "MEDIUM"

        // 温度最高/风量最高
        const val MAX = "MAX"

        // 温度最低
        const val MIN = "MIN"

        // 升高两度参数
        const val REF_CUR = "CUR"
        const val REF_ZERO = "ZERO"

        const val COMMAND_FAILED = "操作没有成功，等一下再试吧"

        const val NEED_START_ENGINE = "操作没有成功，请先启动发动机"


    }
}