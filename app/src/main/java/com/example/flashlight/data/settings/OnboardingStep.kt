package com.example.flashlight.data.settings

enum class OnboardingStep {
    LANGUAGE,
    INTRO,
    PERMISSION,
    DONE;

    companion object {
        fun fromName(name: String?): OnboardingStep =
            entries.firstOrNull { it.name == name } ?: LANGUAGE
    }
}