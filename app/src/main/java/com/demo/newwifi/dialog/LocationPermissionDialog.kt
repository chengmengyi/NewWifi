package com.demo.newwifi.dialog

import android.content.Intent
import android.provider.Settings
import com.demo.newwifi.R
import com.demo.newwifi.base.BaseDialog
import com.demo.newwifi.uti.hasLocationPermission
import kotlinx.android.synthetic.main.dialog_location_permission.*

class LocationPermissionDialog(private val result:(hasPermission:Boolean)->Unit) :BaseDialog(R.layout.dialog_location_permission){
    override fun initView() {
        dialog?.setCancelable(false)
        tv_sure.setOnClickListener {
            startActivityForResult(
                Intent().apply { action = Settings.ACTION_LOCATION_SOURCE_SETTINGS},
                1000
            )
        }

        iv_cancel.setOnClickListener {
            dismiss()
            result.invoke(false)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1000){
            if(requireContext().hasLocationPermission()){
                dismiss()
                result.invoke(true)
            }
        }
    }
}