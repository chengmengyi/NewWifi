package com.demo.newwifi.conf

import com.demo.newwifi.admob.AdLimitManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject

object FireConf {

    fun readFireConf(){
//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if (it.isSuccessful){
//                parseAdJson(remoteConfig.getString("netG_ad"))
//            }
//        }
    }

    private fun parseAdJson(json:String){
        try {
            val jsonObject = JSONObject(json)
            AdLimitManager.setMax(jsonObject.optInt("netG_click"),jsonObject.optInt("netG_show"))
            MMKV.defaultMMKV().encode("netG_ad",json)
        }catch (e:Exception){

        }
    }

    fun getAdJson():String{
        val s = MMKV.defaultMMKV().decodeString("netG_ad") ?: ""
        if (s.isEmpty()){
            return LocalConf.localAd
        }
        return s
    }
}