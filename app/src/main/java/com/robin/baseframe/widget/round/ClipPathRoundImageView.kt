package com.robin.baseframe.widget.round

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.robin.baseframe.app.ext.util.dp2px


class ClipPathRoundImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    var width = 0f
    var height = 0f
    private val mRoundRadius: Int = dp2px(15f)

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        if (width > mRoundRadius && height > mRoundRadius) {
            val path = Path()
            path.moveTo(mRoundRadius.toFloat(), 0f)
            path.lineTo(width - mRoundRadius, 0f) //上侧的横线
            path.quadTo(width, 0f, width, mRoundRadius.toFloat()) //右上的圆弧曲线
            path.lineTo(width, height - mRoundRadius) //右侧的直线
            path.quadTo(width, height, width - mRoundRadius, height) //右下的圆弧曲线
            path.lineTo(mRoundRadius.toFloat(), height) //下侧的横线
            path.quadTo(0f, height, 0f, height - mRoundRadius) //左下的圆弧曲线
            path.lineTo(0f, mRoundRadius.toFloat()) //左侧的直线
            path.quadTo(0f, 0f, mRoundRadius.toFloat(), 0f) //左上的圆弧曲线
            canvas.clipPath(path)
        }
        super.onDraw(canvas)
    }

    init {
        //在api11到api18之间设置禁用硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
        ) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }
}