package com.example.flashlight.ui.language

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashlight.R
import com.example.flashlight.core.ui.applySystemBarsPadding
import com.example.flashlight.core.ui.collectOn
import com.example.flashlight.databinding.ActivityLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding
    private val viewModel: LanguageViewModel by viewModels()
    private lateinit var tvSave: TextView

    private val fromSettings: Boolean
        get() = intent.getBooleanExtra(EXTRA_FROM_SETTINGS, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        setupTopBar()
        setupList()
    }

    private fun setupTopBar() {
        binding.topBar.tvTitle.setText(
            if (fromSettings) R.string.language_choose_title else R.string.language_title
        )
        binding.topBar.btnBack.visibility = if (fromSettings) View.VISIBLE else View.INVISIBLE
        binding.topBar.btnBack.setOnClickListener { finish() }

        tvSave = TextView(this).apply {
            setText(R.string.common_save)
            setTextColor(ContextCompat.getColor(context, R.color.primary))
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(16, 0, 16, 0)
            isVisible = false
            setOnClickListener { onSaveClicked() }
        }
        binding.topBar.endSlot.addView(
            tvSave,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun setupList() {
        // Thay danh sách cờ bằng các drawable flag_* thực tế trong dự án của bạn
        val languages = listOf(
            LanguageOption("en", "English", R.drawable.ic_back),
            LanguageOption("vi", "Tiếng Việt", R.drawable.ic_back),
            LanguageOption("de", "Deutsch", R.drawable.ic_back)
        )

        val adapter = LanguageAdapter(languages) { option ->
            viewModel.selectLanguage(option.code)
        }
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = adapter

        viewModel.selectedCode.collectOn(this) { code ->
            adapter.setSelectedCode(code)
            tvSave.isVisible = code != null
        }
    }

    private fun onSaveClicked() {
        val code = viewModel.selectedCode.value ?: return
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
        if (fromSettings) {
            finish()
        } else {
            viewModel.completeLanguageStep {
                // TODO: Mở OnboardingActivity ở bước tiếp theo
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_FROM_SETTINGS = "extra_from_settings"
    }
}