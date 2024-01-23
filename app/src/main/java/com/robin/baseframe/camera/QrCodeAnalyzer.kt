package com.robin.baseframe.camera

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(private val onQrCodeScanned: (result: String?) -> Unit) : ImageAnalysis.Analyzer {
    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )


    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }

    override fun analyze(image: ImageProxy) {
        //检查扫描图像代理的格式是否在支持的格式当中
        if (image.format in supportedImageFormats) {
            //获取原始数据
            val bytes = image.planes.first().buffer.toByteArray()
            //创建 ZXing 用于解码的亮度源
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            //创建二进制bitmap
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBitmap)

                //回调结果出去
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                image.close()
            }
        }
    }

}