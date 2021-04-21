package com.example.myxposedtools.xp

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
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

    lateinit var handler: Handler

    open val TAG: String = "tag_hook"

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        onHandleLoadPackage(lpparam)
        callApplicationCreate(lpparam)
    }

    private fun callApplicationCreate(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName
        try {
            XposedHelpers.findAndHookMethod(
                Application::class.java, "onCreate", object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        application = param.thisObject as Application
                        classLoader = application.classLoader!!
                        handler = Handler(Looper.getMainLooper())
                        onApplicationCreate(application, classLoader)
                        if (HookUtils.getProcessName(application) == packageName) {
                            log("onMainApplicationCreate: $packageName")
                            onMainApplicationCreate(application, classLoader)
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("callApplicationCreate fail: $packageName")
            log(t.getStackInfo())
        }
    }

    open fun onHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

    }

    open fun onApplicationCreate(application: Application, classLoader: ClassLoader) {

    }

    open fun onMainApplicationCreate(application: Application, classLoader: ClassLoader) {

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

    fun runOnUIThread(runnable: Runnable) {
        handler.post(runnable)
    }

    fun showToast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
        handler.post {
            Toast.makeText(context, msg, duration).show()
        }
    }

}