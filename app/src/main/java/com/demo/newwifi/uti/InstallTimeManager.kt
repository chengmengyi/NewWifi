package com.demo.newwifi.uti

import com.tencent.mmkv.MMKV

object InstallTimeManager {

    private fun getInstallTime()=MMKV.defaultMMKV().decodeLong("time",0L)

    fun writeTime(){
        val installTime = getInstallTime()
        if (installTime==0L){
            MMKV.defaultMMKV().encode("time",System.currentTimeMillis())
        }
    }

    fun getDays():Long{
        try {
            val time = System.currentTimeMillis() - getInstallTime()
            val days = time / 1000 / 60 / 60 / 24
            if(days<=0){
                return 1L
            }
            return days
        } catch (e: Exception) {
            return 1L
        }
    }
}