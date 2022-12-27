package com.demo.newwifi.dialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.demo.newwifi.R
import com.demo.newwifi.base.BaseDialog
import com.demo.newwifi.bean.WifiInfoBean
import com.demo.newwifi.uti.hasOverlayPermission
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.dialog_connect_wifi.*

class ConnectWifiDialog(
    private val wifiInfoBean: WifiInfoBean,
    private val connect:(pwd:String)->Unit
):BaseDialog(R.layout.dialog_connect_wifi) {
    override fun initView() {
        tv_wifi_name.text=wifiInfoBean.name
        et_pwd.setText(MMKV.defaultMMKV().decodeString(wifiInfoBean.name)?:"")

        tv_cancel.setOnClickListener { dismiss() }

        tv_connect.setOnClickListener {
            connectWifi()
        }
        iv_show_pwd.setOnClickListener {
            iv_show_pwd.isSelected=!iv_show_pwd.isSelected
            if(iv_show_pwd.isSelected){
                et_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                et_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun connectWifi(){
        if(!hasOverlayPermission(requireContext())){
            val intent= Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}"))
            startActivityForResult(intent, 101)
            return
        }

        val trim = et_pwd.text.toString().trim()
        if(trim.isEmpty()){
            return
        }
        dismiss()
        connect.invoke(trim)
    }
}