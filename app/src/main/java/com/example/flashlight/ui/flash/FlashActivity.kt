package com.example.flashlight.ui.flash

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flashlight.core.ui.enableDarkEdgeToEdge
import com.example.flashlight.core.ui.fitSystemBars
import androidx.core.view.isInvisible
import androidx.fragment.app.commit
import com.example.flashlight.R
import com.example.flashlight.databinding.ActivityFlashBinding
import com.example.flashlight.databinding.ViewFlashTabBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlashActivity : AppCompatActivity(){

    private lateinit var binding: ActivityFlashBinding
    private val viewModel: FlashViewModel by viewModels()
    private var currentMode = FlashMode.LIGHT

    private val tabs: Map<FlashMode, ViewFlashTabBinding> by lazy {
        mapOf(
            FlashMode.LIGHT to binding.tabLight,
            FlashMode.SOS to binding.tabSos,
            FlashMode.DJ to binding.tabDj
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableDarkEdgeToEdge()
        binding = ActivityFlashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topBar.fitSystemBars(bottom = false)

        binding.header.tvTitle.setText(R.string.flash_title)
        binding.header.btnBack.setOnClickListener { finish() }

        tabs.forEach { (mode, tab) ->
            tab.ivIcon.setImageResource(mode.iconRes)
            tab.tvLabel.setText(mode.titleRes)
            tab.root.setOnClickListener { selectTab(mode) }
        }

        if (!viewModel.hasFlash) {
            Toast.makeText(this, R.string.ft_no_flash, Toast.LENGTH_LONG).show()
        }

        // Tab đang mở được nhớ qua lần tạo lại Activity (xoay máy, đổi ngôn ngữ)
        currentMode = savedInstanceState?.getString(KEY_TAB)?.let { FlashMode.valueOf(it) } ?: FlashMode.LIGHT
        selectTab(currentMode)
    }

    private fun selectTab(mode: FlashMode) {
        currentMode = mode
        val fm = supportFragmentManager
        fm.commit {
            setReorderingAllowed(true)
            FlashMode.entries.forEach { m ->
                val fragment = fm.findFragmentByTag(m.name)
                when {
                    m == mode && fragment == null -> add(R.id.container, FlashModeFragment.newInstance(m), m.name)
                    m == mode -> show(fragment!!)
                    fragment != null -> hide(fragment)
                }
            }
        }
        renderTabs()
    }

    private fun renderTabs() {
        val selectedColor = ContextCompat.getColor(this, R.color.primary)
        val normalColor = ContextCompat.getColor(this, R.color.text_secondary)
        tabs.forEach { (mode, tab) ->
            val selected = mode == currentMode
            val color = if (selected) selectedColor else normalColor
            tab.tvLabel.setTextColor(color)
            tab.ivIcon.setColorFilter(color)
            tab.indicator.isInvisible = !selected
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TAB, currentMode.name)
    }

    private companion object {
        const val KEY_TAB = "tab"
    }
}