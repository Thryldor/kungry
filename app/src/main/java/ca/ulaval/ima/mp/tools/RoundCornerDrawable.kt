package ca.ulaval.ima.mp.tools

import android.graphics.*
import android.graphics.drawable.Drawable


class RoundCornerDrawable(bitmap: Bitmap?, private val mCornerRadius: Float) :
    Drawable() {

    private val mRect = RectF()
    private val mBitmapShader: BitmapShader = BitmapShader(
        bitmap!!,
        Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
    )
    private val mPaint: Paint = Paint()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mRect[0f, 0f, bounds.width().toFloat()] = bounds.height().toFloat()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    init {
        mPaint.isAntiAlias = true
        mPaint.shader = mBitmapShader
    }
}
