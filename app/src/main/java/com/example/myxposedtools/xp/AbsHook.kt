package com.example.myxposedtools.xp

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.myxposedtools.utils.HookUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
abstract class AbsHook {

    lateinit var application: Application
    lateinit var classLoader: ClassLoader

    open val TAG: String = "tag_hook"

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        onHandleLoadPackage(lpparam)
        callOnAppStart(lpparam)
    }

    private fun callOnAppStart(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.processName != lpparam.packageName) return
        try {
            XposedHelpers.findAndHookMethod(
                Application::class.java, "onCreate", object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        application = param.thisObject as Application
                        classLoader = application.classLoader
                        log("onAppStart: ${lpparam.packageName}")
                        onAppStarted(application, classLoader)
                    }
                }
            )
        } catch (t: Throwable) {
            log("onAppStart fail: ${lpparam.packageName}")
            log(t.getStackInfo())
        }
    }

    open fun onAppStarted(application: Application, classLoader: ClassLoader) {

    }

    open fun onHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

    }

    open fun onAppStart(lpparam: XC_LoadPackage.LoadPackageParam) {

    }

    fun log(content: Any?) {
        HookUtils.log(TAG, content)
    }

    fun Throwable.getStackInfo(): String = HookUtils.getStackInfo(this)

    fun printStackInfo() {
        try {
            throw Exception()
        } catch (e: Exception) {
            log(e.getStackInfo())
        }
    }

    fun showToast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, msg, duration).show()
    }

}