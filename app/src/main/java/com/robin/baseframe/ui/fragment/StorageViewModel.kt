package com.robin.baseframe.ui.fragment

import android.content.ContentUris
import android.media.Image
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.robin.baseframe.app.base.BaseViewModel
import com.robin.baseframe.app.base.Ktx
import com.robin.baseframe.app.base.appContext
import com.robin.baseframe.app.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StorageViewModel : BaseViewModel() {

    fun scanMediaFiles() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val start = System.currentTimeMillis()
                val cursor = appContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    null, null, "${MediaStore.MediaColumns.DATE_ADDED} desc"
                )
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        LogUtils.debugInfo("scan image uri:$uri")
                    }
                    cursor.close()
                }
                LogUtils.debugInfo("scan cost time:" + (System.currentTimeMillis() - start))

            }

        }
    }


}