package com.emmsale.presentation

import android.app.Application
import android.content.Context

class KerdyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: KerdyApplication
        val context: Context get() = instance.applicationContext
    }
}
