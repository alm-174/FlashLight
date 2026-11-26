package com.example.flashlight.ui.language

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashlight.data.settings.OnboardingStep
import com.example.flashlight.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LanguageOption(
    val code: String,
    val displayName: String,
    @DrawableRes val flagRes: Int
)

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedCode = MutableStateFlow<String?>(null)
    val selectedCode: StateFlow<String?> = _selectedCode.asStateFlow()

    fun selectLanguage(code: String) {
        _selectedCode.value = code
    }

    fun completeLanguageStep(onDone: () -> Unit) {
        viewModelScope.launch {
            settingsRepository.setOnboardingStep(OnboardingStep.INTRO)
            onDone()
        }
    }
}