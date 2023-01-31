package com.demo.newwifi.admob

import com.tencent.mmkv.MMKV
import java.text.SimpleDateFormat
import java.util.*

object AdLimitManager {
    private var maxClick=15
    private var maxShow=50
    private var click=0
    private var show=0
    private val refresh= hashMapOf<String,Boolean>()

    fun refresh(type:String)=refresh[type]?:true

    fun setValue(type: String,boolean: Boolean){
        refresh[type]=boolean
    }

    fun resetValue(){
        refresh.keys.forEach { refresh[it]=true }
    }

    fun setMax(click:Int,show:Int){
        maxClick=click
        maxShow=show
    }

    fun limit() = click > maxClick||show > maxShow

    fun addClick(){
        click++
        MMKV.defaultMMKV().encode(numKey("netG_click"), click)
    }

    fun addShow(){
        show++
        MMKV.defaultMMKV().encode(numKey("netG_click"), show)
    }

    fun readLocalNum(){
        click= MMKV.defaultMMKV().decodeInt(numKey("click"),0)
        show= MMKV.defaultMMKV().decodeInt(numKey("netG_show"),0)
    }

    private fun numKey(key:String)="${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}_$key"
}