package com.example.flashlight.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.example.flashlight.R
import com.example.flashlight.databinding.ActivitySettingsBinding
import com.example.flashlight.databinding.ViewSettingsRowBinding
import com.example.flashlight.ui.language.LanguageActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyInsets()
        setupRows()
        setupClicks()
    }

    override fun onResume() {
        super.onResume()
        // Quay lại từ màn Language thì cập nhật tên ngôn ngữ
        binding.rowLanguage.tvValue.text = currentLanguageName()
    }

    private fun applyInsets() {
        val root = binding.rootSettings
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }
    }

    private fun setupRows() {
        binding.rowLanguage.bind(R.drawable.ic_language, R.string.settings_language)
        binding.rowLanguage.tvValue.isVisible = true
        binding.rowShare.bind(R.drawable.ic_share, R.string.settings_share)
        binding.rowRate.bind(R.drawable.ic_rate, R.string.settings_rate)
        binding.rowPrivacy.bind(R.drawable.ic_privacy, R.string.settings_privacy)
        binding.rowPolicy.bind(R.drawable.ic_policy, R.string.settings_policy)
    }

    /** Gán icon + tên cho một hàng. Hàm mở rộng nên gọi được thẳng trên binding của hàng. */
    private fun ViewSettingsRowBinding.bind(@DrawableRes icon: Int, @StringRes title: Int) {
        ivIcon.setImageResource(icon)
        tvTitle.setText(title)
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener { finish() }
        binding.rowLanguage.root.setOnClickListener {
            startActivity(LanguageActivity.newIntent(this, fromSettings = true))
        }
        binding.rowShare.root.setOnClickListener { shareApp() }
        binding.rowRate.root.setOnClickListener {
            // Không mở chồng 2 hộp nếu người dùng bấm nhanh 2 lần
            if (supportFragmentManager.findFragmentByTag(RateUsDialog.TAG) == null) {
                RateUsDialog().show(supportFragmentManager, RateUsDialog.TAG)
            }
        }
        binding.rowPrivacy.root.setOnClickListener { openUrl(getString(R.string.privacy_policy_url)) }
    }

    private fun currentLanguageName(): String {
        val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
        // Tên ngôn ngữ viết bằng chính ngôn ngữ đó: "Deutsch", "Tiếng Việt"...
        return locale.getDisplayLanguage(locale).replaceFirstChar { it.titlecase(locale) }
    }

    private fun shareApp() {
        val link = getString(R.string.play_store_web_url, packageName)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, getString(R.string.splash_title), link))
        }
        startActivity(Intent.createChooser(send, null))
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.open_link_error, Toast.LENGTH_SHORT).show()
        }
    }
}