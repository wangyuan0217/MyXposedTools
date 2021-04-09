package com.example.myxposedtools.xp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import com.example.myxposedtools.utils.HookUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/15
 */
object CommonSkipSplashAd {

    const val TAG = "tag_common"

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        HookUtils.log(TAG, "packageName: ${lpparam.packageName}, processName: ${lpparam.processName}")
        //如果不是主进程就跳过
        if (lpparam.processName != lpparam.packageName) return

        XposedHelpers.findAndHookMethod(Application::class.java, "onCreate", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                HookUtils.log(TAG, "onAppStarted: ${lpparam.packageName}")
                onAppStarted(lpparam.packageName)
            }
        })
    }

    private val activeActivitySet: MutableSet<Activity> = mutableSetOf()

    private fun onAppStarted(packageName: String) {
        XposedHelpers.findAndHookMethod(
            Activity::class.java, "attachBaseContext", Context::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    onActivityCreate(param.thisObject as Activity, packageName)
                }
            })


//        XposedHelpers.findAndHookMethod(Activity::class.java, "onStart", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam) {
//                val activity = param.thisObject as Activity
//                activeActivitySet.add(activity)
//                HookUtils.log(TAG, "packageName: $packageName, activity onStart: $activity")
//            }
//        })
//        XposedHelpers.findAndHookMethod(Activity::class.java, "onStop", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam) {
//                val activity = param.thisObject as Activity
//                activeActivitySet.remove(activity)
//                HookUtils.log(TAG, "packageName: $packageName, activity onStop: $activity")
//            }
//        })
//        XposedHelpers.findAndHookMethod(
//            Activity::class.java,
//            "dispatchTouchEvent",
//            MotionEvent::class.java,
//            object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam) {
////                    val motionEvent = param.args[0] as? MotionEvent ?: return
////                    val activity = param.thisObject as? Activity ?: return
////                    if (motionEvent.action == MotionEvent.ACTION_UP) {
////
////                    }
//                    HookUtils.log(
//                        TAG,
//                        "Activity dispatchTouchEvent packageName: $packageName, activity: ${param.thisObject}, dispatchTouchEvent: ${param.args[0]}"
//                    )
//                }
//            })
//        XposedHelpers.findAndHookMethod(
//            View.OnClickListener::class.java,
//            "onClick",
//            View::class.java,
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    val clickView = param.args[0] as View? ?: return
////                    val activity = clickView.context as? Activity ?: return
////                    val isAdView = isAdView(clickView)
//                    HookUtils.log(
//                        TAG,
//                        "View onClick packageName: $packageName, clickView: $clickView"
//                    )
//                }
//            })
//
//        XposedHelpers.findAndHookMethod(
//            ViewGroup::class.java, "dispatchTouchEvent", MotionEvent::class.java, object : XC_MethodHook() {
//                override fun beforeHookedMethod(param: MethodHookParam) {
////                    val motionEvent = param.args[0] as? MotionEvent ?: return
////                    val activity = (param.thisObject as ViewGroup).context as? Activity ?: return
////                    if (motionEvent.action == MotionEvent.ACTION_UP) {
////                        HookUtils.log(
////                            TAG,
////                            "ViewGroup dispatchTouchEvent packageName: $packageName, activity: $activity, dispatchTouchEvent: $motionEvent"
////                        )
////                    }
//                    val viewGroup = param.thisObject as ViewGroup
//                    HookUtils.log(
//                        TAG,
//                        "ViewGroup dispatchTouchEvent packageName: $packageName, viewGroup: $viewGroup"
//                    )
//                }
//            }
//        )
    }

    private fun onActivityCreate(activity: Activity, packageName: String) {


//        val dispatchTouchEventMethod =
//            XposedHelpers.findMethodExactIfExists(activity::class.java, "dispatchTouchEvent", MotionEvent::class.java)
//        HookUtils.log(
//            TAG,
//            "dispatchTouchEventMethod: $dispatchTouchEventMethod, packageName: $packageName, activity: $activity"
//        )
//        if (dispatchTouchEventMethod != null) {
//            XposedHelpers.findAndHookMethod(
//                activity.javaClass, "dispatchTouchEvent", MotionEvent::class.java,
//                object : XC_MethodHook() {
//                    override fun afterHookedMethod(param: MethodHookParam) {
//                        HookUtils.log(
//                            TAG,
//                            "Activity dispatchTouchEvent packageName: $packageName, activity: $activity, dispatchTouchEvent: ${param.args[0]}"
//                        )
//                    }
//                })
//        }
    }

    private fun isAdView(view: View): Boolean {
        //todo
        return false
    }

}