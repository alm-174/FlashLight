package com.example.flashlight.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.flashlight.R
import com.example.flashlight.databinding.ViewNavRowBinding
import com.google.android.material.card.MaterialCardView

/**
 * B3 - Hàng chọn dùng chung: icon + tiêu đề + phụ đề + giá trị + mũi tên (Settings, Flash Alert...).
 * Dùng trong layout: <com.example.flashlight.core.ui.NavRowView app:navTitle="..." .../>
 * Thuộc tính XML (xem attrs.xml): navIcon, navIconTint, navTitle, navSubtitle, navValue, showChevron.
 * Mỗi thuộc tính cũng có hàm set...() tương ứng để đổi lúc runtime (vd set tên ngôn ngữ đang chọn ở Settings).
 */
class NavRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding = ViewNavRowBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // NavRowView kế thừa trực tiếp MaterialCardView nên không gán được style="@style/Widget.App.Card"
        // như view thường trong XML của chính nó -> tự áp các thuộc tính đó bằng code cho khớp.
        radius = resources.getDimension(R.dimen.radius_item)
        strokeWidth = resources.getDimensionPixelSize(R.dimen.stroke_thin)
        strokeColor = ContextCompat.getColor(context, R.color.stroke)
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface))
        cardElevation = 0f
        isClickable = true
        isFocusable = true
        foreground = resolveSelectableForeground()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavRowView)
        try {
            setIcon(typedArray.getDrawable(R.styleable.NavRowView_navIcon))
            if (typedArray.hasValue(R.styleable.NavRowView_navIconTint)) {
                setIconTint(typedArray.getColorStateList(R.styleable.NavRowView_navIconTint))
            }
            setTitle(typedArray.getString(R.styleable.NavRowView_navTitle))
            setSubtitle(typedArray.getString(R.styleable.NavRowView_navSubtitle))
            setValue(typedArray.getString(R.styleable.NavRowView_navValue))
            setShowChevron(typedArray.getBoolean(R.styleable.NavRowView_showChevron, true))
        } finally {
            typedArray.recycle()
        }
    }

    fun setIcon(drawable: Drawable?) {
        binding.ivIcon.setImageDrawable(drawable)
        binding.ivIcon.isVisible = drawable != null
    }

    fun setIcon(@DrawableRes resId: Int) = setIcon(ContextCompat.getDrawable(context, resId))

    fun setIconTint(tint: ColorStateList?) {
        binding.ivIcon.imageTintList = tint
    }

    fun setTitle(title: CharSequence?) {
        binding.tvTitle.text = title
    }

    fun setSubtitle(subtitle: CharSequence?) {
        binding.tvSubtitle.text = subtitle
        binding.tvSubtitle.isVisible = !subtitle.isNullOrBlank()
    }

    fun setValue(value: CharSequence?) {
        binding.tvValue.text = value
        binding.tvValue.isVisible = !value.isNullOrBlank()
    }

    fun setShowChevron(show: Boolean) {
        binding.ivChevron.isVisible = show
    }

    // ?android:attr/selectableItemBackground là ripple chuẩn của hệ thống, tra bằng TypedValue vì
    // không gán trực tiếp được resource id của 1 theme attribute trong code.
    private fun resolveSelectableForeground(): Drawable? {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, value, true)
        return ContextCompat.getDrawable(context, value.resourceId)
    }
}
