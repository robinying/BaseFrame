package com.robin.baseframe.ui.activity

import android.Manifest
import android.os.Bundle
import com.permissionx.guolindev.PermissionX
import com.robin.baseframe.app.base.BaseActivity
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.toast
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
    }
}