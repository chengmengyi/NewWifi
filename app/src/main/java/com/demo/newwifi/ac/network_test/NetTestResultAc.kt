package com.demo.newwifi.ac.network_test

import com.demo.newwifi.R
import com.demo.newwifi.base.BaseAc
import com.demo.newwifi.uti.TestHistoryManager
import kotlinx.android.synthetic.main.activity_net_test_result.*
import java.util.*

class NetTestResultAc:BaseAc(R.layout.activity_net_test_result) {
    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        setGameSpeed()
        setVideoSpeed()
    }

    private fun setGameSpeed(){
        val googleDelay = intent.getIntExtra("googleDelay", 0)
        val twitterDelay = intent.getIntExtra("twitterDelay", 0)
        val facebookDelay = intent.getIntExtra("facebookDelay", 0)
        tv_router.text="${googleDelay}ms"
        tv_total.text="${twitterDelay}ms"
        tv_network.text="${facebookDelay}ms"
        tv_game_speed.text="The game can be played ${
            when(getVideoLevel()){
                "360P"->"normally"
                "720P"->"fluent"
                else->"excellent"
            }
        }"
        TestHistoryManager.saveHistory(this,googleDelay,twitterDelay,facebookDelay)
    }

    private fun setVideoSpeed(){
        val videoLevel = getVideoLevel()
        tv_video_speed.text="According to your speed,We recommend $videoLevel videos"
        when(videoLevel){
            "360P"->{
                iv_360p.setImageResource(R.drawable.yuan1)
                tv_360p.isSelected=true
            }
            "720P"->{
                iv_360p.setImageResource(R.drawable.yuan1)
                tv_360p.isSelected=true
                iv_720p.setImageResource(R.drawable.yuan3)
                tv_720p.isSelected=true
                line1.isSelected=true
            }
            "1080P"->{
                iv_360p.setImageResource(R.drawable.yuan1)
                tv_360p.isSelected=true
                iv_720p.setImageResource(R.drawable.yuan1)
                tv_720p.isSelected=true
                iv_1080p.setImageResource(R.drawable.yuan3)
                tv_1080p.isSelected=true
                line1.isSelected=true
                line2.isSelected=true
            }
            "4K"->{
                iv_360p.setImageResource(R.drawable.yuan1)
                tv_360p.isSelected=true
                iv_720p.setImageResource(R.drawable.yuan1)
                tv_720p.isSelected=true
                iv_1080p.setImageResource(R.drawable.yuan1)
                tv_1080p.isSelected=true
                iv_4k.setImageResource(R.drawable.yuan3)
                tv_4k.isSelected=true
                line1.isSelected=true
                line2.isSelected=true
                line3.isSelected=true
            }
        }
    }

    private fun getVideoLevel():String{
        var speed = intent.getLongExtra("speed", 0L) / 1024
        if(speed<=0){
            speed= (Random().nextInt(100)+100).toLong()
        }
        return when(speed){
            //720p
            in 100..400->"720P"
            //1080p
            in 400..900->"1080P"
            //4K
            in 900..Int.MAX_VALUE->"4K"
            //360P
            else->"360P"
        }
    }
}