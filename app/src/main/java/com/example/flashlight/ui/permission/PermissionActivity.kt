package com.example.flashlight.ui.permission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flashlight.MainActivity
import com.example.flashlight.R
import com.example.flashlight.core.ui.applySystemBarsPadding
import com.example.flashlight.databinding.ActivityPermissionBinding
import com.example.flashlight.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private val viewModel: PermissionViewModel by viewModels()

    private var isNavigating = false

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        // Trước và sau đều không cần "giải thích" + bị từ chối → hệ thống KHÔNG hiện hộp thoại
        // → quyền đã bị từ chối hẳn từ trước → chỉ còn cách vào Cài đặt.
        val rationaleAfter = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        if (!granted && !viewModel.rationaleBeforeRequest && !rationaleAfter) {
            openAppSettings()
        }
        render()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        setupViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        render()   // quay lại từ Cài đặt (cấp hoặc thu hồi quyền) → cập nhật ngay
    }

    /** Chỉ gán những gì phụ thuộc dữ liệu/chuỗi có tham số. Ảnh tĩnh đặt trong XML. */
    private fun setupViews() {
        binding.tvMessage.text = getString(
            R.string.permission_message,
            getString(R.string.splash_title)
        )
        binding.rowCamera.tvLabel.setText(R.string.permission_camera)
    }

    private fun setupListeners() {
        binding.rowCamera.root.setOnClickListener {
            binding.rowCamera.switchStatus.toggle()
        }

        binding.btnContinue.setOnClickListener {
            if (isNavigating) return@setOnClickListener
            isNavigating = true
            viewModel.completePermissionStep {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun handleSwitchChanged(isChecked: Boolean) {
        val granted = isCameraGranted()
        when {
            // Bật khi chưa có quyền → xin quyền
            isChecked && !granted -> requestCamera()
            // Tắt khi đang có quyền → app không tự thu hồi được, mở Cài đặt để người dùng tự tắt
            !isChecked && granted -> openAppSettings()
        }
        render()   // công tắc luôn trở về đúng trạng thái quyền THẬT
    }

    private fun requestCamera() {
        viewModel.rationaleBeforeRequest =
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        cameraLauncher.launch(Manifest.permission.CAMERA)
    }

    /** Nơi DUY NHẤT gán trạng thái công tắc và nút Continue. */
    private fun render() {
        val granted = isCameraGranted()
        binding.rowCamera.switchStatus.setOnCheckedChangeListener(null)
        binding.rowCamera.switchStatus.isChecked = granted
        binding.rowCamera.switchStatus.setOnCheckedChangeListener { _, isChecked ->
            handleSwitchChanged(isChecked)
        }
        binding.btnContinue.isEnabled = granted
    }

    private fun isCameraGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    private fun openAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }
}