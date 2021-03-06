package com.example.myxposedtools.xp

import com.example.myxposedtools.constant.Constant
import com.example.myxposedtools.utils.HookUtils
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
class XposedInit : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {
            //小米应用商店
            Constant.PKG_NAME_XIAOMI_MARKET -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_XIAOMI_MARKET}")
                XiaoMiMarketHook.handleLoadPackage(lpparam)
            }
            //小米商城
            Constant.PKG_NAME_XIAOMI_SHOP -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_XIAOMI_SHOP}")
                XiaoMiShopHook.handleLoadPackage(lpparam)
            }
            //微博
            Constant.PKG_NAME_WEI_BO -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_WEI_BO}")
                WeiBoHook.handleLoadPackage(lpparam)
            }
            //微信
            Constant.PKG_NAME_WEI_XIN -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_WEI_XIN}")
                WeiXinHook.handleLoadPackage(lpparam)
            }
            //迅雷
            Constant.PKG_NAME_XUNLEI -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_XUNLEI}")
                XunLeiHook.handleLoadPackage(lpparam)
            }
            //12306
            Constant.PKG_NAME_12306 -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_12306}")
                MobileTicketHook.handleLoadPackage(lpparam)
            }
            //米家
            Constant.PKG_NAME_MIJIA -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_MIJIA}")
                MiJiaHook.handleLoadPackage(lpparam)
            }
            //MIUI系统广告
            Constant.PKG_NAME_MIUI_SYSTEM_AD -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_MIUI_SYSTEM_AD}")
                MIUIMsaHook.handleLoadPackage(lpparam)
            }
            //小米视频
            Constant.PKG_NAME_VIDEO -> {
                HookUtils.log("handleLoadPackage ${Constant.PKG_NAME_VIDEO}")
                VideoHook.handleLoadPackage(lpparam)
            }
            else -> {
                //CommonSkipSplashAd.handleLoadPackage(lpparam)
            }
        }
    }

}