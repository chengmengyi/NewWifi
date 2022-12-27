package com.demo.newwifi.eventbus

import org.greenrobot.eventbus.EventBus

class EventBean(
    val code:Int
) {
    fun sendEvent(){
        EventBus.getDefault().post(this)
    }
}