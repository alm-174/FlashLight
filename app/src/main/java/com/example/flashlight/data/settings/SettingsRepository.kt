package com.example.flashlight.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val onboardingStep: Flow<OnboardingStep> = dataStore.data.map { prefs ->
        OnboardingStep.fromName(prefs[KEY_ONBOARDING_STEP])
    }

    suspend fun setOnboardingStep(step: OnboardingStep) {
        dataStore.edit { prefs ->
            prefs[KEY_ONBOARDING_STEP] = step.name
        }
    }

    private companion object {
        val KEY_ONBOARDING_STEP = stringPreferencesKey("onboarding_step")
    }
}