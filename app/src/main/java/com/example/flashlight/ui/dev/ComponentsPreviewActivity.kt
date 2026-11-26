package com.example.flashlight.ui.dev

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flashlight.R
import com.example.flashlight.core.ui.ColorSwatchAdapter
import com.example.flashlight.core.ui.applySystemBarsPadding
import com.example.flashlight.core.ui.toast
import com.example.flashlight.databinding.ActivityComponentsPreviewBinding

class ComponentsPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComponentsPreviewBinding
    private var isPowerOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityComponentsPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        setupTopBar()
        setupRows()
        setupColorGrid()
        setupPowerButton()
    }

    private fun setupTopBar() {
        binding.topBar.tvTitle.text = "Components Preview"
        binding.topBar.btnBack.setOnClickListener { finish() }
    }

    private fun setupRows() {
        // Switch Row (B4)
        binding.switchRowSample.tvLabel.text = "Camera"

        // Slider Row (B5)
        binding.sliderRowSample.tvLabel.text = "Brightness"
        binding.sliderRowSample.slider.apply {
            valueFrom = 0f
            valueTo = 100f
            value = 80f
            addOnChangeListener { _, value, _ ->
                binding.sliderRowSample.tvValue.text = "${value.toInt()}%"
            }
        }
        binding.sliderRowSample.tvValue.text = "80%"

        // Selectable Row (B7) - bấm để test đổi trạng thái chọn
        binding.selectableRowSample.tvName.text = "English"
        binding.selectableRowSample.root.setOnClickListener { v ->
            v.isSelected = !v.isSelected
        }
    }

    private fun setupColorGrid() {
        val presetColors = resources.getIntArray(R.array.preset_colors).toList()
        val adapter = ColorSwatchAdapter(
            actionIconRes = R.drawable.ic_back,
            onActionClick = { toast("Open Color Picker") },
            onColorSelected = { color -> toast("Selected: #${Integer.toHexString(color)}") }
        )
        binding.rvColors.layoutManager = GridLayoutManager(this, 6)
        binding.rvColors.adapter = adapter
        adapter.submitColors(presetColors, presetColors.firstOrNull())
    }

    private fun setupPowerButton() {
        fun renderPower() {
            binding.powerButtonSample.tvPowerState.text = if (isPowerOn) "ON" else "OFF"
            binding.powerButtonSample.vCircle.isSelected = isPowerOn
            binding.powerButtonSample.vGlow.isVisible = isPowerOn
        }
        renderPower()
        binding.powerButtonSample.btnPower.setOnClickListener {
            isPowerOn = !isPowerOn
            renderPower()
        }
    }
}