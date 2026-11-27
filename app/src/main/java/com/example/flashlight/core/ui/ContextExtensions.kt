package com.example.flashlight.core.ui

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import java.util.Locale

fun Context.getStringForLocale(@StringRes resId: Int, locale: Locale): String {
    val config = Configuration(resources.configuration).apply {
        setLocale(locale)
    }
    return createConfigurationContext(config).getString(resId)
}
