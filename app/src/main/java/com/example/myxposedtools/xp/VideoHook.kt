package com.example.myxposedtools.xp

import android.app.Activity
import android.app.Application
import com.example.myxposedtools.prefs.XPrefsUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/25
 */
object VideoHook : AbsHook() {

    override val TAG = "tag_video"

    override fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {
        removeSplashAd(application, classLoader)
        hookGetAdAndSave(application, classLoader)
    }

    /**
     * 移除闪屏页广告
     */
    private fun removeSplashAd(application: Application, classLoader: ClassLoader) {
        try {
            val sfClass = XposedHelpers.findClassIfExists(
                "com.miui.video.core.feature.ad.splash.SplashFetcher", classLoader
            )
            val itemClass = XposedHelpers.findClassIfExists(
                "com.miui.video.common.launcher.SplashEntity\$Item", classLoader
            )
            if (sfClass == null || itemClass == null) {
                log("removeSplashAd cancel: $sfClass, $itemClass")
                return
            }
            log("removeSplashAd start")
            XposedHelpers.findAndHookMethod(
                sfClass, "launchSplash", Activity::class.java, itemClass, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val activity = param.args[0] as Activity
                        launchSplashInternal(activity)
                        param.result = null
                        log("removeSplashAd success")
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            runOnUIThread { showToast(application, "已成功为您去除启动页广告") }
                        }
                    }

                    private fun launchSplashInternal(activity: Activity) {
                        try {
                            XposedHelpers.findMethodExact(
                                sfClass, "notifySplashDismiss", Activity::class.java
                            ).invoke(null, activity)
                            XposedHelpers.findMethodExact(
                                sfClass, "removeDefaultSplash", Activity::class.java
                            ).invoke(null, activity)
                            log("removeSplashAd launchSplashInternal success")
                        } catch (t: Throwable) {
                            log("removeSplashAd launchSplashInternal fail: ${t.getStackInfo()}")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeSplashAd fail: ${t.getStackInfo()}")
        }
    }

    private fun hookGetAdAndSave(application: Application, classLoader: ClassLoader) {
        try {
            val sfClass = XposedHelpers.findClassIfExists(
                "com.miui.video.core.feature.ad.splash.SplashFetcher", classLoader
            )
            val callbackClass = XposedHelpers.findClassIfExists(
                "com.miui.video.core.feature.ad.splash.SplashFetcher\$FetchServerAdCallback", classLoader
            )
            if (sfClass == null || callbackClass == null) {
                log("hookGetAdAndSave cancel: $sfClass, $callbackClass")
                return
            }
            log("hookGetAdAndSave start")
            XposedHelpers.findAndHookMethod(
                sfClass, "getServerAdAndSave", callbackClass, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.args[0]?.let { callback ->
                            runOnUIThread { callOnError(callback) }
                        }
                        param.result = null
                        log("hookGetAdAndSave success")
                    }

                    private fun callOnError(callback: Any) {
                        try {
                            XposedHelpers.findMethodExact(
                                callback.javaClass, "onError", *emptyArray()
                            ).invoke(callback)
                            log("hookGetAdAndSave callOnError success")
                        } catch (t: Throwable) {
                            log("hookGetAdAndSave callOnError fail: ${t.getStackInfo()}")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("hookGetAdAndSave fail: ${t.getStackInfo()}")
        }
    }

}