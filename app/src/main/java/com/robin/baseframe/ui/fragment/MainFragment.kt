package com.robin.baseframe.ui.fragment

import android.os.Bundle
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.ext.nav
import com.robin.baseframe.app.ext.navigateAction
import com.robin.baseframe.app.ext.showDialogFragment
import com.robin.baseframe.app.ext.view.clickNoRepeat
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentMainBinding
import com.robin.baseframe.test.Test
import org.w3c.dom.Node

class MainFragment : BaseFragment<BaseViewModel, FragmentMainBinding>() {
    private val data = intArrayOf(10, 3, 4, 2, 5, 42, 32, 8)
    override fun initView(savedInstanceState: Bundle?) {
        binding.btDialog.clickNoRepeat {
            showDialogFragment(BottomDialog())
        }
        binding.btAnyLayer.onClick {
            nav().navigateAction(R.id.action_main_to_anyLayerFragment)
        }
        binding.btMotion.onClick {
            nav().navigateAction(R.id.action_main_to_motionFragment)
        }
        binding.btScopedStorage.onClick{
            nav().navigateAction(R.id.action_main_to_storageFragment)

        }
        Test.quickSort(data, 0, data.size - 1)
        data.forEach {
            LogUtils.debugInfo("$it")
        }
    }
}