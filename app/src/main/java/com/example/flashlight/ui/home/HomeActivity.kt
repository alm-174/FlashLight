package com.example.flashlight.ui.home

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.flashlight.R
import com.example.flashlight.core.ui.DotGridDrawable
import com.example.flashlight.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInsets()
        setupViews()
        setupClicks()
    }

    /** Cộng chiều cao thanh trạng thái / thanh điều hướng vào padding SẴN CÓ của nội dung. */
    private fun applyInsets() {
        val content = binding.contentHome
        val baseTop = content.paddingTop
        val baseBottom = content.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = baseTop + bars.top, bottom = baseBottom + bars.bottom)
            insets
        }
    }

    private fun setupViews() {
        binding.cardLed.clipToOutline = true
        binding.layoutMarquee.clipToOutline = true

        binding.viewLedDots.background = DotGridDrawable(
            dotColor = getColor(R.color.home_led_dot),
            dotSize = resources.getDimension(R.dimen.home_led_dot_size),
            gapX = resources.getDimension(R.dimen.home_led_dot_gap_x),
            gapY = resources.getDimension(R.dimen.home_led_dot_gap_y)
        )

        // Chữ phát sáng: bóng không lệch (dx = dy = 0), cùng màu chữ
        binding.tvMarquee.setShadowLayer(
            resources.getDimension(R.dimen.home_marquee_glow), 0f, 0f,
            getColor(R.color.primary)
        )
        binding.tvMarquee.isSelected = true
    }

    private fun setupClicks() {
        binding.btnCamera.setOnClickListener { /* TODO GĐ 8: mở CameraActivity */ }
        binding.btnSettings.setOnClickListener { /* TODO GĐ 4: mở SettingsActivity */ }
        binding.btnPlayNow.setOnClickListener { /* TODO GĐ 7: mở LedPlayActivity */ }
        binding.tvCustomText.setOnClickListener { /* TODO GĐ 7: mở LedActivity */ }
        binding.btnExploreModes.setOnClickListener { /* TODO GĐ 5: mở FlashActivity */ }
        binding.btnSetUpAlerts.setOnClickListener { /* TODO GĐ 9: mở FlashAlertActivity */ }
        binding.btnScreenlightOpen.setOnClickListener { /* TODO GĐ 6: mở ScreenLightActivity */ }
        binding.tvAdjust.setOnClickListener { /* TODO GĐ 6: mở ScreenLightActivity */ }
        binding.btnEdgeOpen.setOnClickListener { /* TODO GĐ 10: mở EdgeLightingActivity */ }
    }
}