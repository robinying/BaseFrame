/*
 * Copyright (c) 2021. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.robin.baseframe.ext

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Size
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.robin.baseframe.app.base.Ktx
import com.robin.baseframe.app.ext.contentResolver

import java.io.File
import java.io.InputStream
import java.io.OutputStream

lateinit var fileProviderAuthority: String

inline val EXTERNAL_MEDIA_IMAGES_URI: Uri
    get() = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

inline val EXTERNAL_MEDIA_VIDEO_URI: Uri
    get() = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

inline val EXTERNAL_MEDIA_AUDIO_URI: Uri
    get() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

@get:RequiresApi(Build.VERSION_CODES.Q)
inline val EXTERNAL_MEDIA_DOWNLOADS_URI: Uri
    get() = MediaStore.Downloads.EXTERNAL_CONTENT_URI

inline fun File.toUri(authority: String = fileProviderAuthority): Uri =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        FileProvider.getUriForFile(Ktx.app, authority, this)
    } else {
        Uri.fromFile(this)
    }

inline fun <R> Uri.openFileDescriptor(mode: String = "r", block: (ParcelFileDescriptor) -> R): R? =
    contentResolver.openFileDescriptor(this, mode)?.use(block)

inline fun <R> Uri.openInputStream(block: (InputStream) -> R): R? =
    contentResolver.openInputStream(this)?.use(block)

inline fun <R> Uri.openOutputStream(block: (OutputStream) -> R): R? =
    contentResolver.openOutputStream(this)?.use(block)

@RequiresApi(Build.VERSION_CODES.Q)
inline fun Uri.loadThumbnail(width: Int, height: Int, signal: CancellationSignal? = null): Bitmap =
    contentResolver.loadThumbnail(this, Size(width, height), signal)

inline val Uri.fileExtension: String?
    get() = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

inline val Uri.mimeType: String?
    get() = contentResolver.getType(this)
