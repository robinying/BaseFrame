package com.robin.baseframe.ui.fragment

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.robin.baseframe.app.base.BaseFragment
import com.robin.baseframe.app.ext.view.onClick
import com.robin.baseframe.app.util.LogUtils
import com.robin.baseframe.databinding.FragmentStorageBinding

class StorageFragment : BaseFragment<StorageViewModel, FragmentStorageBinding>() {
    private val requestDataLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
            if (result != null) {
                LogUtils.debugInfo("uri:$result")
            }
        }

    override fun initView(savedInstanceState: Bundle?) {
        val cacheDir = mActivity.cacheDir.absolutePath
        val files = mActivity.filesDir.absolutePath
        val codeCache = mActivity.codeCacheDir.absolutePath
        val externalCacheFile = mActivity.externalCacheDir?.absolutePath
        val mediaFiles = mActivity.externalMediaDirs
        val externalFiles = mActivity.getExternalFilesDirs("Download")
        val testFiles = mActivity.getExternalFilesDirs("Test")
        LogUtils.debugInfo("testFiles size:" + testFiles.size)
        val internalSb = StringBuilder()
        internalSb.append("cacheDir: ")
            .append(cacheDir + "\n")
            .append("files: $files\n")
            .append("codeCache : $codeCache\n")
            .append("externalCacheFile: $externalCacheFile\n")
        LogUtils.debugInfo("media size:" + mediaFiles.size)
        for (file in mediaFiles) {
            internalSb.append(file.absolutePath + "\n")
        }
        for (file in testFiles) {
            LogUtils.debugInfo("test file path:" + file.absolutePath)
        }

        binding.tvInternalStorage.text = internalSb.toString()
        binding.btSelect.onClick {
            startSelectFile()
        }
    }

    override fun lazyLoadData() {
        super.lazyLoadData()
        mViewModel.scanMediaFiles()
    }

    private fun startSelectFile() {
        requestDataLauncher.launch(arrayOf("image/*"))
    }


}