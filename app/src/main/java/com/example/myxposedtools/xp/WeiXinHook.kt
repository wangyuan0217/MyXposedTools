package com.example.myxposedtools.xp

import android.app.Application
import android.view.View
import android.view.ViewGroup
import com.example.myxposedtools.prefs.XPrefsUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
object WeiXinHook : AbsHook() {

    override val TAG = "tag_weixin"

    override fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {
        removePYQAdItems()
    }

    /**
     * 移除朋友圈广告
     */
    private fun removePYQAdItems() {
        try {
            log("removePYQAdItems start")
            XposedHelpers.findAndHookMethod(
                "com.tencent.mm.plugin.sns.ui.a.c", classLoader,
                "getView", Int::class.java, View::class.java, ViewGroup::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val position = param.args[0] as Int
                        val parent = param.args[2] as ViewGroup
                        if (isAdItems(param.thisObject, position)) {
                            param.result = View(parent.context).apply {
                                layoutParams = ViewGroup.LayoutParams(0, 0)
                            }
                            log("removePYQAdItems success: $position")
                            if (XPrefsUtils.isSkipAdToastEnabled()) {
                                showToast(application, "已成功为您去除一个朋友圈广告")
                            }
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removePYQAdItems fail: ${t.getStackInfo()}")
        }
    }

    private fun isAdItems(adapter: Any, position: Int): Boolean {
        try {
            val snsInfo = XposedHelpers.findMethodExact(adapter.javaClass, "adF", Int::class.java)
                .invoke(adapter, position) ?: return false
            return XposedHelpers.findField(snsInfo.javaClass, "adsnsinfo").get(snsInfo) != null
        } catch (t: Throwable) {
            log("isAdItems fail: ${t.getStackInfo()}")
        }
        return false
    }

}