// AppStoreProxy.aidl
package com.huawei.appmarket.launcheragent;

// Declare any non-default types here with import statements

//通过该服务接口发送指令遥控应用市场
interface AppStoreProxy {
   /**
   * 对外接口
   * @param commandType 指令
   * @param pkgName app包名
   */
   void doCommand(int commandType, String pkgName);
}