package com.demo.newwifi.ac

import com.demo.newwifi.R
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.conf.LocalConf
import kotlinx.android.synthetic.main.activity_web.*

class WebAc:BaseAc(R.layout.activity_web) {
    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        webview.apply {
            settings.javaScriptEnabled=true
            loadUrl(LocalConf.url)
        }
    }
}