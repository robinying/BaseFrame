package com.robin.baseframe.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.robin.baseframe.R

/*
* 去除TextView的边距
* */
class NoPaddingTextView : AppCompatTextView {
    private val mPaint: Paint = paint
    private val mBounds: Rect = Rect()
    private var mRemoveFontPadding = false //是否去除字体内边距，true：去除 false：不去除

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initAttributes(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mRemoveFontPadding) {
            calculateTextParams()
            setMeasuredDimension(mBounds.right - mBounds.left, -mBounds.top + mBounds.bottom)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        drawText(canvas)
    }

    /**
     * 初始化属性
     */
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.NoPaddingTextView)
        mRemoveFontPadding = typedArray.getBoolean(R.styleable.NoPaddingTextView_removeDefaultPadding, false)
        typedArray.recycle()
    }

    /**
     * 计算文本参数
     */
    private fun calculateTextParams(): String {
        val text = text.toString()
        val textLength = text.length
        mPaint.getTextBounds(text, 0, textLength, mBounds)
        if (textLength == 0) {
            mBounds.right = mBounds.left
        }
        return text
    }

    /**
     * 绘制文本
     */
    private fun drawText(canvas: Canvas) {
        val text = calculateTextParams()
        val left: Int = mBounds.left
        val bottom: Int = mBounds.bottom
        mBounds.offset(-mBounds.left, -mBounds.top)
        mPaint.isAntiAlias = true
        mPaint.color = currentTextColor
        canvas.drawText(text, (-left).toFloat(), (mBounds.bottom - bottom) as Float, mPaint)
    }
}