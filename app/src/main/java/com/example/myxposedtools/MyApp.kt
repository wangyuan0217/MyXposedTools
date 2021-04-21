package com.example.myxposedtools

import android.app.Application

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/22
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    companion object {

        lateinit var application: MyApp

    }

}