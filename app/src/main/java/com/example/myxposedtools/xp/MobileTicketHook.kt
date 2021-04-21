package com.example.myxposedtools.xp

import android.app.Application
import com.example.myxposedtools.prefs.XPrefsUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/21
 */
object MobileTicketHook : AbsHook() {

    override val TAG = "tag_12306"

    override fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {
        removeMainPageAd()
        removePopupAd()
    }

    /**
     * 移除首页全屏广告
     */
    private fun removeMainPageAd() {
        try {
            log("removeMainPageAd start")
            XposedHelpers.findAndHookMethod(
                "com.bontai.mobiads.ads.splash.SplashAdView", classLoader, "isNeedShowAd",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = false
                        log("removeMainPageAd success")
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            showToast(application, "已成功为您跳过启动页广告")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeMainPageAd fail: ${t.getStackInfo()}")
        }
    }

    /**
     * 移除首页弹窗广告
     */
    private fun removePopupAd() {
        try {
            log("removePopupAd start")
            XposedHelpers.findAndHookMethod(
                "com.MobileTicket.netrequest.AdPopUpRequest", classLoader, "requestAdPopUp",
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = null
                        log("removePopupAd success")
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            showToast(application, "已成功为您去除首页弹窗广告")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removePopupAd fail: ${t.getStackInfo()}")
        }
    }

}