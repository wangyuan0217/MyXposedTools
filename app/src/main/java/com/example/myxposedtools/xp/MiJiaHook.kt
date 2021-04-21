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
object MiJiaHook : AbsHook() {

    override val TAG = "tag_mijia"

    override fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {
        removeHomePageAd()
        removeMinePageAd()
    }

    /**
     * 移除米家首页 Banner 广告
     */
    private fun removeHomePageAd() {
        try {
            log("removeHomePageAd start")
            XposedHelpers.findAndHookMethod(
                "_m_j.hdy", classLoader, "O000000o", String::class.java, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = emptyList<Any>()
                        log("removeHomePageAd success")
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            showToast(application, "已成功为您去除广告")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeHomePageAd fail: ${t.getStackInfo()}")
        }
    }

    /**
     * 移除我的页面广告
     */
    private fun removeMinePageAd() {
        try {
            log("removeMinePageAd start")
            XposedHelpers.findAndHookMethod(
                "_m_j.hea", classLoader, "O000000o", String::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = emptyList<Any>()
                        log("removeMinePageAd success")
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            showToast(application, "已成功为您去除广告")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeMinePageAd fail: ${t.getStackInfo()}")
        }
    }

}