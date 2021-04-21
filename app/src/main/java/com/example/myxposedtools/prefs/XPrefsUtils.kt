package com.example.myxposedtools.prefs

import de.robv.android.xposed.XSharedPreferences


/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/22
 */
object XPrefsUtils : BasePrefs() {

    private var xSharedPreferences: XSharedPreferences? = null

    private fun getPrefs(): XSharedPreferences? {
        var xPrefs = xSharedPreferences
        if (xPrefs != null) {
            xPrefs.reload()
            return xPrefs
        }
        xPrefs = XSharedPreferences(PKG_NAME, PREFS_NAME)
        return if (xPrefs.file.canRead()) {
            xSharedPreferences = xPrefs
            xPrefs
        } else {
            null
        }
    }

    /**
     * 去除广告是否弹Toast
     */
    fun isSkipAdToastEnabled(): Boolean {
        return getPrefs()?.getBoolean(SKIP_AD_TOAST_ENABLED, false) ?: false
    }

}