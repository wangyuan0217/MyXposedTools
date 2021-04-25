package com.example.myxposedtools.xp

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ActivityInfo
import com.example.myxposedtools.prefs.XPrefsUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/23
 */
object MIUIMsaHook : AbsHook() {

    override val TAG = "tag_msa"

    override fun onHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            XposedHelpers.findAndHookMethod(Application::class.java, "onCreate", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val application = param.thisObject as Application
                    val classLoader = application.classLoader!!
                    removeSplashAd(application, classLoader)
//                    removeFloatingAd(application, classLoader)
//                    removeSplashScreen(application, classLoader)
//                    removeSplashScreen2(application, classLoader)
                    removeSplashUI(application, classLoader)
                }
            })
        } catch (t: Throwable) {
            log("systemAdService fail: ${t.getStackInfo()}")
        }
    }

    /**
     * 移除闪屏页广告
     */
    private fun removeSplashAd(application: Application, classLoader: ClassLoader) {
        try {
            val binderClass = XposedHelpers.findClassIfExists(
                "com.miui.systemAdSolution.splashAd.SystemSplashAdService\$2", classLoader
            )
            val adListenerClass = XposedHelpers.findClassIfExists(
                "com.miui.systemAdSolution.splashAd.IAdListener", classLoader
            )
            if (binderClass == null || adListenerClass == null) {
                log("removeSplashAd cancel: $binderClass, $adListenerClass")
                return
            }
            log("removeSplashAd start")
            XposedHelpers.findAndHookMethod(
                binderClass, "requestSplashAd", String::class.java, adListenerClass, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val packageName = param.args[0] as String
                        cancelSplashAd(binderClass, param.thisObject, packageName)
                        param.result = true
                        log("removeSplashAd success: packageName: $packageName")
                        if (XPrefsUtils.isSkipAdToastEnabled()) {
                            runOnUIThread { showToast(application, "已成功为您去除启动页广告") }
                        }
                    }

                    private fun cancelSplashAd(binderClass: Class<*>, binder: Any, packageName: String) {
                        try {
                            XposedHelpers.findMethodExact(
                                binderClass, "cancelSplashAd", String::class.java
                            ).invoke(binder, packageName)
                            log("removeSplashAd cancelSplashAd success")
                        } catch (t: Throwable) {
                            log("removeSplashAd cancelSplashAd fail: ${t.getStackInfo()}")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeSplashAd fail: ${t.getStackInfo()}")
        }
    }

    private fun removeFloatingAd(application: Application, classLoader: ClassLoader) {
        try {
            val fadClass = XposedHelpers.findClassIfExists(
                "com.miui.zeus.msa.app.floating.FloatingAdController", classLoader
            ) ?: return
            log("removeFloatingAd start")
            XposedHelpers.findAndHookMethod(
                fadClass, "checkNeedShowAd", ComponentName::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        callDismissAd(param.thisObject)
                        param.result = true
                        log("removeFloatingAd checkNeedShowAd success")
                    }

                    private fun callDismissAd(facObj: Any) {
                        try {
                            XposedHelpers.findMethodExact(facObj.javaClass, "dismissAd", *emptyArray()).invoke(facObj)
                            log("removeFloatingAd dismissAd success")
                        } catch (t: Throwable) {
                            log("removeFloatingAd dismissAd fail: ${t.getStackInfo()}")
                        }
                    }
                })
            XposedHelpers.findAndHookMethod(
                fadClass, "delayCheckNeedShowAd", object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = null
                        log("removeFloatingAd delayCheckNeedShowAd success")
                    }
                })
        } catch (t: Throwable) {
            log("removeFloatingAd fail: ${t.getStackInfo()}")
        }
    }

    private fun removeSplashScreen(application: Application, classLoader: ClassLoader) {
        try {
            val binderClass = XposedHelpers.findClassIfExists(
                "com.miui.systemAdSolution.splashscreen.SplashScreenService\$1", classLoader
            )
            val listenerClass = XposedHelpers.findClassIfExists(
                "com.miui.server.ISplashPackageCheckListener", classLoader
            )
            if (binderClass == null || listenerClass == null) {
                log("removeSplashScreen cancel: binderClass: $binderClass, listenerClass: $listenerClass")
                return
            }
            log("removeSplashScreen start")
            XposedHelpers.findAndHookMethod(
                binderClass, "requestSplashScreen", Intent::class.java, ActivityInfo::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val intent = param.args[0] as Intent
                        param.result = intent
                        log("removeSplashScreen success: $intent")
                    }
                })
            XposedHelpers.findAndHookMethod(
                binderClass, "setSplashPackageListener", listenerClass,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = null
                        log("setSplashPackageListener success")
                    }
                })
        } catch (t: Throwable) {
            log("removeSplashScreen fail: ${t.getStackInfo()}")
        }
    }

    private fun removeSplashScreen2(application: Application, classLoader: ClassLoader) {
        try {
            val binderClass = XposedHelpers.findClassIfExists(
                "com.miui.systemAdSolution.splashscreen.SplashScreenServiceV2\$1", classLoader
            )
            val listenerClass = XposedHelpers.findClassIfExists(
                "com.miui.server.ISplashPackageCheckListener", classLoader
            )
            if (binderClass == null || listenerClass == null) {
                log("removeSplashScreen2 cancel: binderClass: $binderClass, listenerClass: $listenerClass")
                return
            }
            log("removeSplashScreen2 start")
            XposedHelpers.findAndHookMethod(
                binderClass, "requestSplashScreenBlocked", Intent::class.java, ActivityInfo::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val intent = param.args[0] as Intent
                        param.result = intent
                        log("requestSplashScreenBlocked success: $intent")
                    }
                })
            XposedHelpers.findAndHookMethod(
                binderClass, "setSplashPackageListenerNonBlocked", listenerClass,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        param.result = null
                        log("setSplashPackageListenerNonBlocked success")
                    }
                })
        } catch (t: Throwable) {
            log("removeSplashScreen2 fail: ${t.getStackInfo()}")
        }
    }

    private fun removeSplashUI(application: Application, classLoader: ClassLoader) {
        try {
            val sucClass = XposedHelpers.findClassIfExists(
                "com.miui.zeus.msa.app.splashad.SplashUIController", classLoader
            )
            if (sucClass == null) {
                log("removeSplashUI cancel: sucClass: $sucClass")
                return
            }
            log("removeSplashUI start")
            XposedHelpers.findAndHookMethod(
                sucClass, "show", String::class.java, String::class.java, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val s1 = param.args[0] as String
                        val s2 = param.args[1] as String
                        runOnUIThread { showAfter(param.thisObject) }
                        param.result = null
                        log("removeSplashUI show success: $s1, $s2")
                    }

                    private fun showAfter(sucObj: Any) {
                        try {
                            XposedHelpers.findMethodExact(
                                sucObj.javaClass, "notifyViewShown", *emptyArray()
                            ).invoke(sucObj)
                            XposedHelpers.findMethodExact(
                                sucClass.javaClass, "dismissView", *emptyArray()
                            ).invoke(sucObj)
                        } catch (t: Throwable) {
                            log("removeSplashUI showAfter success")
                        }
                    }
                }
            )
        } catch (t: Throwable) {
            log("removeSplashUI fail: ${t.getStackInfo()}")
        }
    }

}