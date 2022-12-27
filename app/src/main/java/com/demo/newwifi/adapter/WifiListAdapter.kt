package com.demo.newwifi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.newwifi.R
import com.demo.newwifi.bean.WifiInfoBean
import kotlinx.android.synthetic.main.item_wifi.view.*

class WifiListAdapter(
    private val context: Context,
    private val click:(bean:WifiInfoBean)->Unit
):RecyclerView.Adapter<WifiListAdapter.WifiView>() {
    private val list= mutableListOf<WifiInfoBean>()

    fun updateList(list:MutableList<WifiInfoBean>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    inner class WifiView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiView {
        return WifiView(LayoutInflater.from(context).inflate(R.layout.item_wifi,parent,false))
    }

    override fun onBindViewHolder(holder: WifiView, position: Int) {
        with(holder.itemView){
            val wifiInfoBean = list[position]
            tv_wifi_name.text=wifiInfoBean.name
            iv_wifi_level.setImageResource(wifiInfoBean.getLevelIcon())
            iv_wifi_pwd.setImageResource(if (wifiInfoBean.hasPwd) R.drawable.suo else R.drawable.suo2)
        }
    }

    override fun getItemCount(): Int = list.size

}