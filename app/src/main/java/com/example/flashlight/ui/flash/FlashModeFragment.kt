package com.example.flashlight.ui.flash

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.flashlight.R
import com.example.flashlight.databinding.FragmentFlashModeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlashModeFragment : Fragment(R.layout.fragment_flash_mode) {

    private var _binding: FragmentFlashModeBinding? = null
    private val binding get() = _binding!!

    /** ViewModel của Activity → 3 tab cùng nhìn 1 trạng thái. */
    private val viewModel: FlashViewModel by activityViewModels()


    private val mode: FlashMode by lazy {
        FlashMode.valueOf(requireArguments().getString(ARG_MODE)!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentFlashModeBinding.bind(view)
        // Mỗi chế độ một cỡ hình (quả cầu DJ to gần kín vòng tròn, còi SOS nhỏ hơn)
        val size = resources.getDimensionPixelSize(mode.imageSize)
        binding.ivImage.updateLayoutParams {
            width = size
            height = size
        }
        binding.circle.contentDescription = getString(mode.titleRes)
        binding.circle.setOnClickListener { viewModel.toggle(mode) }

        // repeatOnLifecycle(STARTED): chỉ lắng nghe khi màn đang hiển thị, tự dừng khi xuống nền
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activeMode.collect { active -> render(isOn = active == mode) }
            }
        }
    }

    private fun render(isOn: Boolean) {
        val ctx = requireContext()
        binding.tvState.setText(if (isOn) R.string.ft_on else R.string.ft_off)
        binding.circle.backgroundTintList = ContextCompat.getColorStateList(
            ctx, if (isOn) R.color.primary else R.color.flash_circle_off
        )
        binding.viewGlow.isInvisible = !isOn

        binding.ivImage.setImageResource(if (isOn) mode.imageOn else mode.imageOff)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MODE = "mode"

        fun newInstance(mode: FlashMode) = FlashModeFragment().apply {
            arguments = bundleOf(ARG_MODE to mode.name)
        }
    }
}