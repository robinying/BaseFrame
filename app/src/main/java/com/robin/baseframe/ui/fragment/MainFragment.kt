package com.robin.baseframe.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.robin.aidldemo.Apple
import com.robin.aidldemo.IApiCallBack
import com.robin.aidldemo.IRemoteService
import com.robin.aidldemo.IRemoteServiceCallBack
import com.robin.baseframe.R
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.ext.nav
import com.robin.baseframe.app.ext.navigateAction
import com.robin.baseframe.app.ext.showDialogFragment
import com.robin.baseframe.app.ext.view.clickNoRepeat
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentMainBinding
import com.robin.baseframe.service.RemoteService
import com.robin.baseframe.test.Test
import com.robin.baseframe.viewmodel.MainViewModel

class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {
    private val data = intArrayOf(10, 3, 4, 2, 5, 42, 32, 8)
    private var mIApiCallback: IApiCallBack? = null
    private var mObserverService: IRemoteService? = null
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
        binding.btScopedStorage.onClick {
            nav().navigateAction(R.id.action_main_to_storageFragment)
        }
        binding.btCoordinator.onClick {
            nav().navigateAction(R.id.action_main_to_coordinatorFragment)
        }
        binding.btCountDown.onClick {
            nav().navigateAction(R.id.action_main_to_countDownFragment)
        }
        binding.btDsl.onClick {
            nav().navigateAction(R.id.action_main_to_dslFragment)
        }
        binding.btScrollView.onClick {
            nav().navigateAction(R.id.action_main_to_scrollFragment)
        }
        binding.btPopUp.onClick {
            nav().navigateAction(R.id.action_main_to_popupWindowFragment)
        }
        binding.btCompose.onClick{
            nav().navigateAction(R.id.action_main_to_composeFragment)
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
        bindService()
        bindRemoteService()
    }


    override fun lazyLoadData() {
        super.lazyLoadData()
        getClassLoader()
        mViewModel.testReference()
        LogUtils.debugInfo("get info from remote:" + mIApiCallback?.appleInfo)
    }

    private fun getClassLoader() {
        val classLoader = mActivity.classLoader
        LogUtils.debugInfo("classLoader toString:$classLoader")
    }

    private fun bindService() {
        val intent = Intent(mActivity, RemoteService::class.java)
        mActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun bindRemoteService() {
        val intent = Intent()
        intent.setComponent(
            ComponentName(
                "com.robin.baseframe.service",
                "com.robin.baseframe.service.RemoteObserverService"
            )
        )
        mActivity.bindService(intent, mRemoteServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mIApiCallback = IApiCallBack.Stub.asInterface(service)
            LogUtils.debugInfo("connect to RemoteService")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIApiCallback = null
            LogUtils.debugInfo("disconnect to RemoteService")
        }
    }

    private val mRemoteServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mObserverService = IRemoteService.Stub.asInterface(service)
            mObserverService?.registerCallback(mRemoteCallback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mObserverService?.unregisterCallback(mRemoteCallback)
            mObserverService = null
        }

    }

    private val mRemoteCallback = object : IRemoteServiceCallBack.Stub() {
        override fun noticeAppleInfo(apple: Apple?) {
            LogUtils.debugInfo("noticeAppleInfo apple:$apple")
        }


    }
}