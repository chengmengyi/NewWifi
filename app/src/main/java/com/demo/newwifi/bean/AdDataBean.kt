package com.demo.newwifi.bean

class AdDataBean(
    val netG_id:String,
    val netG_source:String,
    val netG_type:String,
    val netG_priority:Int,
) {
    override fun toString(): String {
        return "AdDataBean(netG_id='$netG_id', netG_source='$netG_source', netG_type='$netG_type', netG_priority=$netG_priority)"
    }
}