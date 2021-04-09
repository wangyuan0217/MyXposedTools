package com.example.myxposedtools.xp

import android.app.Activity
import android.app.Application
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
object XunLeiHook : AbsHook() {

    override val TAG = "tag_xunlei"

    override fun onAppStarted(application: Application, classLoader: ClassLoader) {
        removeQuitDialog()
    }

    private fun removeQuitDialog() {
        try {
            log("removeQuitDialog start")
            XposedHelpers.findAndHookMethod(
                "com.xunlei.downloadprovider.frame.MainTabActivity",
                classLoader,
                "p",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any? {
                        val activity = param.thisObject as Activity
                        activity.finish()
                        activity.finishAffinity()
                        log("removeQuitDialog success")
                        return null
                    }
                })
        } catch (t: Throwable) {
            log("removeQuitDialog fail: ${t.getStackInfo()}")
        }
    }

}