package com.demo.newwifi.admob

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.newwifi.R
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.base.BaseDialog
import com.demo.newwifi.conf.LocalConf
import com.demo.newwifi.uti.showView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAd(private val type:String) {
    private var lastAd: NativeAd?=null
    private var job: Job?=null
    private val showCover = arrayListOf(LocalConf.TEST_RESULT)

    fun showAc(baseAc: BaseAc){
        LoadAdManager.load(type)
        stopShow()
        job= GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            if (!baseAc.resume){
                return@launch
            }
            while (true) {
                if (!isActive) {
                    break
                }

                val ad = LoadAdManager.getAd(type)
                if(baseAc.resume && null!=ad && ad is NativeAd){
                    cancel()
                    lastAd?.destroy()
                    lastAd=ad
                    showAd(baseAc,ad)
                }

                delay(1000L)
            }
        }
    }

    fun showDialog(baseDialog: BaseDialog,view:View){
        LoadAdManager.load(type)
        stopShow()
        job= GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            if (!baseDialog.resume){
                return@launch
            }
            while (true) {
                if (!isActive) {
                    break
                }

                val ad = LoadAdManager.getAd(type)
                if(baseDialog.resume && null!=ad && ad is NativeAd){
                    cancel()
                    lastAd?.destroy()
                    lastAd=ad
                    showAdDialog(view,ad)
                }

                delay(1000L)
            }
        }
    }

    private fun showAd(baseAc: BaseAc,ad: NativeAd){
        val viewNative = baseAc.findViewById<NativeAdView>(R.id.native_ad)
        viewNative.iconView=baseAc.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        viewNative.callToActionView=baseAc.findViewById(R.id.native_install)
        (viewNative.callToActionView as AppCompatTextView).text=ad.callToAction

        if(showCover.contains(type)){
            viewNative.mediaView=baseAc.findViewById(R.id.native_cover)
            ad.mediaContent?.let {
                viewNative.mediaView?.apply {
                    setMediaContent(it)
                    setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null || outline == null) return
                            outline.setRoundRect(
                                0,
                                0,
                                view.width,
                                view.height,
                                SizeUtils.dp2px(8F).toFloat()
                            )
                            view.clipToOutline = true
                        }
                    }
                }
            }
        }

        viewNative.bodyView=baseAc.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=ad.body

        viewNative.headlineView=baseAc.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=ad.headline

        viewNative.setNativeAd(ad)
        baseAc.findViewById<AppCompatImageView>(R.id.native_default).showView(false)
        showAdFinish()
    }

    private fun showAdDialog(view:View,ad: NativeAd){
        val viewNative = view.findViewById<NativeAdView>(R.id.native_ad)
        viewNative.iconView=view.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        viewNative.callToActionView=view.findViewById(R.id.native_install)
        (viewNative.callToActionView as AppCompatTextView).text=ad.callToAction

        viewNative.bodyView=view.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=ad.body

        viewNative.headlineView=view.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=ad.headline

        viewNative.setNativeAd(ad)
        view.findViewById<AppCompatImageView>(R.id.native_default).showView(false)
        showAdFinish()
    }

    private fun showAdFinish(){
        AdLimitManager.addShow()
        LoadAdManager.removeAd(type)
        LoadAdManager.load(type)
        AdLimitManager.setValue(type,false)
    }

    fun stopShow(){
        job?.cancel()
        job=null
    }
}