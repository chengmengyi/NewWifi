package com.demo.newwifi

import android.app.Application
import com.demo.newwifi.uti.ActivityCallback
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV
import org.litepal.LitePal

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        MMKV.initialize(this)
        LitePal.initialize(this)
        ActivityCallback.register(this)
    }
}