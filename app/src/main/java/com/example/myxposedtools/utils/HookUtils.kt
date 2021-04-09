package com.example.myxposedtools.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import de.robv.android.xposed.XposedBridge
import java.io.File
import java.lang.reflect.Field

/**
 * @author: wangpan
 * @emial: p.wang@aftership.com
 * @date: 2021/4/9
 */
object HookUtils {

    fun log(content: Any?) {
        log("tag_hook", content)
    }

    fun log(tag: String, content: Any?) {
        XposedBridge.log("$tag: $content")
    }

    fun getProcessName(context: Context): String {
        val pid = Process.myPid()
        return (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.let { am ->
            val processes = am.runningAppProcesses ?: emptyList()
            var result = ""
            for (process in processes) {
                if (process.pid == pid) {
                    result = process.processName
                    break
                }
            }
            result
        } ?: ""
    }

    fun getStackInfo(t: Throwable): String {
        val sb = StringBuilder(t.toString())
        for (s in t.stackTrace) {
            sb.append("\n").append(s.toString())
        }
        return sb.toString()
    }

    fun Field.getValueSafety(obj: Any): Any? {
        return try {
            this.isAccessible = true
            this.get(obj)
        } catch (t: Throwable) {
            null
        }
    }

    fun Any?.getFieldsParams(): String {
        if (this == null) {
            return ""
        }
        val fields = this.javaClass.declaredFields
        if (fields.isEmpty()) {
            return ""
        }
        val params = StringBuilder()
        for (field in fields) {
            val value = field.getValueSafety(this)
            params.append("${field.name}=$value, ")
        }
        return params.toString()
    }

    fun deleteFile(file: File) {
        if (!file.exists()) {
            return
        }
        if (file.isDirectory) {
            val listFiles = file.listFiles() ?: emptyArray()
            for (childFile in listFiles) {
                deleteFile(childFile)
            }
        } else {
            file.delete()
        }
    }

}