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
import com.robin.baseframe.databinding.FragmentMainBinding

class MainFragment : BaseFragment<BaseViewModel, FragmentMainBinding>() {
    override fun initView(savedInstanceState: Bundle?) {
        binding.btDialog.clickNoRepeat {
            showDialogFragment(BottomDialog())
        }
        binding.btAnyLayer.onClick{
            nav().navigateAction(R.id.action_main_to_anyLayerFragment)

        }
    }
}