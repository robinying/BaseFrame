package com.robin.baseframe.viewmodel

import android.content.ContentUris
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.robin.baseframe.app.base.LegacyViewModel
import com.robin.baseframe.app.base.appContext
import com.robin.baseframe.app.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StorageViewModel : LegacyViewModel() {

    /** 扫描状态，供页面映射到 [com.robin.baseframe.ui.component.DemoStateView]。 */
    val scanStatus: MutableLiveData<String> = MutableLiveData()

    fun scanMediaFiles() {
        viewModelScope.launch {
            scanStatus.value = STATUS_LOADING
            withContext(Dispatchers.IO) {
                try {
                    val start = System.currentTimeMillis()
                    var imageCount = 0
                    val cursor = appContext.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                        null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc"
                    )
                    cursor?.use {
                        while (it.moveToNext()) {
                            imageCount++
                            val id =
                                it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            val uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                            LogUtils.debugInfo("scan image uri:$uri")
                        }
                    }
                    LogUtils.debugInfo("scan cost time:" + (System.currentTimeMillis() - start))
                    scanStatus.postValue(if (imageCount == 0) STATUS_EMPTY else STATUS_CONTENT)
                } catch (e: Exception) {
                    LogUtils.debugInfo("scan media files failed:${e.message}")
                    scanStatus.postValue(STATUS_ERROR_PREFIX + (e.message ?: ""))
                }
            }
        }
    }

    companion object {
        const val STATUS_LOADING = "loading"
        const val STATUS_EMPTY = "empty"
        const val STATUS_CONTENT = "content"
        const val STATUS_ERROR_PREFIX = "error:"
    }
}
