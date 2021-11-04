package com.robin.baseframe.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object PhoneUtil {
    fun callPhone(context: Context, phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data: Uri = Uri.parse("tel:$phoneNum")
        intent.data = data
        context.startActivity(intent)
    }
}
