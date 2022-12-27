package com.demo.newwifi.uti

import android.content.Context
import com.demo.newwifi.bean.NetTestHistoryBean
import org.litepal.LitePal

object TestHistoryManager {

    fun saveHistory(context: Context,router:Int,tg:Int,cn:Int){
        try {
            NetTestHistoryBean(
                name = WifiUtils.getInstance(context)?.getConnectedWifiName()?:"unknown",
                time = System.currentTimeMillis(),
                router = router,
                tg = tg,
                cn = cn
            ).save()
        } catch (e: Exception) {
        }
    }

    fun queryHistory(offset:Int):List<NetTestHistoryBean>{
        return LitePal.select("*")
            .order("time desc")
            .limit(20)
            .offset(offset)
            .find(NetTestHistoryBean::class.java)
    }
}