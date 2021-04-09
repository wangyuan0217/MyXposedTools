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
object XiaoMiMarketHook : AbsHook() {

    override val TAG = "tag_xiaomi_market"

    override fun onAppStarted(application: Application, classLoader: ClassLoader) {
        skipSplashAd()
    }

    /**
     * 移除启动页广告
     */
    private fun skipSplashAd() {
        try {
            log("skipSplashAd start")
            XposedHelpers.findAndHookMethod(
                "com.xiaomi.market.ui.BaseActivity",
                classLoader,
                "needShowSplash",
                Activity::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        log("skipSplashAd success")
                        return false
                    }
                }
            )
        } catch (t: Throwable) {
            log("skipSplashAd fail: ${t.getStackInfo()}")
        }
    }

}