package com.example.myxposedtools.xp

import android.app.Activity
import android.app.Application
import android.content.pm.ShortcutManager
import android.os.Build
import com.example.myxposedtools.prefs.XPrefsUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
object XiaoMiShopHook : AbsHook() {

    override val TAG = "tag_xiaomi_shop"

    override fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {
        removeShortcut()
        skipSplashAd()
    }

    /**
     * 移除长按图标的菜单
     */
    private fun removeShortcut() {
        try {
            log("removeShortcut start")
            if (Build.VERSION.SDK_INT >= 25) {
                XposedHelpers.findAndHookMethod(
                    ShortcutManager::class.java, "setDynamicShortcuts", List::class.java,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val shortcutInfoList = param.args[0] as MutableList<*>
                            shortcutInfoList.clear()
                            param.args[0] = shortcutInfoList
                            log("removeShortcut success")
                        }
                    })
            }
        } catch (t: Throwable) {
            log("removeShortcut fail: ${t.getStackInfo()}")
        }
    }

    /**
     * 移除启动页广告
     */
    private fun skipSplashAd() {
        try {
            log("skipSplashAd start")
            val utilsClass = XposedHelpers.findClass("com.xiaomi.shop.utils.SplashCommonUtils", classLoader)
            //跳过首页的方法
            val autoJumpMethod = XposedHelpers.findMethodExact(
                utilsClass, "autoJump", Activity::class.java, String::class.java, Int::class.java
            )
            //让 appCanAutoJump 方法永远返回 true
            XposedHelpers.findAndHookMethod(utilsClass, "appCanAutoJump", XC_MethodReplacement.returnConstant(true))

            val presenterClass = XposedHelpers.findClass("com.xiaomi.shop.presenter.SplashPresenter", classLoader)
            val activityField = XposedHelpers.findField(presenterClass, "mActivity")
            XposedHelpers.findAndHookMethod(presenterClass, "displaySplashView", object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val activity = activityField.get(param.thisObject) as Activity
                    autoJumpMethod.invoke(null, activity, null, 1)
                    param.result = null
                    log("skipSplashAd success")
                    if (XPrefsUtils.isSkipAdToastEnabled()) {
                        showToast(application, "已成功为您跳过启动页广告")
                    }
                }
            })
        } catch (t: Throwable) {
            log("skipSplashAd fail: ${t.getStackInfo()}")
        }
    }

}