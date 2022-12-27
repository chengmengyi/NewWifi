package com.demo.newwifi.bean

import org.litepal.crud.LitePalSupport

data class NetTestHistoryBean(
    val name:String,
    val time:Long,
    val router:Int,
    val tg:Int,
    val cn:Int,
): LitePalSupport()