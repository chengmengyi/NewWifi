package com.demo.newwifi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.newwifi.R
import com.demo.newwifi.bean.NetTestHistoryBean
import kotlinx.android.synthetic.main.item_history.view.*
import java.text.SimpleDateFormat

class HistoryAdapter(
    private val context: Context,
    private val list:ArrayList<NetTestHistoryBean>
):RecyclerView.Adapter<HistoryAdapter.HistoryView>() {

    inner class HistoryView(view:View):RecyclerView.ViewHolder(view){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryView {
        return HistoryView(LayoutInflater.from(context).inflate(R.layout.item_history,parent,false))
    }

    override fun onBindViewHolder(holder: HistoryView, position: Int) {
        with(holder.itemView){
            val netTestHistoryBean = list[position]
            tv_wifi_name.text=netTestHistoryBean.name
            tv_date.text=getDate(netTestHistoryBean.time)
            tv_time.text=getTime(netTestHistoryBean.time)
            tv_router.text=netTestHistoryBean.router.toString()
            tv_tg.text=netTestHistoryBean.tg.toString()
            tv_cn.text=netTestHistoryBean.cn.toString()
        }
    }

    override fun getItemCount(): Int = list.size

    private fun getDate(time:Long):String{
        val simpleDateFormat = SimpleDateFormat("MM-dd")
        return simpleDateFormat.format(time)
    }

    private fun getTime(time:Long):String{
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        return simpleDateFormat.format(time)
    }
}