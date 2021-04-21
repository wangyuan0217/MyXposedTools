package com.example.myxposedtools.xp

import android.app.Activity
import android.app.Application
import com.example.myxposedtools.prefs.XPrefsUtils
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
object XiaoMiMarketHook : AbsHook() {

    override val TAG = "tag_xiaomi_market"

    override fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {
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
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            showToast(application, "已成功为您跳过启动页广告")
                        }
                        return false
                    }
                }
            )
        } catch (t: Throwable) {
            log("skipSplashAd fail: ${t.getStackInfo()}")
        }
    }

}