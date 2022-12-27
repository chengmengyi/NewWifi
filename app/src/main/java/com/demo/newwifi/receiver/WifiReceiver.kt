package com.demo.newwifi.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Parcelable
import com.demo.newwifi.eventbus.EventBean
import com.demo.newwifi.eventbus.EventCode
import com.demo.newwifi.uti.ActivityCallback
import com.demo.newwifi.uti.connectedWifiName
import com.demo.newwifi.uti.showToast
import com.tencent.mmkv.MMKV

class WifiReceiver: BroadcastReceiver()  {
    override fun onReceive(context: Context, intent: Intent) {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action) {
            when (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
                WifiManager.WIFI_STATE_DISABLED -> {
                    EventBean(EventCode.WIFI_CLOSE).sendEvent()
                }
                WifiManager.WIFI_STATE_DISABLING -> {}
                WifiManager.WIFI_STATE_ENABLED -> {
                    EventBean(EventCode.WIFI_OPEN).sendEvent()
                }
                WifiManager.WIFI_STATE_ENABLING -> {}
                WifiManager.WIFI_STATE_UNKNOWN -> {}
            }
        }
        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，
        // 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
        // 当然刚打开wifi肯定还没有连接到有效的无线
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION == intent.action) {
//            Log.i("tag", "NETWORK_STATE_CHANGED_ACTION")
            val parcelableExtra = intent
                .getParcelableExtra<Parcelable>(WifiManager.EXTRA_NETWORK_INFO)
            if (null != parcelableExtra) {
                val networkInfo = parcelableExtra as NetworkInfo
                val state = networkInfo.state
                val isConnected = state == NetworkInfo.State.CONNECTED // 当然，这边可以更精确的确定状态
//                Log.i("tag", "NETWORK_STATE_CHANGED_ACTION=====${isConnected}")
                if (isConnected) {
                    EventBean(EventCode.WIFI_CONNECTED).sendEvent()
                } else {
                    EventBean(EventCode.WIFI_DISCONNECTED).sendEvent()
                }
            }
        }
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION == intent.action) {
            handleSupplicantState(intent,context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleSupplicantState(intent: Intent, context: Context) {
        val error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)
        if (error == WifiManager.ERROR_AUTHENTICATING) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val configurations = wifiManager.configuredNetworks
            for (configuration in configurations) {
                val replace = configuration.SSID.replace("\"", "")
                if (replace == connectedWifiName) {
                    if(ActivityCallback.isFront){
                        context.showToast("Password error")
                    }
                    var removeResult = wifiManager.removeNetwork(configuration.networkId)
                    removeResult = removeResult and wifiManager.saveConfiguration()
                    connectedWifiName=""
                    MMKV.defaultMMKV().encode(replace,"")
                }
            }
        }
    }
}