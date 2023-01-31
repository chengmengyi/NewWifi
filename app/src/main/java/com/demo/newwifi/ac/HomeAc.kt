package com.demo.newwifi.ac

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.Settings
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.newwifi.R
import com.demo.newwifi.ac.network_test.HistoryAc
import com.demo.newwifi.ac.network_test.NetTestAc
import com.demo.newwifi.ac.security.SecurityAc
import com.demo.newwifi.adapter.WifiListAdapter
import com.demo.newwifi.admob.AdLimitManager
import com.demo.newwifi.admob.LoadAdManager
import com.demo.newwifi.admob.ShowFullAd
import com.demo.newwifi.admob.ShowNativeAd
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.bean.WifiInfoBean
import com.demo.newwifi.conf.LocalConf
import com.demo.newwifi.dialog.ConnectWifiDialog
import com.demo.newwifi.dialog.LocationPermissionDialog
import com.demo.newwifi.eventbus.EventBean
import com.demo.newwifi.eventbus.EventCode
import com.demo.newwifi.receiver.WifiReceiver
import com.demo.newwifi.uti.*
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.tencent.mmkv.MMKV
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_home_connected_wifi_info.*
import kotlinx.android.synthetic.main.layout_home_content.*
import kotlinx.android.synthetic.main.layout_home_drawer.*
import kotlinx.android.synthetic.main.layout_home_func.*
import kotlinx.android.synthetic.main.layout_home_top_wifi_info.*
import kotlinx.android.synthetic.main.layout_wifi_list.*
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception

class HomeAc:BaseAc(R.layout.activity_home), OnRefreshListener {
    private var clickTime=0L
    private var wifiReceiver:WifiReceiver?=null
    private val showHomeAd by lazy { ShowNativeAd(LocalConf.HOME) }
    private val showFullAd by lazy { ShowFullAd(LocalConf.CONNECT_WIFI) }

    private val wifiAdapter by lazy { WifiListAdapter(this){ clickWifiItem(it) } }

    override fun initEvent(): Boolean = true

    override fun initView() {
        immersionBar.statusBarView(top).init()
        setAdapter()
        setClick()
        refresh_layout.setOnRefreshListener(this)
        checkHasLocationServer()
    }

    private fun checkHasLocationServer(){
        if(hasLocationPermission()){
            requestLocationPermission {  }
        }else{
            LocationPermissionDialog{
                if(it){
                    requestLocationPermission {  }
                }
            }.show(supportFragmentManager,"LocationDialog")
        }
    }

    private fun startReceiver(){
        if(null!=wifiReceiver) return
        wifiReceiver = WifiReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        registerReceiver(wifiReceiver, filter)
    }

