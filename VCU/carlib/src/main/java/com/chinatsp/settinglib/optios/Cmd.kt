package com.chinatsp.settinglib.optios

/**
 * @author : luohong
 * @e-mail : luohong1@bdstar.com
 * @date   : 2022/7/17 15:38
 * @desc   :
 * @version: 1.0
 */
enum class Cmd(val id: Byte) {
    VOID(-1),
    SEEK(1),
    OPEN(2),
    CLOSE(3),
    OPTION(4);

    companion object {

    }
}