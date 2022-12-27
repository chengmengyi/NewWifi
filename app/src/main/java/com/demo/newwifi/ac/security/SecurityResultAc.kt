package com.demo.newwifi.ac.security

import android.net.wifi.WifiManager
import com.demo.newwifi.R
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.uti.WifiUtils
import kotlinx.android.synthetic.main.activity_security_result.*

class SecurityResultAc:BaseAc(R.layout.activity_security_result) {
    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        val wifiName = intent.getStringExtra("wifiName") ?: ""
        tv_wifi_name.text=wifiName
        tv_wifi_max_speed.text=intent.getStringExtra("maxSpeed") ?: ""
        tv_wifi_ip.text=intent.getStringExtra("wifiIp") ?: ""
        tv_wifi_mac.text=intent.getStringExtra("wifiMac") ?: ""

        val hasPwd = checkWifiHasPwd(wifiName)
        if(!hasPwd||WifiUtils.getInstance(this)?.isWifiEnable!=true){
            iv_result_bg.setImageResource(R.drawable.security_result3)
            iv_result_state.setImageResource(R.drawable.security_result4)
            tv_result.text="public network with little or no security"
        }else{
            tv_result.text="private network such as home or work network"
        }
    }

    private fun checkWifiHasPwd(ssid:String):Boolean{
        try {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val connectionInfo = wifiManager.connectionInfo
            val scanResults = wifiManager.scanResults
            scanResults?.let {
                for (scanResult in it) {
                    if (scanResult.SSID==ssid&&scanResult.BSSID==connectionInfo.bssid){
                        if(null!=scanResult.capabilities){
                            val capabilities = scanResult.capabilities.trim()
                            if(capabilities==""||capabilities=="[ESS]"){
                                return false
                            }
                        }
                    }
                }
            }
        }catch (e:Exception){
            return true
        }
        return true
    }
}