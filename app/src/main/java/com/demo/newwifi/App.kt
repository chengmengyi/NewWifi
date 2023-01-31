package com.demo.newwifi

import android.app.Application
import com.demo.newwifi.conf.FireConf
import com.demo.newwifi.uti.ActivityCallback
import com.tencent.mmkv.MMKV
import org.litepal.LitePal


lateinit var app:App
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        app=this
        MMKV.initialize(this)
        LitePal.initialize(this)
        ActivityCallback.register(this)
        FireConf.readFireConf()
    }
}