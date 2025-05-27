package com.example.exemplosimplesdecompose.Utils

import android.content.Context

class SwitchPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("switch_prefs", Context.MODE_PRIVATE)

    fun saveSwitchState(isChecked: Boolean) {
        prefs.edit().putBoolean("switch_state", isChecked).apply()
    }

    fun getSwitchState(): Boolean {
        return prefs.getBoolean("switch_state", true) // true é o valor padrão
    }
}