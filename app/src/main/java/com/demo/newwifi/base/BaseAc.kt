package com.demo.newwifi.base

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import org.greenrobot.eventbus.EventBus

abstract class BaseAc (private val layout:Int):AppCompatActivity(){
    lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMetrics()
        setContentView(layout)
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(true)
            init()
        }
        if(initEvent()){
            EventBus.getDefault().register(this)
        }
        initView()
    }

    abstract fun initView()

    open fun initEvent()=false

    private fun displayMetrics(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    override fun onDestroy() {
        super.onDestroy()
        if(initEvent()){
            EventBus.getDefault().unregister(this)
        }
    }
}