package com.demo.newwifi.ac.security

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import com.demo.newwifi.R
import com.demo.newwifi.admob.LoadAdManager
import com.demo.newwifi.admob.ShowFullAd
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.conf.LocalConf
import com.demo.newwifi.uti.ActivityCallback
import com.demo.newwifi.uti.InstallTimeManager
import com.demo.newwifi.uti.WifiUtils
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.android.synthetic.main.activity_security.*
import kotlinx.coroutines.*

class SecurityAc:BaseAc(R.layout.activity_security) {
    private var scanTimeJob:Job?=null
    private var closeWifiJob:Job?=null
    private var downloadJob:Job?=null
    private val speedTestSocket = SpeedTestSocket()
    private var objectAnimator:ObjectAnimator?=null

    private val showFullAd by lazy { ShowFullAd(LocalConf.TEST_INTER) }

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_time.text="You have been protected from Internet Access for ${InstallTimeManager.getDays()} days"
        startAnimator()
        starScan()
        iv_back.setOnClickListener { finish() }
    }

    private fun starScan(){
        val instance = WifiUtils.getInstance(this)
        if (instance?.isConnectWifi()!=true){
            closeWifiJob=GlobalScope.launch {
                delay(2000L)
                withContext(Dispatchers.Main){
                    toSecurityResult(0L,"", "", "", "")
                }
            }
            return
        }
        val currentWifiName = instance.getConnectedWifiName()?:""
        val wifiIp = instance.getWifiIp()?:""
        val maxSpeed = instance.getMaxSpeed()?:""
        val wifiMac = instance.getWifiMac()?:""
        var speed=0L

        val listener = object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                speed = report.transferRateOctet.toLong()
                toSecurityResult(speed, currentWifiName, wifiIp, maxSpeed, wifiMac)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                toSecurityResult(speed, currentWifiName, wifiIp, maxSpeed, wifiMac)
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {

            }
        }
        speedTestSocket.addSpeedTestListener(listener)

        downloadJob=GlobalScope.launch {
            delay(2000L)
            speedTestSocket.startDownload("http://ipv4.appliwave.testdebit.info/5M/5M.zip")
        }

        scanTimeJob= GlobalScope.launch {
            delay(10000L)
            speedTestSocket.clearListeners()
            withContext(Dispatchers.Main){
                toSecurityResult(speed,currentWifiName, wifiIp, maxSpeed, wifiMac)
            }
        }
    }

    private fun toSecurityResult(transferRateOctet:Long,currentWifiName:String,wifiIp:String,maxSpeed:String,wifiMac:String){
        runOnUiThread {
            showFullAd.show(
                this,
                adEmptyBack = true,
                showed = { stopAll() },
                closeAd = {
                    LoadAdManager.load(LocalConf.TEST_INTER)
                    val intent = Intent(this, SecurityResultAc::class.java).apply {
                        putExtra("speed",transferRateOctet)
                        putExtra("wifiName",currentWifiName)
                        putExtra("wifiIp",wifiIp)
                        putExtra("maxSpeed",maxSpeed)
                        putExtra("wifiMac",wifiMac)
                    }
                    startActivity(intent)
                    finish()
                }
            )
        }
    }

    private fun startAnimator(){
        objectAnimator= ObjectAnimator.ofFloat(iv_scan, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }
    }

    private fun stopAnimator() {
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAll()
    }

    private fun stopAll(){
        stopAnimator()
        speedTestSocket.clearListeners()
        speedTestSocket.closeSocket()
        scanTimeJob?.cancel()
        scanTimeJob=null
        downloadJob?.cancel()
        downloadJob=null
        closeWifiJob?.cancel()
        closeWifiJob=null
    }
}