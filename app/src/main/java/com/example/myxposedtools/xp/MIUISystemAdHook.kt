package com.example.myxposedtools.xp

import android.app.Application
import com.example.myxposedtools.prefs.XPrefsUtils
import com.example.myxposedtools.utils.HookUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/23
 */
object MIUISystemAdHook : AbsHook() {

    override val TAG = "tag_system_ad"

    override fun onHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(Application::class.java, "onCreate", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val application = param.thisObject as Application
                    log("systemAdService start: ${HookUtils.getProcessName(application)}")
                    removeSplashAd()
                }
            })
        } catch (t: Throwable) {
            log("systemAdService fail: ${t.getStackInfo()}")
        }
    }

    /**
     * 移除闪屏页广告
     */
    private fun removeSplashAd() {
        try {
            val binderClass = XposedHelpers.findClassIfExists(
                "com.miui.systemAdSolution.splashAd.SystemSplashAdService\$2", classLoader
            )
            val adListenerClass = XposedHelpers.findClassIfExists(
                "com.miui.systemAdSolution.splashAd.IAdListener", classLoader
            )
            if (binderClass == null || adListenerClass == null) {
                log("removeSplashAd cancel: binderClass: $binderClass, adListenerClass: $adListenerClass")
                return
            }
            log("removeSplashAd start")
            XposedHelpers.findAndHookMethod(
                binderClass, "requestSplashAd", String::class.java, adListenerClass, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val packageName = param.args[0] as String
                        log("requestSplashAd packageName: $packageName")
                        cancelSplashAd(binderClass, param.thisObject, packageName)
                        param.result = true
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            runOnUIThread { showToast(application, "已成功为您去除启动页广告") }
                        }
                    }

                    private fun cancelSplashAd(binderClass: Class<*>, binder: Any, packageName: String) {
                        try {
                            XposedHelpers.findMethodExact(
                                binderClass, "cancelSplashAd", String::class.java
                            ).invoke(binder, packageName)
                        } catch (t: Throwable) {
                            log("cancelSplashAd fail: ${t.getStackInfo()}")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeSplashAd fail: ${t.getStackInfo()}")
        }
    }

}