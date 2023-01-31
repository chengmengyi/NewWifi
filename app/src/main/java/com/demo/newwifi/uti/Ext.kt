package com.demo.newwifi.uti

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.math.roundToInt

var connectedWifiName=""

fun String.log(){
    Log.e("qwer",this)
}

fun Context.showToast(text:String){
    Toast.makeText(this,text, Toast.LENGTH_LONG).show()
}


fun View.showView(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

fun Context.hasLocationPermission(): Boolean {
    var result = false
    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager ?: return false
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    ) {
        result = true
    }
    return result
}

fun hasOverlayPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true
    return Settings.canDrawOverlays(context)
}

suspend fun getDelay(ip: String): Int {
    var delay = Random().nextInt(200)
    var timeout = 1
    val cmd = "/system/bin/ping -w $timeout $ip"
    return withContext(Dispatchers.IO) {
        val r = ping(cmd)
        if (r != null) {
            try {
                val index: Int = r.indexOf("min/avg/max/mdev")
                if (index != -1) {
                    val tempInfo: String = r.substring(index + 19)
                    val temps = tempInfo.split("/".toRegex()).toTypedArray()
                    delay = temps[0].toFloat().roundToInt()
                    if(delay<=0){
                        delay = Random().nextInt(200)
                    }
                }
            } catch (e: Exception) {

            }
        }
        delay
    }
}

private fun ping(cmd: String): String? {
    var process: Process? = null
    try {
        process = Runtime.getRuntime().exec(cmd) //执行ping指令
        val inputStream = process!!.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        var line: String?
        while (null != reader.readLine().also { line = it }) {
            sb.append(line)
            sb.append("\n")
        }
        reader.close()
        inputStream.close()
        return sb.toString()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        process?.destroy()
    }
    return null
}

fun Context.getNetStatus(): Int {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return 2
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            return 0
        }
    } else {
        return 1
    }
    return 1
}
