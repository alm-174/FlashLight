package com.example.flashlight.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.example.flashlight.R
import com.example.flashlight.databinding.DialogRateUsBinding

class RateUsDialog : DialogFragment() {

    private var _binding: DialogRateUsBinding? = null
    private val binding get() = _binding!!

    /** Số sao đang chọn: 0 = chưa chọn. */
    private var rating = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogRateUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rating = savedInstanceState?.getInt(KEY_RATING) ?: 0

        stars().forEachIndexed { index, star ->
            star.setOnClickListener {
                rating = index + 1
                render()
            }
        }
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnSubmit.setOnClickListener { submit() }
        render()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // bỏ nền trắng mặc định của dialog
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setDimAmount(0.5f)                                      // nền phía sau tối 50% như Figma
        }
    }

    private fun stars(): List<ImageView> =
        listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

    /** Vẽ lại TOÀN BỘ hộp theo `rating` – chỉ một chỗ quyết định giao diện. */
    private fun render() {
        stars().forEachIndexed { index, star ->
            star.setImageResource(
                if (index < rating) R.drawable.ic_rate_filled else R.drawable.ic_star
            )
        }

        val (title, message) = when (rating) {
            1 -> getString(R.string.rate_1_title) to getString(R.string.rate_1_message)
            2 -> getString(R.string.rate_2_title) to getString(R.string.rate_2_message)
            3 -> getString(R.string.rate_3_title) to getString(R.string.rate_3_message)
            4 -> getString(R.string.rate_4_title) to getString(R.string.rate_4_message)
            5 -> getString(R.string.rate_5_title) to getString(R.string.rate_5_message)
            else -> getString(R.string.rate_0_title, getString(R.string.splash_title)) to getString(R.string.rate_0_message)
        }
        binding.tvTitle.text = title
        binding.tvMessage.text = message
        binding.btnSubmit.isEnabled = rating > 0


        val emojiRes = when (rating) {
            1 -> R.drawable.img_rate_1
            2 -> R.drawable.img_rate_2
            3 -> R.drawable.img_rate_3
            4 -> R.drawable.img_rate_4
            5 -> R.drawable.img_rate_5
            else -> R.drawable.img_rate_default
        }
        binding.ivEmoji.setImageResource(emojiRes)
    }

    private fun submit() {
        if (rating == 5) {
            openPlayStore()
        } else {
            Toast.makeText(requireContext(), R.string.rate_thanks_toast, Toast.LENGTH_SHORT).show()
        }
        dismiss()
    }

    private fun openPlayStore() {
        val pkg = requireContext().packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$pkg".toUri()))
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.play_store_web_url, pkg).toUri()))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), R.string.open_link_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_RATING, rating)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RateUsDialog"
        private const val KEY_RATING = "rating"
    }
}