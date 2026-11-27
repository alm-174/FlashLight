package com.example.flashlight.core.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding

/**
 * Hàm tiện ích dùng chung cho các màn tính năng.
 * Mỗi màn chỉ cần gọi 2 dòng: enableDarkEdgeToEdge() + root.fitSystemBars().
 */

/** Vẽ tràn dưới thanh trạng thái / điều hướng, icon thanh hệ thống màu sáng (nền app tối). */
fun ComponentActivity.enableDarkEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
    )
}

/**
 * CỘNG chiều cao thanh hệ thống vào padding SẴN CÓ của view (không ghi đè).
 * Lưu padding gốc 1 lần bên ngoài listener, vì listener có thể chạy nhiều lần.
 */
fun View.fitSystemBars(top: Boolean = true, bottom: Boolean = true) {
    val baseTop = paddingTop
    val baseBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        view.updatePadding(
            top = baseTop + if (top) bars.top else 0,
            bottom = baseBottom + if (bottom) bars.bottom else 0
        )
        insets
    }
}

/** Ẩn hẳn thanh trạng thái + điều hướng (màn Play). Vuốt từ mép để hiện tạm. */
fun Activity.enterImmersiveMode() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/** Đổi dp sang pixel theo mật độ màn hình thật của máy. */
fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density
