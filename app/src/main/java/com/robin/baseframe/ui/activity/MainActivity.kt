package com.robin.baseframe.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.content.res.Configuration
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseViewActivity
import com.robin.baseframe.databinding.ActivityMainBinding
import com.robin.baseframe.test.DemoJni

class MainActivity : BaseViewActivity<ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isNightMode()
            isAppearanceLightNavigationBars = !isNightMode()
        }
        val hostFragment = binding.root.findViewById<View>(R.id.host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(hostFragment) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
        DemoJni().sayHi()
        DemoJni().accessField()
    }

    private fun isNightMode(): Boolean =
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
}
