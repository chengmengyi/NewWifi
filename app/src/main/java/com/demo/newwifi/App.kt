package com.demo.newwifi

import android.app.Application
import com.demo.newwifi.uti.ActivityCallback
import com.tencent.mmkv.MMKV
import org.litepal.LitePal

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        LitePal.initialize(this)
        ActivityCallback.register(this)
    }
}