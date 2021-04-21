package com.example.myxposedtools.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.myxposedtools.MyApp

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/22
 */
@SuppressLint("ApplySharedPref")
object PrefsUtils : BasePrefs() {

    private var sharedPreferences: SharedPreferences? = null

    @SuppressLint("WorldReadableFiles")
    private fun getPrefs(): SharedPreferences? {
        var prefs = sharedPreferences
        if (prefs != null) {
            return prefs
        }
        try {
            prefs = MyApp.application.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE)
            sharedPreferences = prefs
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return prefs
    }

    fun setSkipAdToastEnabled(enabled: Boolean) {
        getPrefs()?.run {
            edit().putBoolean(SKIP_AD_TOAST_ENABLED, enabled).commit()
        }
    }

    fun isSkipAdToastEnabled(): Boolean {
        return getPrefs()?.getBoolean(SKIP_AD_TOAST_ENABLED, false) ?: false
    }

}