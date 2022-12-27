package com.demo.newwifi.uti

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.newwifi.ac.network_test.NetTestAc
import com.demo.newwifi.ac.security.SecurityAc
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ActivityCallback {
    var isFront=true

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(callback)
    }

    private val callback=object : Application.ActivityLifecycleCallbacks{
        private var pages=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            pages++
            if (pages==1){
                isFront=true
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            pages--
            if (pages<=0){
                isFront=false
                ActivityUtils.finishActivity(SecurityAc::class.java)
                ActivityUtils.finishActivity(NetTestAc::class.java)
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}