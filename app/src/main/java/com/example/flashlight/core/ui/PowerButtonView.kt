package com.example.flashlight.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.example.flashlight.R

/**
 * B12 - Vòng tròn ON/OFF lớn dùng ở 3 tab Flash (Flash Light / SOS / DJ).
 * Chỉ lo phần hình: vòng sáng (vGlow) + màu nền theo trạng thái (vCircle, qua selector_power_circle_bg) + icon.
 * Màn dùng nó tự quan sát TorchController.state rồi gọi setOn(true/false) + setIcon(...); view này
 * KHÔNG tự đổi trạng thái khi bấm, chỉ lo hiệu ứng nhấn.
 *
 * Children (vGlow, vCircle, ivIcon) khai báo trực tiếp trong layout dùng view này (xem view_power_button.xml),
 * nên chỉ cần tìm lại bằng findViewById ở onFinishInflate, không cần tự inflate thêm layout.
 */
class PowerButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var vGlow: View
    private lateinit var vCircle: View
    private lateinit var ivIcon: ImageView
    private var externalClickListener: OnClickListener? = null

    init {
        isClickable = true
        isFocusable = true
        // Chỉ gán 1 listener "thật" duy nhất (qua super) để luôn chạy animation nhấn trước,
        // rồi mới gọi listener của màn dùng nó -> màn khác không phải tự viết lại animation.
        super.setOnClickListener {
            animatePress()
            externalClickListener?.onClick(it)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        vGlow = findViewById(R.id.vGlow)
        vCircle = findViewById(R.id.vCircle)
        ivIcon = findViewById(R.id.ivIcon)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        externalClickListener = l
    }

    fun setOn(isOn: Boolean) {
        vCircle.isSelected = isOn
        vGlow.isVisible = isOn
    }

    fun setIcon(@DrawableRes resId: Int) {
        ivIcon.setImageResource(resId)
    }

    private fun animatePress() {
        animate().scaleX(0.95f).scaleY(0.95f).setDuration(80L)
            .withEndAction {
                animate().scaleX(1f).scaleY(1f).setDuration(80L).start()
            }.start()
    }
}
