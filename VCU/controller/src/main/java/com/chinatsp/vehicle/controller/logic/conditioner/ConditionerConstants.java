package com.chinatsp.vehicle.controller.logic.conditioner;

public interface ConditionerConstants {
    String[] KT_USED = {"好的", "嗯", "主人我知道了", "收到"};
    String[] KT_COMPRESSOR_OPEN = {"打开压缩机", "打开制冷模式", "打开AC模式", "空调设为AC模式", "打开空调AC", "打开空调压缩机"};
    String[] KT_COMPRESSOR_CLOSE = {"关闭AC模式", "空调AC关闭", "关闭空调AC模式", "把空调AC关闭", "把空调AC关掉", "关闭空调AC", "关闭压缩机", "关闭空调压缩机"};
    String[] KT_WIND_BIG = {"风调大", "风大一点", "风量大", "增大", "吹风大一点", "加大", "风量调高", "空调风量大一点", "空调风速大一点"};//声源识别
    String[] KT_WIND_SMALL = {"小一点", "风小一点", "减小风量", "吹风小一点", "风量减少", "调低", "空调风量小一点", "空调风速小一点", "风量降低一档", "风量减低一档", "风量降低一点"};//声源识别
    String KT_WIND_GIG_VARIABLE = "调高风量%d级";//声源识别
    String KT_WIND_SMALL_VARIABLE = "降低风量%d级";//声源识别

    String KT_WIND_GIG_VARIABLE_1 = "风量设为%d级";
    String KT_WIND_SMALL_VARIABLE_1 = "风量设为%d级";

    String[] KT_WIND_BIG_1 = {"风量最大", "风量调到最大", "调到最大风量", "最大"};
    String[] KT_WIND_SMALL_1 = {"风量最小", "小", "最小", "调到最小风量", "最小风"};

    String[] KT_TEMPERATURE_BIG = {"太热了", "我觉得热", "我好热", "感觉车里有点热", "车里真热", "车里好热", "温度高", "空调温度高", "低一点", "空调低一点", "低", "温度调低", "降低", "空调降低"};//声源识别
    String[] KT_TEMPERATURE_SMALL = {"太冷了", "我觉得冷", "我好冷", "感觉车里有点冷", "车里真冷", "凉", "车里好冷", "空调温度调高", "把温度调高"};//声源识别

    String[] KT_TEMPERATURE_BIG_1 = {"调到最低温度", "最低温度", "空调温度调到最低", "很低", "调到空调最低温度", "最冷", "空调最冷"};
    String[] KT_TEMPERATURE_SMALL_1 = {"空调最热", "温度调到最高", "空调温度调到最高", "最高温度"};

    String KT_VARIABLE_VARIABLE_1 = "升高%d度";
    String KT_VARIABLE_VARIABLE_2 = "降低%d度";
    String KT_VARIABLE_VARIABLE_3 = "空调降低%d度";
    String KT_VARIABLE_VARIABLE_4 = "空调升高%d度";
    String KT_VARIABLE_VARIABLE_5 = "把空调调到%d度";
    String KT_VARIABLE_VARIABLE_6 = "把空调温度调到%d度";
    String KT_VARIABLE_VARIABLE_7 = "空调调到%d度";
    String KT_VARIABLE_VARIABLE_8 = "空调设为%d度";
    String KT_VARIABLE_VARIABLE_9 = "空调%d度";
    String KT_VARIABLE_VARIABLE_10 = "打开空调%d度";
    String KT_VARIABLE_VARIABLE_11 = "打开空调到%d度";
    String KT_VARIABLE_VARIABLE_12 = "空调达到%d度";
    String KT_VARIABLE_VARIABLE_13 = "设置空调%d度";
    String KT_VARIABLE_VARIABLE_14 = "设置空调温度%d度";

    String KT_DIRECTION_BODY = "吹身上";
    String KT_DIRECTION_BODICE = "吹上身";
    String KT_DIRECTION_FOOT = "吹脚";
    String KT_DIRECTION_FRONT_GLASS = "前挡玻璃看不清";
    String KT_DIRECTION_FACE = "吹脸模式";
    String KT_DIRECTION_FOOT_MODEL = "吹脚模式";
    String KT_DIRECTION_FACE_FOOT_MODEL = "吹脸吹脚模式";
    String KT_DIRECTION_FACE_FOO = "吹脸吹脚";
    String KT_DIRECTION_DEFROSTING = "打开前除霜";
    String KT_DIRECTION_FOG = "打开前除雾";
    String KT_DIRECTION_OPEN = "开一下除霜";
    ;
    String KT_DIRECTION_DEFROST = "设为除霜模式";
    String KT_DIRECTION_CLOSE_DEFROST = "关闭前除霜";
    String KT_DIRECTION_BEFORE_MIST = "前除雾";
    String KT_DIRECTION_CLOSE = "把除霜模式关了,除霜模式关闭,关闭除霜";

    String[] KT_INSIDE_CYCLE_OPEN = {"车内循环", "内循环", "打开内循环", "空调内循环", "打开空调内循环", "空调调到内循环", "内循环打开"};
    String[] KT_INSIDE_CYCLE_CLOSE = {"关闭内循环"};
    String[] KT_OUTER_CYCLE_OPEN = {"外循环打开", "打开空调外循环", "空调外循环", "开外循环"};

    String KT_OPEN_AFTER_DEFROSTING = "打开后除霜";
    String KT_OPEN_AFTER_MIST = "打开后除雾";
    String KT_OPEN_WINDOW_HEATING = "打开后窗加热";
    String KT_DEMIST = "后档玻璃除雾";
    String KT_DEFROST = "后档玻璃除霜";

    String[] KT_AUTOMATIC_MODEL = {"空调设置为自动模式", "空调调为自动"};

    String KT_LOW = "温度low";
    String KT_HIGH = "high调节";

    String KT_OPEN_AIR_PURIFICATION = "打开空气净化";
    String KT_CLOSE_AIR_PURIFICATION = "关闭空气净化";


    int KT_MODEL = 1;//模块类型，空调
    String KT_NAME = "空调";
}
