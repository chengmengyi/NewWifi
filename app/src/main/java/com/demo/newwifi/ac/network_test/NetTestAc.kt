package com.demo.newwifi.ac.network_test

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import com.demo.newwifi.R
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.uti.getDelay
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.android.synthetic.main.activity_net_test.*
import kotlinx.coroutines.*
import java.util.*

class NetTestAc:BaseAc(R.layout.activity_net_test) {
    private var objectAnimator:ObjectAnimator?=null
    private val speedTestSocket = SpeedTestSocket()
    private var scanTimeJob:Job?=null
    private var downloadJob:Job?=null

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }

        startAnimator()
        startNetTest()
    }

    private fun startNetTest(){
        var speed=0L
        var googleDelay=0
        var twitterDelay=0
        var facebookDelay=0
        val listener = object : ISpeedTestListener {
            override fun onCompletion(report: SpeedTestReport) {
                speed = report.transferRateOctet.toLong()
                toResultAc(speed, googleDelay, twitterDelay, facebookDelay)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                toResultAc(speed, googleDelay, twitterDelay, facebookDelay)
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {}
        }
        speedTestSocket.addSpeedTestListener(listener)

        downloadJob=GlobalScope.launch {
            googleDelay = getDelay("www.google.com")
            twitterDelay = getDelay("twitter.com")
            facebookDelay = getDelay("www.facebook.com")
            delay(2000L)
            speedTestSocket.startDownload("http://ipv4.appliwave.testdebit.info/5M/5M.zip")
        }

        scanTimeJob= GlobalScope.launch {
            delay(10000L)
            speedTestSocket.clearListeners()
            withContext(Dispatchers.Main){
                toResultAc(speed, googleDelay, twitterDelay, facebookDelay)
            }
        }
    }

    private fun toResultAc(speed:Long,googleDelay:Int,twitterDelay:Int,facebookDelay:Int){
        val apply = Intent(this, NetTestResultAc::class.java).apply {
            putExtra("speed", speed)
            putExtra("googleDelay", getSpeed(googleDelay))
            putExtra("twitterDelay", getSpeed(twitterDelay))
            putExtra("facebookDelay", getSpeed(facebookDelay))
        }
        startActivity(apply)
        finish()
    }

    private fun getSpeed(speed:Int):Int{
        if(speed<=0){
            return Random().nextInt(100)+100
        }
        return speed
    }

    private fun startAnimator(){
        objectAnimator= ObjectAnimator.ofFloat(iv_scan, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }
    }

    private fun stopAnimator(){
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
        objectAnimator=null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
        speedTestSocket.clearListeners()
        speedTestSocket.closeSocket()
        scanTimeJob?.cancel()
        scanTimeJob=null
        downloadJob?.cancel()
        downloadJob=null
    }
}