    private fun setClick(){
        iv_set.setOnClickListener {
            if(!drawer_layout.isOpen){
                drawer_layout.openDrawer(Gravity.LEFT)
            }
        }
        iv_history.setOnClickListener {
            startActivity(Intent(this,HistoryAc::class.java))
        }
        llc_security.setOnClickListener {
            if(!drawer_layout.isOpen&&canClick()){
                requestLocationPermission {
                    if (it){
                        getConnectedWifiInfo()
                        scanWifiList()
                        startActivity(Intent(this,SecurityAc::class.java))
                    }
                }
            }
        }
        llc_network.setOnClickListener {
            if(!drawer_layout.isOpen&&canClick()){
                if(WifiUtils.getInstance(this)?.isWifiEnable == true&&getNetStatus()!=1){
                    requestLocationPermission {
                        if (it){
                            getConnectedWifiInfo()
                            scanWifiList()
                            startActivity(Intent(this,NetTestAc::class.java))
                        }
                    }
                }else{
                    showToast("Please open the network")
                }
            }
        }
        llc_contact.setOnClickListener {
            try {
                val uri = Uri.parse("mailto:${LocalConf.email}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                startActivity(intent)
            }catch (e: Exception){
                showToast("Contact us by email：${LocalConf.email}")
            }
        }

        llc_agree.setOnClickListener {
            startActivity(Intent(this,WebAc::class.java))
        }

        llc_update.setOnClickListener {
            val packName = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packName")
            }
            startActivity(intent)
        }

        llc_share.setOnClickListener {
            val pm = packageManager
            val packageName=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${packageName}"
            )
            startActivity(Intent.createChooser(intent, "share"))
        }
    }

    private fun canClick():Boolean{
        val currentTimeMillis = System.currentTimeMillis()
        if(currentTimeMillis-clickTime>500){
            clickTime=currentTimeMillis
            return true
        }
        return false
    }

    private fun setAdapter(){
        rv_wifi_list.apply {
            layoutManager=LinearLayoutManager(this@HomeAc)
            adapter=wifiAdapter
        }
    }

    private fun clickWifiItem(wifiInfoBean: WifiInfoBean){
        if(drawer_layout.isOpen) return
        if (wifiInfoBean.hasPwd){
            ConnectWifiDialog(wifiInfoBean){
                showFullAd.show(
                    this,
                    adEmptyBack = true,
                    showed = {},
                    closeAd = {
                        LoadAdManager.load(LocalConf.CONNECT_WIFI)
                        connectedWifiName=wifiInfoBean.name
                        WifiUtils.getInstance(this)?.openWifi()
                        WifiUtils.getInstance(this)?.connectWifiPws(wifiInfoBean.name,it)
                        MMKV.defaultMMKV().encode(wifiInfoBean.name,it)
                    }
                )
            }.show(supportFragmentManager,"ConnectWifiDialog")
        }else{
            if(!hasOverlayPermission(this)){
                ActivityCallback.banReload=true
                val intent= Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${this.packageName}"))
                startActivityForResult(intent, 101)
                return
            }
            connectedWifiName=wifiInfoBean.name
            WifiUtils.getInstance(this)?.connectWifiNoPws(wifiInfoBean.name)
        }
    }

    @Subscribe
    fun onEvent(eventBean: EventBean) {
        when(eventBean.code){
            EventCode.WIFI_OPEN->{
                requestLocationPermission {
                    if (it){
                        scanWifiList()
                    }else{
                        layout_wifi_list.showView(false)
                        layout_wifi_connected.showView(false)
                    }
                }
            }
            EventCode.WIFI_CLOSE->{
                hideConnectedWifi()
                layout_wifi_connected.showView(false)
            }
            EventCode.WIFI_CONNECTED->{
                requestLocationPermission {
                    if (it){
                        getConnectedWifiInfo(wifiConnected = true)
                        scanWifiList()
                    }else{
                        hideConnectedWifi()
                    }
                }
            }
            EventCode.WIFI_DISCONNECTED->{
                hideConnectedWifi()
            }
        }
    }

    private fun requestLocationPermission(callback:(has:Boolean)->Unit){
        if (!hasLocationPermission()){
            LocationPermissionDialog{
                if(it){
                    requestLocationPermission{ }
                }
            }.show(supportFragmentManager,"LocationDialog")
            if(refresh_layout.isRefreshing){
                refresh_layout.finishRefresh()
            }
            return
        }
        AndPermission.with(this)
            .runtime()
            .permission(Permission.ACCESS_FINE_LOCATION)
            .onGranted {
                startReceiver()
                callback.invoke(true)
            }
            .onDenied {
                callback.invoke(false)
            }
            .start()
    }

//    private fun checkLocationPermission(): Boolean {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            return true
//        }
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
//        return false
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 101) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getConnectedWifiInfo()
//                scanWifiList()
//            }else{
//                layout_wifi_list.showView(false)
//                hideConnectedWifi()
//            }
//        }
//    }

    private fun scanWifiList(){
        val instance = WifiUtils.getInstance(this)
        if (instance?.isWifiEnable == true){
            layout_wifi_list.showView(true)
            instance.scanWifiList().let {
                wifiAdapter.updateList(it)
            }
        }
        if(refresh_layout.isRefreshing){
            refresh_layout.finishRefresh()
        }
    }

    private fun getConnectedWifiInfo(wifiConnected:Boolean=false){
        val instance = WifiUtils.getInstance(this)
        if (instance?.isConnectWifi() == true || wifiConnected){
            layout_wifi_connected.showView(true)
            val name = instance?.getConnectedWifiName()?:""
            tv_top_wifi_name.text=name
            iv_wifi_connected.showView(true)
            tv_connected_wifi_name.text=name
            connectedWifiName=name
            iv_connected_wifi_pwd.setImageResource(if (checkWifiHasPwd(name)) R.drawable.suo else R.drawable.suo2)
        }else{
            hideConnectedWifi()
        }
    }

    private fun hideConnectedWifi(){
        layout_wifi_connected.showView(false)
        tv_top_wifi_name.text=""
        iv_wifi_connected.showView(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        requestLocationPermission {
            if (it){
                scanWifiList()
            }else{
                if(refresh_layout.isRefreshing){
                    refresh_layout.finishRefresh()
                }
            }
        }
    }

    private fun stopReceiver() {
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver)
            wifiReceiver = null
        }
    }

    override fun onResume() {
        super.onResume()
        ActivityCallback.banReload=false
        if (AdLimitManager.refresh(LocalConf.HOME)){
            showHomeAd.showAc(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showHomeAd.stopShow()
        AdLimitManager.setValue(LocalConf.HOME,true)
        stopReceiver()
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