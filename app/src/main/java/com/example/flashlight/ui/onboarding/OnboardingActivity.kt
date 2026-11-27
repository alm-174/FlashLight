package com.example.flashlight.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.flashlight.R
import com.example.flashlight.core.ui.applyBottomSystemBarsPadding
import com.example.flashlight.databinding.ActivityOnboardingBinding
import com.example.flashlight.ui.permission.PermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private var isNavigating = false

    // Thay ảnh và string theo đúng tên resource trong dự án của bạn
    private val pages = listOf(
        OnboardingPage(R.drawable.img_onboarding_1, R.string.onboarding_1_title, R.string.onboarding_1_desc),
        OnboardingPage(R.drawable.img_onboarding_2, R.string.onboarding_2_title, R.string.onboarding_2_desc),
        OnboardingPage(R.drawable.img_onboarding_3, R.string.onboarding_3_title, R.string.onboarding_3_desc),
        OnboardingPage(R.drawable.img_onboarding_4, R.string.onboarding_4_title, R.string.onboarding_4_desc)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Anh phai tran len duoi status bar -> KHONG pad top cho ca man; chi adContainer o day can
        // tranh navigation bar duoi cung.
        binding.adContainer.applyBottomSystemBarsPadding()

        setupViewPager()
        setupDots(0)
        setupButtons()
        setupBackNavigation()
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val current = binding.viewPager.currentItem
                if (current > 0) {
                    binding.viewPager.currentItem = current - 1
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = OnboardingPagerAdapter(this, pages)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setupDots(position)
                val isLast = position == pages.lastIndex
                binding.btnNext.setText(
                    if (isLast) R.string.onboarding_get_started else R.string.common_next
                )
            }
        })
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < pages.lastIndex) {
                binding.viewPager.currentItem = current + 1
            } else {
                if (isNavigating) return@setOnClickListener
                isNavigating = true
                viewModel.completeOnboarding {
                    startActivity(Intent(this, PermissionActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupDots(selectedIndex: Int) {
        binding.layoutDots.removeAllViews()
        val heightPx = resources.getDimensionPixelSize(R.dimen.onboarding_dot_height)
        val marginPx = resources.getDimensionPixelSize(R.dimen.onboarding_dot_margin)
        val activeWidthPx = resources.getDimensionPixelSize(R.dimen.onboarding_dot_width_active)
        val inactiveWidthPx = resources.getDimensionPixelSize(R.dimen.onboarding_dot_width_inactive)

        pages.indices.forEach { index ->
            val isSelected = index == selectedIndex
            val widthPx = if (isSelected) activeWidthPx else inactiveWidthPx
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(widthPx, heightPx).apply {
                    marginEnd = marginPx
                }
                background = ContextCompat.getDrawable(
                    context,
                    if (isSelected) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
                )
            }
            binding.layoutDots.addView(dot)
        }
    }
}