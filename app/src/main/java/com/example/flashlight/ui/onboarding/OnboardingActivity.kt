package com.example.flashlight.ui.onboarding

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.flashlight.MainActivity
import com.example.flashlight.R
import com.example.flashlight.core.ui.applyBottomSystemBarsPadding
import com.example.flashlight.databinding.ActivityOnboardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()

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
                viewModel.completeOnboarding {
                    // TODO: Đổi sang PermissionActivity ở bước tiếp theo
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupDots(selectedIndex: Int) {
        binding.layoutDots.removeAllViews()
        val density = resources.displayMetrics.density
        val heightPx = (8 * density).toInt()
        val marginPx = (4 * density).toInt()

        pages.indices.forEach { index ->
            val isSelected = index == selectedIndex
            val widthPx = ((if (isSelected) 24 else 8) * density).toInt()
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(widthPx, heightPx).apply {
                    marginEnd = marginPx * 2
                }
                background = GradientDrawable().apply {
                    cornerRadius = heightPx / 2f
                    setColor(
                        ContextCompat.getColor(
                            context,
                            if (isSelected) R.color.primary else R.color.white
                        )
                    )
                }
            }
            binding.layoutDots.addView(dot)
        }
    }
}