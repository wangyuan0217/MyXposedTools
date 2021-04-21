package com.example.myxposedtools.prefs

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/22
 */
open class BasePrefs {

    companion object {

        const val PKG_NAME = "com.example.myxposedtools"

        const val PREFS_NAME = "my_xposed_tools_sp"

        /**
         * 去除广告是否弹Toast
         */
        const val SKIP_AD_TOAST_ENABLED = "skip_ad_toast_enabled"
    }

}