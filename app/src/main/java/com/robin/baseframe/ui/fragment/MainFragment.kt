package com.robin.baseframe.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
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
        binding.btCoordinator.onClick{
            nav().navigateAction(R.id.action_main_to_coordinatorFragment)
        }
        binding.btCountDown.onClick{
            nav().navigateAction(R.id.action_main_to_countDownFragment)
        }
        Test.quickSort(data, 0, data.size - 1)
        data.forEach {
            LogUtils.debugInfo("$it")
        }
        //沉浸式状态栏
        //Android 11开始提供新的API WindowInsetsController
        binding.mainCl.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        /*
        * 这里其实可以借助setOnApplyWindowInsetsListener()函数去监听WindowInsets发生变化的事件，
        * 当有监听到发生变化时，我们可以读取顶部Insets的大小，然后对控件进行相应距离的偏移。
        * */
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolBar) { view, insets ->
            val params = view.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }

    }
}