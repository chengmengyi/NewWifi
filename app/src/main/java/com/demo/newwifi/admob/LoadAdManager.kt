package com.demo.newwifi.admob

import com.demo.newwifi.app
import com.demo.newwifi.bean.AdDataBean
import com.demo.newwifi.bean.AdMapBean
import com.demo.newwifi.conf.FireConf
import com.demo.newwifi.conf.LocalConf
import com.demo.newwifi.uti.log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.json.JSONObject

object LoadAdManager {
    var fullAdShowing=false
    private val loadingList= arrayListOf<String>()
    private val adMap= hashMapOf<String,AdMapBean>()

    fun load(type:String,loadOpen:Boolean=true){
        if (AdLimitManager.limit()){
            "limit".log()
            return
        }
        if (loadingList.contains(type)){
            "$type is loading".log()
            return
        }
        if(adMap.containsKey(type)){
            val adBean = adMap[type]
            if (null!=adBean?.ad){
                if(adBean.expired()){
                    removeAd(type)
                }else{
                    "$type has cache".log()
                    return
                }
            }
        }
        val adDataList = getAdDataList(type)
        if (adDataList.isEmpty()){
            "$type list info is empty".log()
            return
        }
        loadingList.add(type)
        loopLoad(type,adDataList.iterator(),loadOpen)
    }


    private fun loopLoad(type: String, iterator: Iterator<AdDataBean>, loadOpen: Boolean){
        loadAdByType(type,iterator.next()){
            if (null==it){
                if (iterator.hasNext()){
                    loopLoad(type,iterator,loadOpen)
                }else{
                    loadingList.remove(type)
                    if (type== LocalConf.OPEN&&loadOpen){
                        load(type,loadOpen = false)
                    }
                }
            }else{
                loadingList.remove(type)
                adMap[type]=it
            }
        }
    }

    private fun loadAdByType(type: String,adDataBean: AdDataBean,result:(bean:AdMapBean?)->Unit){
        "start load $type,${adDataBean.toString()}".log()
        when(adDataBean.netG_type){
            "open"-> loadOpen(type, adDataBean, result)
            "inter"-> loadInterstitial(type, adDataBean, result)
            "native"-> loadNative(type, adDataBean, result)
        }
    }

    private fun loadOpen(type:String,adDataBean: AdDataBean,result:(bean:AdMapBean?)->Unit){
        AppOpenAd.load(
            app,
            adDataBean.netG_id,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback(){
                override fun onAdLoaded(p0: AppOpenAd) {
                    "load $type ad success".log()
                    result.invoke(AdMapBean(System.currentTimeMillis(),p0))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    "load $type fail,${p0.message}".log()
                    result.invoke(null)
                }
            }
        )
    }

    private fun loadInterstitial(type:String,adDataBean: AdDataBean,result:(bean:AdMapBean?)->Unit){
        InterstitialAd.load(
            app,
            adDataBean.netG_id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    "load $type fail,${p0.message}".log()
                    result.invoke(null)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    "load $type ad success".log()
                    result.invoke(AdMapBean(System.currentTimeMillis(),p0))
                }
            }
        )
    }

    private fun loadNative(type:String,adDataBean: AdDataBean,result:(bean:AdMapBean?)->Unit) {
        AdLoader.Builder(
            app,
            adDataBean.netG_id,
        ).forNativeAd {
            "load $type ad success".log()
            result.invoke(AdMapBean(System.currentTimeMillis(),it))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    "load $type fail,${p0.message}".log()
                    result.invoke(null)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    AdLimitManager.addClick()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_BOTTOM_LEFT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    private fun getAdDataList(type: String):List<AdDataBean>{
        val list= arrayListOf<AdDataBean>()
        try {
            val jsonArray = JSONObject(FireConf.getAdJson()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    AdDataBean(
                        jsonObject.optString("netG_id"),
                        jsonObject.optString("netG_source"),
                        jsonObject.optString("netG_type"),
                        jsonObject.optInt("netG_priority"),
                    )
                )
            }
        }catch (e:Exception){
        }
        return list.filter { it.netG_source == "admob" }.sortedByDescending { it.netG_priority }
    }

    fun getAd(type: String)= adMap[type]?.ad

    fun removeAd(type: String){
        adMap.remove(type)
    }

    fun preLoadAllAd(){
        load(LocalConf.OPEN)
        load(LocalConf.HOME)
        load(LocalConf.CONNECT_WIFI_DIALOG)
        load(LocalConf.TEST_INTER)
        load(LocalConf.CONNECT_WIFI)
        load(LocalConf.TEST_RESULT)
    }
}