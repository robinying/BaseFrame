package com.robin.baseframe.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

object ImageUtils {

    private val MAX_WIDTH = 100
    private val MAX_HEIGHT = 100

    /**
     * 加载本地图片
     * @param reuse 可以复用的Bitmap内存
     */
    fun load(imagePath: String, reuse: Bitmap?): Bitmap {

        val option = BitmapFactory.Options()
        option.inMutable = true
        option.inJustDecodeBounds = true

        BitmapFactory.decodeFile(imagePath, option)
        val outHeight = option.outHeight
        val outWidth = option.outWidth
        option.inSampleSize = calculateSampleSize(outWidth, outHeight, MAX_WIDTH, MAX_HEIGHT)

        option.inJustDecodeBounds = false
        option.inBitmap = reuse
        //新创建的Bitmap复用这块内存
        return BitmapFactory.decodeFile(imagePath, option)
    }

    private fun calculateSampleSize(
        outWidth: Int,
        outHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        var sampleSize = 1
        Log.e("TAG", "outWidth $outWidth outHeight $outHeight")
        if (outWidth > maxWidth && outHeight > maxHeight) {
            sampleSize = 2
            while (outWidth / sampleSize > maxWidth && outHeight / sampleSize > maxHeight) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }
}
