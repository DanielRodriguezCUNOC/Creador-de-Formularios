package com.example.proyecto1_compi1_1s_2026.data.config

import android.content.Context

class ApiConfigStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, "") ?: ""

    fun setBaseUrl(value: String) {
        prefs.edit().putString(KEY_BASE_URL, value.trim()).apply()
    }

    companion object {
        private const val PREFS_NAME = "api_config_prefs"
        private const val KEY_BASE_URL = "base_url"
    }
}
