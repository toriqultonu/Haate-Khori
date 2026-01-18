package com.example.alphabettracer.data

import android.content.Context

/**
 * Storage for Coloring Book progress and settings
 */
object ColoringStorage {
    private const val PREFS_NAME = "coloring_prefs"
    private const val KEY_TUTORIAL_SHOWN = "coloring_tutorial_shown"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Tutorial persistence
    fun hasShownTutorial(context: Context): Boolean =
        getPrefs(context).getBoolean(KEY_TUTORIAL_SHOWN, false)

    fun markTutorialShown(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_TUTORIAL_SHOWN, true).apply()
    }
}
