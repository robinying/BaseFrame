package com.robin.baseframe.ui.fragment

import android.os.Bundle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.databinding.FragmentComposeBinding

class ComposeFragment: BaseFragment<BaseViewModel,FragmentComposeBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        binding.composeView.setContent { greeting() }
    }

    @Preview
    @Composable
    private fun greeting(){
        Text(text = "this is a compose TextView")
    }
}