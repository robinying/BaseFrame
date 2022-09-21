package com.robin.baseframe.ui.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.permissionx.guolindev.PermissionX
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseActivity
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.toast
import com.robin.baseframe.app.util.StatusBarUtils
import com.robin.baseframe.databinding.ActivityMainBinding


class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>() {


    override fun initView(savedInstanceState: Bundle?) {
        PermissionX.init(mActivity)
            .permissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    toast("All permissions are granted")
                } else {
                    toast("These permissions are denied: $deniedList")
                }
            }
        //setLightStatusBar()
        StatusBarUtils.setColor(this, resources.getColor(R.color.md_amber_A200))
        StatusBarUtils.immersive(this)
    }

    private fun setLightStatusBar() {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setDarkStatusBar() {
        val flags = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}