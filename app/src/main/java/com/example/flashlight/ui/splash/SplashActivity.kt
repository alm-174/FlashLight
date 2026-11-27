package com.example.flashlight.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.example.flashlight.MainActivity
import com.example.flashlight.data.settings.OnboardingStep
import com.example.flashlight.data.settings.SettingsRepository
import com.example.flashlight.databinding.ActivitySplashBinding
import com.example.flashlight.ui.home.HomeActivity
import com.example.flashlight.ui.language.LanguageActivity
import com.example.flashlight.ui.onboarding.OnboardingActivity
import com.example.flashlight.ui.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fadeInBackground()
        navigateWhenReady()
    }

    private fun fadeInBackground() {
        binding.ivBackground.alpha = 0f
        binding.ivBackground.animate()
            .alpha(1f)
            .setDuration(BACKGROUND_FADE_MS)
            .start()
    }

    private fun navigateWhenReady() {
        lifecycleScope.launch {
            val step = settingsRepository.onboardingStep.first()
            delay(MIN_DURATION_MS)
            // Chỉ chuyển màn khi Activity đang hiển thị. Người dùng thoát ra ngoài giữa chừng
            // thì đợi họ quay lại mới chuyển (Android chặn mở Activity khi app đang ở nền).
            withStarted {
                startActivity(intentFor(step))
                finish()
            }
        }
    }

    private fun intentFor(step: OnboardingStep): Intent {
        // TODO GĐ 3: thay MainActivity bằng màn thật khi đã tạo
        val target = when (step) {
            OnboardingStep.LANGUAGE -> LanguageActivity::class.java  // → LanguageActivity
            OnboardingStep.INTRO -> OnboardingActivity::class.java      // → OnboardingActivity
            OnboardingStep.PERMISSION -> PermissionActivity::class.java // → PermissionActivity
            OnboardingStep.DONE -> HomeActivity::class.java       // → HomeActivity
        }
        return Intent(this, target)
    }

    private companion object {
        const val MIN_DURATION_MS = 1500L      // hỏi người hướng dẫn thời gian chuẩn của công ty
        const val BACKGROUND_FADE_MS = 300L
    }
}