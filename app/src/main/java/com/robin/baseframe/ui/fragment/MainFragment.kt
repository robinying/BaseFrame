package com.robin.baseframe.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.core.widget.doAfterTextChanged
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
import com.robin.baseframe.ui.home.DemoCategory
import com.robin.baseframe.viewmodel.MainViewModel

class MainFragment : LegacyBaseFragment<MainViewModel, FragmentMainBinding>() {
    private var mIApiCallback: IApiCallBack? = null
    private var mObserverService: IRemoteService? = null
    private var searchQuery: String = ""
    private var selectedCategory: DemoCategory? = null

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
        binding.demoSearchInput.setText(searchQuery)
        binding.demoSearchInput.doAfterTextChanged {
            searchQuery = it?.toString().orEmpty()
            updateCatalog(adapter)
        }
        binding.demoFilterGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedCategory = when (checkedId) {
                R.id.filter_overlay -> DemoCategory.OVERLAY
                R.id.filter_layout -> DemoCategory.LAYOUT
                R.id.filter_state -> DemoCategory.STATE
                R.id.filter_device -> DemoCategory.DEVICE
                else -> null
            }
            updateCatalog(adapter)
        }
        binding.demoFilterGroup.check(
            when (selectedCategory) {
                DemoCategory.OVERLAY -> R.id.filter_overlay
                DemoCategory.LAYOUT -> R.id.filter_layout
                DemoCategory.STATE -> R.id.filter_state
                DemoCategory.DEVICE -> R.id.filter_device
                null -> R.id.filter_all
            }
        )
        updateCatalog(adapter)

        bindService()
        bindRemoteService()
    }

    private fun updateCatalog(adapter: DemoCatalogAdapter) {
        val filteredItems = DemoCatalog.filter(
            query = searchQuery,
            category = selectedCategory,
            searchableText = { item ->
                getString(item.titleRes) + " " + getString(item.summaryRes)
            }
        )
        binding.homeDemoCount.text = getString(R.string.home_demo_count, filteredItems.size)
        binding.demoEmptyState.visibility = if (filteredItems.isEmpty()) View.VISIBLE else View.GONE
        binding.demoList.visibility = if (filteredItems.isEmpty()) View.GONE else View.VISIBLE
        adapter.submitList(DemoCatalog.rows(filteredItems))
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
