package com.robin.baseframe.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import com.robin.baseframe.R


/**
 * 只能支持四周固定的圆角，或者圆形的图片
 * 支持设置自定义圆角或圆形背景颜色，（需要使用当前类提供的背景颜色方法）
 * <p>
 * 支持四个角各自定义角度
 */
class RoundCircleImageView : AppCompatImageView {
    private var mRoundRadius = 0
    private var isCircleType = false
    private val mDrawableRect: RectF = RectF()
    private val mShaderMatrix: Matrix = Matrix()
    private val mBitmapPaint: Paint = Paint()
    private val mRoundBackgroundPaint: Paint = Paint()
    private var mRoundBackgroundDrawable: Drawable? = null
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mBackgroundBitmap: Bitmap? = null
    private var mBackgroundBitmapShader: BitmapShader? = null
    private var mColorFilter: ColorFilter? = null
    private var mDrawableRadius = 0f
    private var mReady = false
    private var mSetupPending = false
    private var mTopLeft = 0f
    private var mTopRight = 0f
    private var mBottomLeft = 0f
    private var mBottomRight = 0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

        //读取配置属性
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCircleImageView)
        mRoundRadius = array.getDimensionPixelOffset(R.styleable.RoundCircleImageView_round_radius, 0)
        mTopLeft = array.getDimensionPixelOffset(R.styleable.RoundCircleImageView_topLeft, 0).toFloat()
        mTopRight = array.getDimensionPixelOffset(R.styleable.RoundCircleImageView_topRight, 0).toFloat()
        mBottomLeft = array.getDimensionPixelOffset(R.styleable.RoundCircleImageView_bottomLeft, 0).toFloat()
        mBottomRight = array.getDimensionPixelOffset(R.styleable.RoundCircleImageView_bottomRight, 0).toFloat()
        if (array.hasValue(R.styleable.RoundCircleImageView_round_background_color)) {
            val roundBackgroundColor: Int =
                array.getColor(R.styleable.RoundCircleImageView_round_background_color, Color.TRANSPARENT)
            mRoundBackgroundDrawable = ColorDrawable(roundBackgroundColor)
            mBackgroundBitmap = getBitmapFromDrawable(mRoundBackgroundDrawable)
        }
        if (array.hasValue(R.styleable.RoundCircleImageView_round_background_drawable)) {
            mRoundBackgroundDrawable = array.getDrawable(R.styleable.RoundCircleImageView_round_background_drawable)
            mBackgroundBitmap = getBitmapFromDrawable(mRoundBackgroundDrawable)
        }
        isCircleType = array.getBoolean(R.styleable.RoundCircleImageView_isCircle, false)
        array.recycle()
        init()
    }

    private fun init() {
        super.setScaleType(SCALE_TYPE)
        mReady = true
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType) {
        require(scaleType == SCALE_TYPE) { "已经自带设置了，你无需再设置了" }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "已经自带设置了，你无需再设置了" }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (mBitmap == null && mBackgroundBitmap == null) {
            return
        }
        if (isCircleType) {
            if (mRoundBackgroundDrawable != null && mBackgroundBitmap != null) {
                canvas.drawCircle(
                    mDrawableRect.centerX(),
                    mDrawableRect.centerY(),
                    mDrawableRadius,
                    mRoundBackgroundPaint
                )
            }
            if (mBitmap != null) {
                canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRadius, mBitmapPaint)
            }
        } else {
            if (mTopLeft > 0 || mTopRight > 0 || mBottomLeft > 0 || mBottomRight > 0) {
                //使用单独的圆角
                if (mRoundBackgroundDrawable != null && mBackgroundBitmap != null) {
                    val path = Path()
                    path.addRoundRect(
                        mDrawableRect,
                        floatArrayOf(
                            mTopLeft,
                            mTopLeft,
                            mTopRight,
                            mTopRight,
                            mBottomRight,
                            mBottomRight,
                            mBottomLeft,
                            mBottomLeft
                        ),
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, mRoundBackgroundPaint)
                }
                if (mBitmap != null) {
                    val path = Path()
                    path.addRoundRect(
                        mDrawableRect,
                        floatArrayOf(
                            mTopLeft,
                            mTopLeft,
                            mTopRight,
                            mTopRight,
                            mBottomRight,
                            mBottomRight,
                            mBottomLeft,
                            mBottomLeft
                        ),
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, mBitmapPaint)
                }
            } else {
                //使用统一的圆角
                if (mRoundBackgroundDrawable != null && mBackgroundBitmap != null) {
                    canvas.drawRoundRect(mDrawableRect, mRoundRadius.toFloat(),
                        mRoundRadius.toFloat(), mRoundBackgroundPaint)
                }
                if (mBitmap != null) {
                    canvas.drawRoundRect(mDrawableRect, mRoundRadius.toFloat(), mRoundRadius.toFloat(), mBitmapPaint)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    override fun setBackgroundColor(color: Int) {
        setRoundBackgroundColor(color)
    }

    override fun setBackground(background: Drawable?) {
        setRoundBackgroundDrawable(background)
    }

    override fun setBackgroundDrawable(@Nullable background: Drawable?) {
        setRoundBackgroundDrawable(background)
    }

    override fun setBackgroundResource(resId: Int) {
        @SuppressLint("UseCompatLoadingForDrawables") val drawable: Drawable = context.resources.getDrawable(resId)
        setRoundBackgroundDrawable(drawable)
    }

    override fun getBackground(): Drawable? {
        return getRoundBackgroundDrawable()
    }

    fun setRoundBackgroundColor(@ColorInt roundBackgroundColor: Int) {
        val drawable = ColorDrawable(roundBackgroundColor)
        setRoundBackgroundDrawable(drawable)
    }

    fun setRoundBackgroundColorResource(@ColorRes circleBackgroundRes: Int) {
        setRoundBackgroundColor(context.resources.getColor(circleBackgroundRes))
    }

    fun getRoundBackgroundDrawable(): Drawable? {
        return mRoundBackgroundDrawable
    }

    fun setRoundBackgroundDrawable(drawable: Drawable?) {
        mRoundBackgroundDrawable = drawable
        initializeBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === mColorFilter) {
            return
        }
        mColorFilter = cf
        applyColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? {
        return mColorFilter
    }

    private fun applyColorFilter() {
        if (mBitmapPaint != null) {
            mBitmapPaint.colorFilter = mColorFilter
        }
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            (drawable as BitmapDrawable).bitmap
        } else try {
            val bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = getBitmapFromDrawable(drawable)
        if (mRoundBackgroundDrawable != null) {
            mBackgroundBitmap = getBitmapFromDrawable(mRoundBackgroundDrawable)
        }
        setup()
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }
        if (width == 0 && height == 0) {
            return
        }
        if (mBitmap == null && mBackgroundBitmap == null) {
            invalidate()
            return
        }
        if (mBitmap != null) {
            mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mBitmapPaint.isAntiAlias = true
            mBitmapPaint.shader = mBitmapShader
        }
        if (mRoundBackgroundDrawable != null && mBackgroundBitmap != null) {
            mBackgroundBitmapShader = BitmapShader(mBackgroundBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mRoundBackgroundPaint.isAntiAlias = true
            mRoundBackgroundPaint.shader = mBackgroundBitmapShader
        }
        val bitmap: Bitmap = (mBitmap ?: mBackgroundBitmap) as Bitmap
        mBitmapHeight = bitmap.height
        mBitmapWidth = bitmap.width
        mDrawableRect.set(calculateBounds())
        mDrawableRadius = Math.min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)
        applyColorFilter()
        updateShaderMatrix()
        //重绘
        invalidate()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = Math.min(availableWidth, availableHeight)
        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f
        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate((dx + 0.5f).toInt() + mDrawableRect.left, (dy + 0.5f).toInt() + mDrawableRect.top)
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
        mBackgroundBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG: Bitmap.Config = Bitmap.Config.ARGB_8888
        private const val COLORDRAWABLE_DIMENSION = 2
    }
}