package com.chinatsp.vehicle.controller.utils;

public interface ConstantsVolume {

    /**
     * 操作指令，表示为打开的操作数组
     */
    String[] OPT_OPENS = {"LAUNCH", "OPEN", "SET", "COLLECT"};
    /**
     * 操作指令，表示为关闭的操作数组
     */
    String[] OPT_CLOSES = {"EXIT", "CLOSE"};

    String SKYLIGHT = "天窗";
    String SKYLIGHT_OPEN = "天窗透气";
    String SKYLIGHT_OPEN2 = "天窗通风";
    String SKYLIGHT_OPEN3 = "打开透气";
    String SKYLIGHT_OPEN4 = "天窗换气";
    String SKYLIGHT_OPEN5 = "天窗翘起";
    String SKYLIGHT_MODE = "透气模式";
    String ABAT_VENT = "遮阳帘";
    String DRIVING_MODE = "驾驶模式";
    String LAMP = "氛围灯";
    String LAMPLIGHTPLUS = "PLUS";
    String LAMPLIGHTMINUS = "MINUS";
    String LAMPLIGHTPLUS_1 = "高";
    String LAMPLIGHTPLUS_2 = "低";

    String[] WINDOW_ALL = {"所有窗户", "所有车窗"};
    String WINDOW_LEFT_FRONT = "左前车窗";
    String WINDOW_RIGHT_FRONT = "右前车窗";
    String WINDOW_LEFT_FRONT2 = "主驾车窗";
    String WINDOW_RIGHT_FRONT2 = "副驾车窗";
    String WINDOW_LEFT_BACK = "左后车窗";
    String WINDOW_RIGHT_BACK = "右后车窗";
    String WINDOW_BACK = "后车窗";
    String WINDOW = "窗户";
    String WINDOW_VENTILATE = "车窗透气";

    String WINDOW_FRONT = " 前车窗";
    String WINDOW_FRONT2 = " 前排";
    String WINDOW_BACK2 = "后排";
    String WINDOW_LEFT_BACK2 = "后排左车窗";
    String WINDOW_RIGHT_BACK2 = "后排右车窗";

    String[] LOUVER = {"天窗", "遮阳帘"};

    String PARK_AUTO = "自动泊车";
    String SLIDE_DOOR_LEFT = "左侧滑门";
    String SLIDE_DOOR_RIGHT = "右侧滑门";
    String SLIDE_DOOR_CENTER = "中滑门";
    String SLIDE_DOOR_ALL = "所有滑门";

    String MIRROR = "后视镜";

    String REFUEL_MODE = "加油模式";
    String REFUEL_TEXT = "加油";

    String OIL_SHROUD = "油箱盖";
    String OIL_SHROUD_TEXT = "油箱";

    String[] HOODS = {"前罩", "前罩盖", "前舱盖", "引擎盖", "电动前罩"};
    String[] TRUNKS = {"电动后备厢", "后背门", "电动后背门", "电动尾门", "尾门", "后备箱", "电动后备箱", "尾箱", "电动尾箱", "背门", "电动背门"};
    String[] WIPERS = {"雨刮", "雨刷", "前雨刮", "前雨刮器", "雨刮器", "前雨刷", "前雨刷器", "雨刷器"};
    String[] REAR_WIPERS = {"后雨刮", "后雨刮器", "后雨刷器", "后雨刷"};
    String[] TIRE_PRESSURE_MONITORS = {"胎压监测", "胎压"};
    String[] SMOKES = {"抽烟", "抽烟模式", "小欧我要抽烟"};
    String WIRELESS_CHARGING = "无线充电";
    String[] IDLE_START_AND_STOP = {"怠速启停", "智能启停"};
    String[] AUTO_HEAD_LIGHTS = {"大灯"};
    String[] LIGHTS = {"近光灯", "远光灯", "位置灯"};
    String[] FOG_LIGHTS = {"雾灯", "后雾灯"};
    String MODE_DRIVE = "驾驶模式";
    String MODE_ECO = "经济";
    String MODE_SPORT = "运动";
    String MODE_COMFORT = "舒适";
    String MODE_TRACK = "赛道";
    String[] MODE_NAMES = {MODE_SPORT, MODE_COMFORT, MODE_ECO, MODE_TRACK};
    String[] DRIVER_WINDOW_COMMS = {"左前车窗", "前左车窗", "主驾车窗"};
    String[] PASSENGER_WINDOW_COMMS = {"右前车窗", "副驾车窗", "前右车窗"};
    String[] VOICE_VENT = {"车外声浪"};


    /**
     * 二次交互，肯定词汇
     */
    String[] SECOND_AFFIMS = {"确定", "好的", "好啊", "可以", "需要", "同意"};
    /**
     * 二次交互，否定词汇
     */
    String[] SECOND_NEGATIVES = {"取消", "退下", "不行", "不好", "不用"};
    //加油模式 免唤醒退出
    String[] OIL_MODE_MVWACTION = {"退下"};

    String NAME_VALUE_HALF = "二分之一";
    String NAME_VALUE_ONE_THIRD = "三分之一";
    String NAME_VALUE_QUARTER = "四分之一";
    String VENTILATE = "透气";


    //------------ 可见即可说热词 start ------------
    //设置左侧导航菜单
    String[] HOTWORDS_MENU = {"快捷", "车况", "小欧", "座舱", "灯光", "EaglePilot", "车窗", "门锁", "连接", "显示", "声音", "系统"};
    //快捷界面
    String[] HOTWORDS_FASTCONTROL = {"打开朋友模式", "关闭朋友模式", "打开洗车模式", "关闭洗车模式"};


    //------------- 可见即可说热词 end -------------

    String[] PACKAGES = {"com.chinatsp.dvrcamera", "com.onstyle.track"};


}
