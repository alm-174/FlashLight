package com.example.flashlight.core.torch

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorchController @Inject constructor(
    @ApplicationContext context: Context
) {

    private val cameraManager = context.getSystemService(CameraManager::class.java)

    private val cameraId: String? = try {
        cameraManager.cameraIdList.firstOrNull { id ->
            val c = cameraManager.getCameraCharacteristics(id)

            c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                    c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }
    } catch (e: CameraAccessException) {
        null
    }

    val hasFlash: Boolean get() = cameraId != null

    private val _isOn = MutableStateFlow(false)

    /** Đèn đang sáng hay tắt – cập nhật cả khi app khác / hệ thống đổi trạng thái đèn. */
    val isOn: StateFlow<Boolean> = _isOn.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val lock = Any()
    private var generation = 0
    private var patternJob: Job? = null

    init {
        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(id: String, enabled: Boolean) {
                if (id == cameraId) _isOn.value = enabled
            }

            override fun onTorchModeUnavailable(id: String) {
                // Camera đang bị app khác (hoặc màn Camera của mình) chiếm → đèn không dùng được
                if (id == cameraId) _isOn.value = false
            }
        }, Handler(Looper.getMainLooper()))
    }

    /** Bật sáng liên tục. */
    fun turnOn() = synchronized(lock) {
        newGeneration()
        setTorch(true)
    }

    /** Tắt hẳn, dừng mọi kiểu nháy. */
    fun turnOff() = synchronized(lock) {
        newGeneration()
        setTorch(false)
    }

    /**
     * Chạy một chuỗi bước sáng/tắt.
     * @param steps hàm tạo danh sách bước – gọi lại mỗi vòng (DJ dùng để tạo nhịp ngẫu nhiên mới).
     * @param repeat số vòng; null = lặp mãi đến khi có lệnh khác.
     */
    fun play(repeat: Int? = null, steps: () -> List<FlashStep>) = synchronized(lock) {
        val gen = newGeneration()
        setTorch(false)
        patternJob = scope.launch {
            var round = 0
            while (isActive && (repeat == null || round < repeat)) {
                for (step in steps()) {
                    if (!applyIfCurrent(gen, step.on)) return@launch
                    delay(step.durationMs)
                }
                round++
            }
            applyIfCurrent(gen, false)
        }
    }

    private fun newGeneration(): Int {
        generation++
        patternJob?.cancel()
        patternJob = null
        return generation
    }

    /** Chỉ đổi đèn nếu lệnh này vẫn là lệnh mới nhất. Trả về false nếu đã bị lệnh khác thay thế. */
    private fun applyIfCurrent(gen: Int, on: Boolean): Boolean = synchronized(lock) {
        if (gen != generation) return false
        setTorch(on)
        true
    }

    private fun setTorch(on: Boolean) {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, on)
        } catch (e: CameraAccessException) {
            // Camera đang được dùng (ví dụ màn Camera đang mở) → bỏ qua, không crash
        } catch (e: IllegalArgumentException) {
            // Id camera không hợp lệ trên một số máy lạ
        }
    }
}

data class FlashStep(val on: Boolean, val durationMs: Long)