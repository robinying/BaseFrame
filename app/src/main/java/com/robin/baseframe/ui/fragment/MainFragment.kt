package com.robin.baseframe.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
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
import com.robin.baseframe.data.repository.DemoPreferenceRepository
import com.robin.baseframe.databinding.FragmentMainBinding
import com.robin.baseframe.service.RemoteObserverService
import com.robin.baseframe.service.RemoteService
import com.robin.baseframe.ui.adapter.DemoCatalogAdapter
import com.robin.baseframe.ui.home.DemoAction
import com.robin.baseframe.ui.home.DemoCatalog
import com.robin.baseframe.ui.home.DemoCatalogRow
import com.robin.baseframe.ui.home.DemoCategory
import com.robin.baseframe.viewmodel.MainViewModel
import kotlinx.coroutines.launch

/** Displays the demo catalog and manages its AIDL service connections. */
class MainFragment : LegacyBaseFragment<MainViewModel, FragmentMainBinding>() {
    private lateinit var mDemoPreferenceRepository: DemoPreferenceRepository
    private var mIApiCallback: IApiCallBack? = null
    private var mObserverService: IRemoteService? = null
    private var mIsApiServiceBound = false
    private var mIsObserverServiceBound = false
    private var mIsObserverCallbackRegistered = false
    private var searchQuery: String = ""
    private var selectedCategory: DemoCategory? = null

    override fun initView(savedInstanceState: Bundle?) {
        mDemoPreferenceRepository = DemoPreferenceRepository(requireContext().applicationContext)
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

        bindApiService()
        bindObserverService()
    }

    override fun onDestroyView() {
        unbindObserverService()
        unbindApiService()
        super.onDestroyView()
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
        openDemo(row.item.id)
    }

    /** Opens a catalog demo using its stable identifier. */
    fun openDemo(demoId: String) {
        val item = DemoCatalog.findById(demoId) ?: return
        if (!item.isAvailable) {
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mDemoPreferenceRepository.recordRecent(item.id)
        }
        when (val action = item.action) {
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

    private fun bindApiService() {
        if (mIsApiServiceBound) {
            return
        }
        val intent = Intent(mActivity, RemoteService::class.java)
        mIsApiServiceBound = mActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        if (!mIsApiServiceBound) {
            LogUtils.warnInfo(TAG, "Unable to bind RemoteService")
        }
    }

    private fun bindObserverService() {
        if (mIsObserverServiceBound) {
            return
        }
        val intent = Intent(mActivity, RemoteObserverService::class.java)
        mIsObserverServiceBound = mActivity.bindService(
            intent,
            mRemoteServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        if (!mIsObserverServiceBound) {
            LogUtils.warnInfo(TAG, "Unable to bind RemoteObserverService")
        }
    }

    private fun unbindApiService() {
        if (!mIsApiServiceBound) {
            return
        }
        try {
            mActivity.unbindService(mServiceConnection)
        } catch (exception: IllegalArgumentException) {
            LogUtils.warnInfo(TAG, "RemoteService was already unbound")
        } finally {
            mIsApiServiceBound = false
            mIApiCallback = null
        }
    }

    private fun unbindObserverService() {
        unregisterObserverCallback()
        if (mIsObserverServiceBound) {
            try {
                mActivity.unbindService(mRemoteServiceConnection)
            } catch (exception: IllegalArgumentException) {
                LogUtils.warnInfo(TAG, "RemoteObserverService was already unbound")
            }
        }
        mIsObserverServiceBound = false
        mObserverService = null
    }

    private fun unregisterObserverCallback() {
        if (!mIsObserverCallbackRegistered) {
            return
        }
        try {
            mObserverService?.unregisterCallback(mRemoteCallback)
        } catch (exception: RemoteException) {
            LogUtils.warnInfo(TAG, "Unable to unregister RemoteObserverService callback")
        } finally {
            mIsObserverCallbackRegistered = false
        }
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (!mIsApiServiceBound) {
                return
            }
            mIApiCallback = IApiCallBack.Stub.asInterface(service)
            LogUtils.debugInfo("connect to RemoteService")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mIApiCallback = null
            LogUtils.debugInfo("disconnect to RemoteService")
        }

        override fun onBindingDied(name: ComponentName?) {
            mIApiCallback = null
            mIsApiServiceBound = false
            if (view != null) {
                bindApiService()
            }
        }

        override fun onNullBinding(name: ComponentName?) {
            mIApiCallback = null
        }
    }

    private val mRemoteServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (!mIsObserverServiceBound) {
                return
            }
            mObserverService = IRemoteService.Stub.asInterface(service)
            try {
                mObserverService?.registerCallback(mRemoteCallback)
                mIsObserverCallbackRegistered = true
            } catch (exception: RemoteException) {
                mObserverService = null
                LogUtils.warnInfo(TAG, "Unable to register RemoteObserverService callback")
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mObserverService = null
            mIsObserverCallbackRegistered = false
            LogUtils.debugInfo("disconnect to RemoteObserverService")
        }

        override fun onBindingDied(name: ComponentName?) {
            mObserverService = null
            mIsObserverCallbackRegistered = false
            mIsObserverServiceBound = false
            if (view != null) {
                bindObserverService()
            }
        }

        override fun onNullBinding(name: ComponentName?) {
            mObserverService = null
            mIsObserverCallbackRegistered = false
        }
    }

    private val mRemoteCallback = object : IRemoteServiceCallBack.Stub() {
        override fun noticeAppleInfo(apple: Apple?) {
            LogUtils.debugInfo("noticeAppleInfo apple:$apple")
        }
    }

    private companion object {
        const val TAG = "MainFragment"
    }
}
