package com.demo.newwifi.admob

import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.uti.log
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd

class ShowFullAd(private val type:String) {

    fun show(baseAc: BaseAc,adEmptyBack:Boolean=false,showed:()->Unit,closeAd:()->Unit){
        val ad = LoadAdManager.getAd(type)
        if (null!=ad){
            if (LoadAdManager.fullAdShowing||!baseAc.resume){
                return
            }
            "show full ad $type".log()
            showed.invoke()
            when(ad){
                is InterstitialAd ->{
                    showInterstitial(baseAc,ad,closeAd)
                }
                is AppOpenAd ->{
                    showOpen(baseAc,ad,closeAd)
                }
            }
        }else{
            if (adEmptyBack){
                closeAd.invoke()
            }
        }
    }

    private fun showOpen(baseAc: BaseAc,ad : AppOpenAd,closeAd:()->Unit){
        ad.fullScreenContentCallback=FullAdCallBack(baseAc,type,closeAd)
        ad.show(baseAc)
    }

    private fun showInterstitial(baseAc: BaseAc,ad : InterstitialAd,closeAd:()->Unit){
        ad.fullScreenContentCallback=FullAdCallBack(baseAc,type,closeAd)
        ad.show(baseAc)
    }

}