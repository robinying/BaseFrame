package com.robin.baseframe.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.recyclerview.widget.GridLayoutManager
import com.robin.aidldemo.Apple
import com.robin.aidldemo.IApiCallBack
import com.robin.aidldemo.IRemoteService
import com.robin.aidldemo.IRemoteServiceCallBack
import com.robin.baseframe.R
import com.robin.baseframe.app.base.LegacyBaseFragment
import com.robin.baseframe.app.ext.nav
import com.robin.baseframe.app.ext.navigateAction
import com.robin.baseframe.app.ext.showDialogFragment
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentMainBinding
import com.robin.baseframe.service.RemoteService
import com.robin.baseframe.ui.adapter.DemoCatalogAdapter
import com.robin.baseframe.ui.home.DemoAction
import com.robin.baseframe.ui.home.DemoCatalog
import com.robin.baseframe.ui.home.DemoCatalogRow
import com.robin.baseframe.viewmodel.MainViewModel

class MainFragment : LegacyBaseFragment<MainViewModel, FragmentMainBinding>() {
    private var mIApiCallback: IApiCallBack? = null
    private var mObserverService: IRemoteService? = null

    override fun initView(savedInstanceState: Bundle?) {
        val adapter = DemoCatalogAdapter(::onDemoClicked)
        val gridLayoutManager = GridLayoutManager(
            requireContext(),
            resources.getInteger(R.integer.home_grid_span_count)
        )
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (adapter.isCategory(position)) gridLayoutManager.spanCount else 1
        }

        binding.demoList.layoutManager = gridLayoutManager
        binding.demoList.adapter = adapter
        binding.homeDemoCount.text = getString(R.string.home_demo_count, DemoCatalog.items.size)
        adapter.submitList(DemoCatalog.rows())

        bindService()
        bindRemoteService()
    }

    private fun onDemoClicked(row: DemoCatalogRow.DemoItem) {
        when (val action = row.item.action) {
            DemoAction.ShowDialog -> showDialogFragment(BottomDialog())
            is DemoAction.Navigate -> nav().navigateAction(action.destinationAction)
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        getClassLoader()
        mViewModel.testReference()
        LogUtils.debugInfo("get info from remote:" + mIApiCallback?.appleInfo)
    }

    private fun getClassLoader() {
        LogUtils.debugInfo("classLoader toString:${mActivity.classLoader}")
    }

    private fun bindService() {
        val intent = Intent(mActivity, RemoteService::class.java)
        mActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun bindRemoteService() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.robin.baseframe.service",
                "com.robin.baseframe.service.RemoteObserverService"
            )
        }
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
