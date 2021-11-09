package com.robin.baseframe.widget.text

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.text.NoCopySpan
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.SuperscriptSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import com.robin.baseframe.R
import com.robin.baseframe.app.ext.util.dp2px
import com.robin.baseframe.widget.ColorUtil
import com.robin.baseframe.widget.text.Type.Companion.RECTANGE
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class DPTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) :
    AppCompatTextView(context, attrs, defStyleAttr), Type {
    var normalTextColor = Color.WHITE
    var pressedTextColor = normalTextColor
    var disableTextColor = Color.GRAY
    var strokeColor = Color.TRANSPARENT
    var normalBackgroundColor = Color.TRANSPARENT
    var pressedBackgroundColor = 0
    var disableBackgroundColor = 0
    var isStrokeMode = false
    var pressWithStrokeMode = true
    var strokeWidth: Int = dp2px(0.5f)
    var radius = 0
    var topLeftRadius = 0
    var topRightRadius = 0
    var bottomLeftRadius = 0
    var bottomRightRadius = 0
    var drawableWidth = 0
    var drawableHeight = 0
    var drawableScale = 0f
    var type: Int = Type.RECTANGE
        private set
    var isUrlRegion = false
    private val mOnUrlClickListener: OnUrlClickListener? = null
    private val applyRunnable = Runnable { apply() }

    /*
     * 正则文本
     * ((http|ftp|https)://)(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\&%_\./-~-]*)?|(([a-zA-Z0-9\._-]+\.[a-zA-Z]{2,6})|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\&%_\./-~-]*)?
     * */
    private val pattern =
        "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?|(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?"

    // 创建 Pattern 对象
    var r = Pattern.compile(pattern)

    // 现在创建 matcher 对象
    var m: Matcher? = null

    //记录网址的list
    var mStringList: LinkedList<String>? = null

    //记录该网址所在位置的list
    var mUrlInfos: LinkedList<UrlInfo>? = null
    var flag = Spanned.SPAN_POINT_MARK

    companion object {
        private const val TAG = "DPTextView"
    }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DPTextView)
        var background: Drawable? = null
        background = a.getDrawable(R.styleable.DPTextView_android_background)
        if (background != null) {
            type = Type.RECTANGE
            setBackground(this, background)
            a.recycle()
            return
        } else {
            applyAttrs(context, a)
        }
        a.recycle()
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (lineCount > 1) {
                    if (lineSpacingExtra == 0f && lineSpacingMultiplier == 1f) {
                        setLineSpacing(dp2px(1.2f).toFloat(), 1.2f)
                    }
                }
                viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
    }

    private fun applyAttrs(context: Context?, a: TypedArray) {
        type = a.getInt(R.styleable.DPTextView_type, Type.RECTANGE)
        normalTextColor = a.getColor(R.styleable.DPTextView_android_textColor, normalTextColor)
        pressedTextColor = a.getColor(R.styleable.DPTextView_textPressedColor, normalTextColor)
        disableTextColor = a.getColor(
            R.styleable.DPTextView_textDisableColor,
            ColorUtil.alphaColor(0.3f, normalTextColor)
        )
        strokeColor = a.getColor(R.styleable.DPTextView_strokeColor, strokeColor)
        normalBackgroundColor =
            a.getColor(R.styleable.DPTextView_backgroundColor, normalBackgroundColor)
        pressedBackgroundColor = a.getColor(
            R.styleable.DPTextView_backgroundPressedColor,
            ColorUtil.brightnessColor(normalBackgroundColor, ColorUtil.DEFAULT_BRIGHTNESS)
        )
        disableBackgroundColor = a.getColor(
            R.styleable.DPTextView_backgroundDisableColor,
            ColorUtil.alphaColor(0.3f, normalBackgroundColor)
        )
        isStrokeMode = a.getBoolean(R.styleable.DPTextView_strokeMode, false)
        pressWithStrokeMode =
            a.getBoolean(R.styleable.DPTextView_pressWithStrokeMode, pressWithStrokeMode)
        strokeWidth = a.getDimensionPixelSize(R.styleable.DPTextView_stroke_Width, strokeWidth)
        radius = a.getDimensionPixelSize(R.styleable.DPTextView_corner_radius, radius)
        topLeftRadius =
            a.getDimensionPixelSize(R.styleable.DPTextView_corner_topLeftRadius, topLeftRadius)
        topRightRadius =
            a.getDimensionPixelSize(R.styleable.DPTextView_corner_topRightRadius, topRightRadius)
        bottomLeftRadius = a.getDimensionPixelSize(
            R.styleable.DPTextView_corner_bottomLeftRadius,
            bottomLeftRadius
        )
        bottomRightRadius = a.getDimensionPixelSize(
            R.styleable.DPTextView_corner_bottomRightRadius,
            bottomRightRadius
        )
        drawableWidth = a.getDimensionPixelSize(R.styleable.DPTextView_drawableWidth, drawableWidth)
        drawableHeight =
            a.getDimensionPixelSize(R.styleable.DPTextView_drawableHeight, drawableHeight)
        drawableScale = a.getFloat(R.styleable.DPTextView_drawableScale, drawableScale)
        isUrlRegion = a.getBoolean(R.styleable.DPTextView_urlRegion, false)
        if (drawableWidth > 0 || drawableHeight > 0 || drawableScale > 0) {
            val drawables = compoundDrawables
            if (drawables.size == 4) {
                var hasDrawable = false
                for (drawable in drawables) {
                    if (drawable != null) {
                        hasDrawable = true
                        if (drawableWidth > 0 || drawableHeight > 0) {
                            drawable.setBounds(
                                0,
                                0,
                                if (drawableWidth > 0) drawableWidth else drawable.intrinsicWidth,
                                if (drawableHeight > 0) drawableHeight else drawable.intrinsicHeight
                            )
                        } else if (drawableScale > 0) {
                            drawable.setBounds(
                                0,
                                0,
                                Math.round(drawable.intrinsicWidth * drawableScale),
                                Math.round(drawable.intrinsicHeight * drawableScale)
                            )
                        }
                    }
                }
                if (hasDrawable) {
                    setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
                }
            }
        }
        apply()
    }

    //因为可能会set很多参数，因此这里采取10毫秒延迟检查，在16ms内，不影响.
    private fun applyValue() {
        removeCallbacks(applyRunnable)
        postDelayed(applyRunnable, 10)
    }

    fun apply() {
        var shape = GradientDrawable.RECTANGLE
        when (type) {
            Type.RECTANGE -> shape = GradientDrawable.RECTANGLE
            Type.OVAL -> shape = GradientDrawable.OVAL
        }
        val normalDrawable = generateDrawable(shape, normalBackgroundColor)
        val pressedDrawable = generateDrawable(shape, pressedBackgroundColor)
        if (isStrokeMode && !pressWithStrokeMode) {
            pressedDrawable.setColor(pressedBackgroundColor)
        }
        val disableDrawable = generateDrawable(shape, disableBackgroundColor)
        val backgroundStateListDrawable = StateListDrawable()
        backgroundStateListDrawable.addState(
            intArrayOf(android.R.attr.state_pressed),
            pressedDrawable
        )
        backgroundStateListDrawable.addState(
            intArrayOf(android.R.attr.state_focused),
            pressedDrawable
        )
        backgroundStateListDrawable.addState(
            intArrayOf(-android.R.attr.state_enabled),
            disableDrawable
        )
        backgroundStateListDrawable.addState(intArrayOf(), normalDrawable)
        val textColorState = arrayOfNulls<IntArray>(4)
        textColorState[0] = intArrayOf(android.R.attr.state_pressed)
        textColorState[1] = intArrayOf(android.R.attr.state_focused)
        textColorState[2] = intArrayOf(-android.R.attr.state_enabled)
        textColorState[3] = intArrayOf()
        val textColors =
            intArrayOf(pressedTextColor, pressedTextColor, disableTextColor, normalTextColor)
        setBackground(this, backgroundStateListDrawable)
        setTextColor(ColorStateList(textColorState, textColors))
    }

    private fun generateDrawable(shape: Int, color: Int): GradientDrawable {
        val result = GradientDrawable()
        result.shape = shape
        if (isStrokeMode) {
            result.setColor(Color.TRANSPARENT)
        } else {
            result.setColor(color)
        }
        result.setStroke(strokeWidth, if (strokeColor == Color.TRANSPARENT) color else strokeColor)
        if (type == RECTANGE) {
            if (radius > 0) {
                result.cornerRadius = radius.toFloat()
            } else if (topLeftRadius > 0 || topRightRadius > 0 || bottomLeftRadius > 0 || bottomRightRadius > 0) {
                result.cornerRadii = floatArrayOf(
                    topLeftRadius.toFloat(),
                    topLeftRadius.toFloat(),
                    topRightRadius.toFloat(),
                    topRightRadius.toFloat(),
                    bottomRightRadius.toFloat(),
                    bottomRightRadius.toFloat(),
                    bottomLeftRadius.toFloat(),
                    bottomLeftRadius
                        .toFloat()
                )
            }
        }
        return result
    }

    override fun setText(text: CharSequence, type: BufferType) {
        var text = text
        clearPointSpan()
        if (isUrlRegion) {
            text = recognUrl(text)
            this.movementMethod = LinkMovementMethod.getInstance()
        }
        super.setText(text, type)
    }

    private fun recognUrl(text: CharSequence): SpannableStringBuilderCompat {
        var text: CharSequence? = text
        mStringList!!.clear()
        mUrlInfos!!.clear()
        val contextText: CharSequence
        val clickText: CharSequence?
        text = text ?: ""
        //以下用于拼接本来存在的spanText
        val span = SpannableStringBuilderCompat(text)
        val clickableSpans: Array<ClickableSpan> = span.getSpans(
            0, text.length,
            ClickableSpan::class.java
        )
        if (clickableSpans.size > 0) {
            var start = 0
            var end = 0
            for (i in clickableSpans.indices) {
                start = span.getSpanStart(clickableSpans[0])
                end = span.getSpanEnd(clickableSpans[i])
            }
            //可点击文本后面的内容页
            contextText = text.subSequence(end, text.length)
            //可点击文本
            clickText = text.subSequence(
                start,
                end
            )
        } else {
            contextText = text
            clickText = null
        }
        m = r.matcher(contextText)
        //匹配成功
        while (m!!.find()) {
            //得到网址数
            val info = UrlInfo()
            info.start = m!!.start()
            info.end = m!!.end()
            mStringList!!.add(m!!.group())
            mUrlInfos!!.add(info)
        }
        return jointText(clickText, contextText)
    }

    /**
     * 拼接文本
     */
    private fun jointText(
        clickSpanText: CharSequence?,
        contentText: CharSequence
    ): SpannableStringBuilderCompat {
        val spanBuilder: SpannableStringBuilderCompat
        if (clickSpanText != null) {
            spanBuilder = SpannableStringBuilderCompat(clickSpanText)
        } else {
            spanBuilder = SpannableStringBuilderCompat()
        }
        if (mStringList!!.size > 0) {
            //只有一个网址
            if (mStringList!!.size == 1) {
                val preStr = contentText.toString().substring(0, mUrlInfos!![0].start)
                spanBuilder.append(preStr)
                val url = mStringList!![0]
                spanBuilder.append(url, URLClick(url), flag)
                val nextStr = contentText.toString().substring(mUrlInfos!![0].end)
                spanBuilder.append(nextStr)
            } else {
                //有多个网址
                for (i in mStringList!!.indices) {
                    if (i == 0) {
                        //拼接第1个span的前面文本
                        val headStr = contentText.toString().substring(0, mUrlInfos!![0].start)
                        spanBuilder.append(headStr)
                    }
                    if (i == mStringList!!.size - 1) {
                        //拼接最后一个span的后面的文本
                        spanBuilder.append(
                            mStringList!![i], URLClick(mStringList!![i]),
                            flag
                        )
                        val footStr = contentText.toString().substring(mUrlInfos!![i].end)
                        spanBuilder.append(footStr)
                    }
                    if (i != mStringList!!.size - 1) {
                        //拼接两两span之间的文本
                        spanBuilder.append(mStringList!![i], URLClick(mStringList!![i]), flag)
                        val betweenStr = contentText.toString()
                            .substring(
                                mUrlInfos!![i].end,
                                mUrlInfos!![i + 1].start
                            )
                        spanBuilder.append(betweenStr)
                    }
                }
            }
        } else {
            spanBuilder.append(contentText)
        }
        return spanBuilder
    }

    fun setNormalTextColor(normalTextColor: Int): DPTextView {
        this.normalTextColor = normalTextColor
        applyValue()
        return this
    }

    fun setPressedTextColor(pressedTextColor: Int): DPTextView {
        this.pressedTextColor = pressedTextColor
        applyValue()
        return this
    }

    fun setDisableTextColor(disableTextColor: Int): DPTextView {
        this.disableTextColor = disableTextColor
        applyValue()
        return this
    }

    fun setNormalBackgroundColor(normalBackgroundColor: Int): DPTextView {
        this.normalBackgroundColor = normalBackgroundColor
        pressedBackgroundColor =
            ColorUtil.brightnessColor(normalBackgroundColor, ColorUtil.DEFAULT_BRIGHTNESS)
        applyValue()
        return this
    }

    fun setPressedBackgroundColor(pressedBackgroundColor: Int): DPTextView {
        this.pressedBackgroundColor = pressedBackgroundColor
        applyValue()
        return this
    }

    fun setDisableBackgroundColor(disableBackgroundColor: Int): DPTextView {
        this.disableBackgroundColor = disableBackgroundColor
        applyValue()
        return this
    }

    fun setStrokeMode(strokeMode: Boolean): DPTextView {
        isStrokeMode = strokeMode
        applyValue()
        return this
    }

    fun setStrokeWidth(strokeWidth: Int): DPTextView {
        this.strokeWidth = strokeWidth
        applyValue()
        return this
    }

    fun setRadius(radius: Int): DPTextView {
        this.radius = radius
        applyValue()
        return this
    }

    fun setTopLeftRadius(topLeftRadius: Int): DPTextView {
        this.topLeftRadius = topLeftRadius
        applyValue()
        return this
    }

    fun setTopRightRadius(topRightRadius: Int): DPTextView {
        this.topRightRadius = topRightRadius
        applyValue()
        return this
    }

    fun setBottomLeftRadius(bottomLeftRadius: Int): DPTextView {
        this.bottomLeftRadius = bottomLeftRadius
        applyValue()
        return this
    }

    fun setBottomRightRadius(bottomRightRadius: Int): DPTextView {
        this.bottomRightRadius = bottomRightRadius
        applyValue()
        return this
    }

    fun setType(type: Int): DPTextView {
        this.type = type
        applyValue()
        return this
    }

    fun setStrokeColor(strokeColor: Int): DPTextView {
        this.strokeColor = strokeColor
        applyValue()
        return this
    }

    override fun setTextColor(color: Int) {
        setNormalTextColor(color)
        super.setTextColor(color)
    }

    class UrlInfo {
        var start = 0
        var end = 0
    }

    private inner class URLClick(private val text: String) : ClickableSpan() {
        override fun onClick(widget: View) {
            if (mOnUrlClickListener != null) {
                if (mOnUrlClickListener.onUrlClickListener(text)) return
            }
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val content_url = Uri.parse(text)
            intent.data = content_url
            context.startActivity(intent)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = -0xae8052
            ds.isUnderlineText = false
        }
    }

    interface OnUrlClickListener {
        fun onUrlClickListener(url: String?): Boolean
    }

    fun setLoadingText() {
        setLoadingText("...")
    }

    fun setLoadingText(text: CharSequence) {
        setLoadingText(text, 1500)
    }

    fun setLoadingText(text: CharSequence, loopDuration: Int) {
        clearPointSpan()
        val builderCompat = SpannableStringBuilderCompat(text)
        val length: Int = builderCompat.length
        //延迟
        val delay = (loopDuration / length * 0.5).toInt()
        for (i in 0 until length) {
            val span: LoadingPointSpan = LoadingPointSpan(delay * i, loopDuration, 0.4f)
            builderCompat.setSpan(span, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        super.setText(builderCompat)
    }

    override fun onDetachedFromWindow() {
        clearPointSpan()
        super.onDetachedFromWindow()
    }

    private fun stopPointSpan() {
        if (text is Spanned) {
            val span = (text as Spanned).getSpans(
                0, text.length,
                LoadingPointSpan::class.java
            )
            if (span != null) {
                for (loadingPointSpan in span) {
                    loadingPointSpan.stop()
                }
            }
        }
    }

    private fun startPointSpan() {
        if (text is Spanned) {
            val span = (text as Spanned).getSpans(
                0, text.length,
                LoadingPointSpan::class.java
            )
            if (span != null) {
                for (loadingPointSpan in span) {
                    loadingPointSpan.start()
                }
            }
        }
    }

    private fun clearPointSpan() {
        if (text is Spanned) {
            val span = (text as Spanned).getSpans(
                0, text.length,
                LoadingPointSpan::class.java
            )
            if (span != null) {
                for (loadingPointSpan in span) {
                    loadingPointSpan.clear()
                }
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startPointSpan()
        } else {
            stopPointSpan()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        startPointSpan()
    }

    override fun onSaveInstanceState(): Parcelable? {
        stopPointSpan()
        return super.onSaveInstanceState()
    }

    /**
     * textview小点点span，内封动画（注意内存泄漏）
     */
    internal inner class LoadingPointSpan(
        private val delay: Int, private val duration: Int, //最高弹跳高度百分比
        private var maxOffsetRatio: Float
    ) :
        SuperscriptSpan(), AnimatorUpdateListener, Animator.AnimatorListener,
        NoCopySpan {
        private var mValueAnimator: ValueAnimator? = null
        private var curOffset = 0

        /**
         * 更改baseline,.来改变位置
         *
         * @param tp
         */
        override fun updateDrawState(tp: TextPaint) {
            initAnimate(tp.ascent())
            tp.baselineShift = curOffset
        }

        /**
         * @param textAscent 文字高度
         */
        private fun initAnimate(textAscent: Float) {
            if (mValueAnimator != null) {
                return
            }
            Log.i(TAG, "animate create")
            curOffset = 0
            maxOffsetRatio = Math.max(0f, Math.min(1.0f, maxOffsetRatio))
            val maxOffset = (textAscent * maxOffsetRatio).toInt()
            mValueAnimator = ValueAnimator.ofInt(0, maxOffset)
            mValueAnimator!!.duration = duration.toLong()
            mValueAnimator!!.startDelay = delay.toLong()
            mValueAnimator!!.interpolator = PointInterpolator(
                maxOffsetRatio
            )
            mValueAnimator!!.repeatCount = ValueAnimator.INFINITE
            mValueAnimator!!.repeatMode = ValueAnimator.RESTART
            mValueAnimator!!.addUpdateListener(this)
            mValueAnimator!!.addListener(this)
            mValueAnimator!!.start()
        }

        fun stop() {
            if (mValueAnimator != null) {
                Log.i(TAG, "span stop: $mValueAnimator")
                mValueAnimator!!.cancel()
            }
        }

        fun start() {
            if (mValueAnimator != null) {
                Log.i(TAG, "span start: $mValueAnimator")
                mValueAnimator!!.start()
            }
        }

        fun clear() {
            if (mValueAnimator != null) {
                Log.i(TAG, "span clear: $mValueAnimator")
                mValueAnimator!!.removeAllUpdateListeners()
                mValueAnimator!!.removeAllListeners()
                mValueAnimator!!.cancel()
            }
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            curOffset = animation.animatedValue as Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow) {
                    invalidate()
                }
            } else {
                if (parent != null) {
                    invalidate()
                }
            }
            Log.e("canim", "onAnimationUpdate$text$curOffset")
        }

        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            curOffset = 0
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}

        /**
         * 时间限制在0~maxOffsetRatio
         */
        private inner class PointInterpolator(animatedRange: Float) :
            TimeInterpolator {
            private val maxOffsetRatio: Float = Math.abs(animatedRange)
            override fun getInterpolation(input: Float): Float {
                if (input > maxOffsetRatio) {
                    return 0f
                }
                val radians = input / maxOffsetRatio * Math.PI
                return Math.sin(radians).toFloat()
            }

        }
    }

    fun setBackground(v: View?, background: Drawable?) {
        if (v == null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.background = background
        } else {
            v.setBackgroundDrawable(background)
        }
    }


}