package com.example.flashlight.ui.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashlight.data.settings.OnboardingStep
import com.example.flashlight.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var rationaleBeforeRequest: Boolean = false

    fun completePermissionStep(onDone: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.setOnboardingStep(OnboardingStep.DONE)
            onDone()
        }
    }
}