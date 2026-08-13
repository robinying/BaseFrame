package com.robin.baseframe.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseViewActivity
import com.robin.baseframe.app.navigation.NavHostFragment
import com.robin.baseframe.databinding.ActivityMainBinding
import com.robin.baseframe.test.DemoJni
import com.robin.baseframe.ui.fragment.MainFragment
import com.robin.baseframe.ui.home.DemoDeepLink

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
        navigateToDeepLinkedDemo(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navigateToDeepLinkedDemo(intent)
    }

    private fun navigateToDeepLinkedDemo(intent: Intent?) {
        val demoId = DemoDeepLink.parseDemoId(intent?.dataString) ?: return
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_fragment)
            as? NavHostFragment ?: return
        val mainFragment = navHostFragment.childFragmentManager.fragments
            .filterIsInstance<MainFragment>()
            .firstOrNull()
        if (mainFragment != null) {
            mainFragment.openDemo(demoId)
            return
        }
        navHostFragment.childFragmentManager.registerFragmentLifecycleCallbacks(
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(
                    fragmentManager: androidx.fragment.app.FragmentManager,
                    fragment: androidx.fragment.app.Fragment,
                    view: View,
                    savedInstanceState: Bundle?
                ) {
                    if (fragment is MainFragment) {
                        fragment.openDemo(demoId)
                        fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                    }
                }
            },
            false
        )
    }

    private fun isNightMode(): Boolean =
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
}
