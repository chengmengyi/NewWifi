package com.demo.newwifi.admob

import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.conf.LocalConf
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullAdCallBack(
    private val baseAc: BaseAc,
    private val type:String,
    private val closeAd:()->Unit
): FullScreenContentCallback() {
    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        LoadAdManager.fullAdShowing=false
        showFinish()
    }

    override fun onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent()
        LoadAdManager.fullAdShowing=true
        AdLimitManager.addShow()
        LoadAdManager.removeAd(type)
    }

    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        super.onAdFailedToShowFullScreenContent(p0)
        LoadAdManager.fullAdShowing=false
        LoadAdManager.removeAd(type)
        showFinish()
    }


    override fun onAdClicked() {
        super.onAdClicked()
        AdLimitManager.addClick()
    }

    private fun showFinish(){
        if (type!= LocalConf.OPEN){
            LoadAdManager.load(type)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (baseAc.resume){
                closeAd.invoke()
            }
        }
    }
}