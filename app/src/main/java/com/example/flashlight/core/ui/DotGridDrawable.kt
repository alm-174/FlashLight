package com.example.flashlight.core.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/** Lưới chấm vuông phủ kín vùng vẽ – giả lập bảng đèn LED. */
class DotGridDrawable(
    @ColorInt dotColor: Int,
    private val dotSize: Float,
    private val gapX: Float,
    private val gapY: Float
) : Drawable() {

    private val paint = Paint().apply { color = dotColor }

    override fun draw(canvas: Canvas) {
        val b = bounds
        var y = b.top.toFloat()
        while (y < b.bottom) {
            var x = b.left.toFloat()
            while (x < b.right) {
                canvas.drawRect(x, y, x + dotSize, y + dotSize, paint)
                x += dotSize + gapX
            }
            y += dotSize + gapY
        }
    }

    override fun setAlpha(alpha: Int) { paint.alpha = alpha; invalidateSelf() }
    override fun setColorFilter(colorFilter: ColorFilter?) { paint.colorFilter = colorFilter; invalidateSelf() }
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}