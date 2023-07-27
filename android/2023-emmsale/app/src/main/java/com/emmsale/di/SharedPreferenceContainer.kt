package com.emmsale.di

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceContainer(
    context: Context,
) {
    val preference: SharedPreferences by lazy {
        context.getSharedPreferences(KERDY_PREF_KEY, Context.MODE_PRIVATE)
    }

    companion object {
        private const val KERDY_PREF_KEY = "kerdy"
    }
}
