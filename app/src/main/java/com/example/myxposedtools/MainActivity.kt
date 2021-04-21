package com.example.myxposedtools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myxposedtools.databinding.ActivityMainBinding
import com.example.myxposedtools.prefs.PrefsUtils

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.run {
            skipAdToastSc.isChecked = PrefsUtils.isSkipAdToastEnabled()
            skipAdToastRl.setOnClickListener {
                val newValue = !PrefsUtils.isSkipAdToastEnabled()
                PrefsUtils.setSkipAdToastEnabled(newValue)
                skipAdToastSc.isChecked = newValue
            }
        }
    }

}