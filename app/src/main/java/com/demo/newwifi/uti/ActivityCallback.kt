package com.demo.newwifi.uti

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.newwifi.ac.HomeAc
import com.demo.newwifi.ac.MainActivity
import com.demo.newwifi.ac.network_test.NetTestAc
import com.demo.newwifi.ac.security.SecurityAc
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ActivityCallback {
    var banReload=false
    var isFront=true

    private var jumpToMain=false
    private var job: Job?=null

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(callback)
    }

    private val callback=object : Application.ActivityLifecycleCallbacks{
        private var pages=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            pages++
            job?.cancel()
            job=null
            if (pages==1){
                isFront=true
                if (jumpToMain&&!banReload){
                    if (ActivityUtils.isActivityExistsInStack(HomeAc::class.java)){
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                    }
                }
                jumpToMain=false
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
                job= GlobalScope.launch {
                    delay(3000L)
                    jumpToMain=true
                    ActivityUtils.finishActivity(MainActivity::class.java)
                    ActivityUtils.finishActivity(AdActivity::class.java)
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}