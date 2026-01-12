package com.example.alphabettracer.data

import android.content.Context

object StickBuilderStorage {
    private const val PREFS_NAME = "stick_builder_prefs"
    private const val KEY_COMPLETED_PREFIX = "challenge_completed_"
    private const val KEY_CURRENT_LEVEL = "current_level"
    private const val KEY_TOTAL_COMPLETED = "total_completed"
    private const val KEY_HIGHEST_LEVEL = "highest_level"

    fun saveChallengeCompleted(context: Context, challengeId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Mark as completed
        editor.putBoolean("$KEY_COMPLETED_PREFIX$challengeId", true)

        // Update total completed count
        val currentTotal = getTotalCompleted(context)
        editor.putInt(KEY_TOTAL_COMPLETED, currentTotal + 1)

        // Update highest level if needed
        val currentHighest = getHighestLevel(context)
        if (challengeId > currentHighest) {
            editor.putInt(KEY_HIGHEST_LEVEL, challengeId)
        }

        // Move to next level
        if (challengeId < StickBuilderLevels.allChallenges.size) {
            editor.putInt(KEY_CURRENT_LEVEL, challengeId + 1)
        }

        editor.apply()
    }

    fun isChallengeCompleted(context: Context, challengeId: Int): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("$KEY_COMPLETED_PREFIX$challengeId", false)
    }

    fun getCurrentLevel(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_CURRENT_LEVEL, 1)
    }

    fun setCurrentLevel(context: Context, level: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_CURRENT_LEVEL, level).apply()
    }

    fun getTotalCompleted(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_TOTAL_COMPLETED, 0)
    }

    fun getHighestLevel(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_HIGHEST_LEVEL, 0)
    }

    fun clearAllProgress(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
