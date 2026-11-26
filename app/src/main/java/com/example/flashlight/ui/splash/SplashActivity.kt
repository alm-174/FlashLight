package com.example.flashlight.ui.splash


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.flashlight.R
import com.example.flashlight.data.settings.OnboardingStep
import com.example.flashlight.data.settings.SettingsRepository
import com.example.flashlight.ui.language.LanguageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            val step = settingsRepository.onboardingStep.first()
            kotlinx.coroutines.delay(1200)
            navigateNext(step)
        }
    }

    private fun navigateNext(step: OnboardingStep) {
        val target = when (step) {
            OnboardingStep.LANGUAGE -> LanguageActivity::class.java
            OnboardingStep.INTRO -> LanguageActivity::class.java      // TODO: đổi sang OnboardingActivity sau
            OnboardingStep.PERMISSION -> LanguageActivity::class.java // TODO: đổi sang PermissionActivity sau
            OnboardingStep.DONE -> LanguageActivity::class.java       // TODO: đổi sang HomeActivity/MainActivity sau
        }
        startActivity(Intent(this, target))
        finish()
    }
}