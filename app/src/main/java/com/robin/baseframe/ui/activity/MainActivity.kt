package com.robin.baseframe.ui.activity

import android.os.Bundle
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseViewActivity
import com.robin.baseframe.app.util.StatusBarUtils
import com.robin.baseframe.databinding.ActivityMainBinding
import com.robin.baseframe.test.DemoJni

class MainActivity : BaseViewActivity<ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarUtils.setColor(this, resources.getColor(R.color.md_amber_A200))
        StatusBarUtils.immersive(this)
        DemoJni().sayHi()
        DemoJni().accessField()
    }
}
