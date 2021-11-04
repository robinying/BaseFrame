package com.robin.baseframe.app.network


import com.robin.baseframe.app.network.interceptor.logging.LogInterceptor
import com.robin.baseframe.app.util.ZipHelper
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

/**
 * 自定义头部参数拦截器，传入heads
 */
class MyInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
//        if (!TokenCache.getToken().isNullOrEmpty()) {
//            builder.addHeader("Authorization", TokenCache.getToken()!!)
//        }
        val response = chain.proceed(builder.build())
//        val token = response.header("Authorization")
//        if (!token.isNullOrEmpty()) {
//            TokenCache.setToken(token)
//        }
        // 对返回response统一拦截
//        if (response != null) {
//            val responseBody = response.newBuilder().build().body()
//            val source = responseBody!!.source()
//            source.request(Long.MAX_VALUE) // Buffer the entire body.
//            val buffer = source.buffer()
//
//            //获取content的压缩类型
//            val encoding = response
//                .headers()["Content-Encoding"]
//            val clone = buffer.clone()
//            val responseStr = parseContent(responseBody, encoding, clone)
//            if (responseStr != null) {
//                val jsonElement = JsonParser.parseString(responseStr)
//                if (jsonElement != null && jsonElement is JsonObject) {
//                    try {
//                        val jsonObject = jsonElement.asJsonObject
//                        if (jsonObject.has("msg") && jsonObject.has("status")) {
//                            val status = jsonObject["status"].asInt
//                            val message = jsonObject["msg"].asString
//                            LogUtils.debugInfo("status:$status")
//                            if (status == 502 && message != null && message.contains("请您先登录")) {
//                                ToastUtils.showShort("登录过期，请重新登录")
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        }
        return response
    }

    /**
     * 解析服务器响应的内容
     *
     * @param responseBody [ResponseBody]
     * @param encoding     编码类型
     * @param clone        克隆后的服务器响应内容
     * @return 解析后的响应结果
     */
    private fun parseContent(
        responseBody: ResponseBody?,
        encoding: String?,
        clone: Buffer
    ): String? {
        var charset = Charset.forName("UTF-8")
        val contentType = responseBody!!.contentType()
        if (contentType != null) {
            charset = contentType.charset(charset)
        }
        //content 使用 gzip 压缩
        return if ("gzip".equals(encoding, ignoreCase = true)) {
            //解压
            ZipHelper.decompressForGzip(
                clone.readByteArray(),
                LogInterceptor.convertCharset(charset)
            )
        } else if ("zlib".equals(encoding, ignoreCase = true)) {
            //content 使用 zlib 压缩
            ZipHelper.decompressToStringForZlib(
                clone.readByteArray(),
                LogInterceptor.convertCharset(charset)
            )
        } else {
            //content 没有被压缩, 或者使用其他未知压缩方式
            clone.readString(charset)
        }
    }
}