package com.example.flashlight.ui.language

import android.content.Intent
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
import com.example.flashlight.core.ui.VerticalSpaceItemDecoration
import com.example.flashlight.core.ui.applySystemBarsPadding
import com.example.flashlight.core.ui.collectOn
import com.example.flashlight.core.ui.getStringForLocale
import com.example.flashlight.databinding.ActivityLanguageBinding
import com.example.flashlight.ui.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

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
        renderTexts(viewModel.selectedCode.value)
    }

    private fun setupTopBar() {
        binding.topBar.btnBack.visibility = if (fromSettings) View.VISIBLE else View.INVISIBLE
        binding.topBar.btnBack.setOnClickListener { finish() }

        tvSave = TextView(this).apply {
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
        val languages = listOf(
            LanguageOption("vi", "Việt Nam", R.drawable.ic_flag_vi),
            LanguageOption("es", "Español", R.drawable.ic_flag_es),
            LanguageOption("hi", "हिन्दी", R.drawable.ic_flag_hi),
            LanguageOption("en", "English", R.drawable.ic_flag_en),
            LanguageOption("pt", "Português", R.drawable.ic_flag_pt),
            LanguageOption("ja", "日本語", R.drawable.ic_flag_ja),
            LanguageOption("de", "Deutsch", R.drawable.ic_flag_de),
            LanguageOption("ko", "한국어", R.drawable.ic_flag_ko),
            LanguageOption("fr", "Français", R.drawable.ic_flag_fr),
            LanguageOption("it", "Italiano", R.drawable.ic_flag_it),
            LanguageOption("in", "Indonesia", R.drawable.ic_flag_id)
        )

        val adapter = LanguageAdapter(languages) { option ->
            viewModel.selectLanguage(option.code)
        }
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        if (binding.rvLanguages.itemDecorationCount == 0) {
            val spacePx = resources.getDimensionPixelSize(R.dimen.list_item_gap)
            binding.rvLanguages.addItemDecoration(VerticalSpaceItemDecoration(spacePx))
        }
        binding.rvLanguages.adapter = adapter

        viewModel.selectedCode.collectOn(this) { code ->
            adapter.setSelectedCode(code)
            tvSave.isVisible = code != null
            renderTexts(code)
        }
    }

    private fun renderTexts(languageCode: String?) {
        val titleRes = if (fromSettings) R.string.language_choose_title else R.string.language_title
        val saveRes = R.string.common_save
        val backRes = R.string.common_back

        val (titleText, saveText, backText) = if (languageCode != null) {
            val locale = Locale.forLanguageTag(languageCode)
            Triple(
                getStringForLocale(titleRes, locale),
                getStringForLocale(saveRes, locale),
                getStringForLocale(backRes, locale)
            )
        } else {
            Triple(
                getString(titleRes),
                getString(saveRes),
                getString(backRes)
            )
        }

        binding.topBar.tvTitle.text = titleText
        tvSave.text = saveText
        binding.topBar.btnBack.contentDescription = backText
    }

    private fun onSaveClicked() {
        val code = viewModel.selectedCode.value ?: return
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
        if (fromSettings) {
            finish()
        } else {
            viewModel.completeLanguageStep {
                viewModel.completeLanguageStep {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
                }
            }
        }
    }

    companion object {
        const val EXTRA_FROM_SETTINGS = "extra_from_settings"
    }
}
