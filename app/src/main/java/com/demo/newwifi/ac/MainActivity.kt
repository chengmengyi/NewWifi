package com.demo.newwifi.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.newwifi.R
import com.demo.newwifi.admob.AdLimitManager
import com.demo.newwifi.admob.LoadAdManager
import com.demo.newwifi.admob.ShowFullAd
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.conf.LocalConf
import com.demo.newwifi.dialog.LocationPermissionDialog
import com.demo.newwifi.uti.InstallTimeManager
import com.demo.newwifi.uti.hasLocationPermission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseAc(R.layout.activity_main) {
    private var launchAnimator: ValueAnimator?=null
    private val showOpenAd by lazy { ShowFullAd(LocalConf.OPEN) }

    override fun initView() {
        AdLimitManager.resetValue()
        LoadAdManager.preLoadAllAd()
        InstallTimeManager.writeTime()
        startAnimator()
    }

    private fun startAnimator(){
        launchAnimator=ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                launch_progress.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if (pro in 2..9){
                    showOpenAd.show(
                        this@MainActivity,
                        showed = {
                            stopAnimator()
                            launch_progress.progress=100
                        },
                        closeAd = {
                            jumpHome()
                        }
                    )
                }else if (pro>=10){
                    jumpHome()
                }
            }
            start()
        }
    }

    private fun hasPermission(){
        if(hasLocationPermission()){
            jumpHome()
        }else{
            LocationPermissionDialog{
                if(it){
                    jumpHome()
                }else{
                    finish()
                }
            }.show(supportFragmentManager,"LocationDialog")
        }
    }

    private fun jumpHome(){
        if (!ActivityUtils.isActivityExistsInStack(HomeAc::class.java)){
            startActivity(Intent(this,HomeAc::class.java))
        }
        finish()
    }

    private fun stopAnimator(){
        launchAnimator?.removeAllUpdateListeners()
        launchAnimator?.cancel()
        launchAnimator=null
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        launchAnimator?.resume()
    }

    override fun onPause() {
        super.onPause()
        launchAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
    }
}