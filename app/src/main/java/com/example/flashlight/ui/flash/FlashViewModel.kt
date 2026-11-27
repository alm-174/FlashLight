package com.example.flashlight.ui.flash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashlight.core.torch.FlashPatterns
import com.example.flashlight.core.torch.TorchController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashViewModel @Inject constructor(
    private val torch: TorchController
) : ViewModel() {

    private val _activeMode = MutableStateFlow<FlashMode?>(null)

    /** Kiểu đèn đang chạy; null = tất cả đang tắt. */
    val activeMode: StateFlow<FlashMode?> = _activeMode.asStateFlow()

    val hasFlash: Boolean get() = torch.hasFlash

    init {
        // Đèn bị tắt từ bên ngoài (thanh thông báo nhanh, app khác) → cập nhật lại giao diện
        viewModelScope.launch {
            torch.isOn.collect { on ->
                if (!on && _activeMode.value == FlashMode.LIGHT) _activeMode.value = null
            }
        }
    }

    fun toggle(mode: FlashMode) {
        if (_activeMode.value == mode) stop() else start(mode)
    }

    private fun start(mode: FlashMode) {
        when (mode) {
            FlashMode.LIGHT -> torch.turnOn()
            FlashMode.SOS -> torch.play { FlashPatterns.sos() }
            FlashMode.DJ -> torch.play { FlashPatterns.dj() }
        }
        _activeMode.value = mode
    }

    fun stop() {
        torch.turnOff()
        _activeMode.value = null
    }

    override fun onCleared() {
        if (_activeMode.value != null) torch.turnOff()
    }
}