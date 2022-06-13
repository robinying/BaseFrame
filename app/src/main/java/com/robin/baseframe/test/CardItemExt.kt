package com.robin.baseframe.test

import com.robin.baseframe.databinding.CardItemBinding

fun CardItemBinding.bind(imageResId: Int,nameStr: String, descStr: String){
    avatar.setImageResource(imageResId)
    name.text = nameStr
    des.text = descStr
}