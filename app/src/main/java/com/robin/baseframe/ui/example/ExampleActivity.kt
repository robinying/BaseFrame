package com.robin.baseframe.ui.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.robin.baseframe.base.BaseComposeActivity
import com.robin.baseframe.ui.theme.BaseFrameTheme

/**
 * 示例页面 Activity — 演示 BaseComposeActivity + ExampleScreen 用法。
 */
class ExampleActivity : BaseComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaseFrameTheme {
                ExampleScreen(onBack = { onBackPressedDispatcher.onBackPressed() })
            }
        }
    }
}
