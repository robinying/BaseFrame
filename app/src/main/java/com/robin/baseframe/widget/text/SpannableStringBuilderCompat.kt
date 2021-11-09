package com.robin.baseframe.widget.text

import android.text.SpannableStringBuilder

class SpannableStringBuilderCompat : SpannableStringBuilder {
    constructor() : super("") {}
    constructor(text: CharSequence) : super(text, 0, text.length) {}
    constructor(text: CharSequence?, start: Int, end: Int) : super(text, start, end) {}

    override fun append(text: CharSequence): SpannableStringBuilderCompat {
        if (text == null) return this
        val length = length
        return replace(length, length, text, 0, text.length) as SpannableStringBuilderCompat
    }

    /** 该方法在原API里面只支持API21或者以上，这里抽取出来以适应低版本  */
    override fun append(text: CharSequence, what: Any, flags: Int): SpannableStringBuilderCompat {
        if (text == null) return this
        val start = length
        append(text)
        setSpan(what, start, length, flags)
        return this
    }
}
