package com.demo.newwifi.ac.network_test

import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.newwifi.R
import com.demo.newwifi.adapter.HistoryAdapter
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.bean.NetTestHistoryBean
import com.demo.newwifi.uti.TestHistoryManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_history.*

class HistoryAc:BaseAc(R.layout.activity_history), OnRefreshLoadMoreListener {
    private var offset=0
    private val historyList= arrayListOf<NetTestHistoryBean>()
    private val historyAdapter by lazy { HistoryAdapter(this,historyList) }

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        refresh_layout.setOnRefreshLoadMoreListener(this)
        rv_history.apply {
            layoutManager=LinearLayoutManager(this@HistoryAc)
            adapter=historyAdapter
        }
        refresh_layout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        offset=0
        queryHistory()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        queryHistory()
    }

    private fun queryHistory(){
        val queryHistory = TestHistoryManager.queryHistory(offset)
        if(queryHistory.isNotEmpty()){
            if (offset==0){
                historyList.clear()
            }
            offset+=queryHistory.size
            historyList.addAll(queryHistory)
            historyAdapter.notifyDataSetChanged()
        }
        if (refresh_layout.isRefreshing){
            refresh_layout.finishRefresh()
        }
        if (refresh_layout.isLoading){
            refresh_layout.finishLoadMore()
        }
    }
}