package com.example.alphabettracer.data

import android.content.Context
import com.example.alphabettracer.model.MatchResult

object LetterStorage {
    private const val PREFS_NAME = "haate_khori_prefs"
    private const val KEY_PREFIX = "letter_result_"
    private const val KEY_SELECTED_COLOR = "selected_color_index"
    private const val KEY_STROKE_WIDTH = "stroke_width"
    private const val KEY_TUTORIAL_SHOWN = "tracing_tutorial_shown"

    fun saveLetterResult(context: Context, letterIndex: Int, result: MatchResult) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentResult = getLetterResult(context, letterIndex)
        // Only save if new result is better than existing
        if (result.ordinal > currentResult.ordinal) {
            prefs.edit().putInt("$KEY_PREFIX$letterIndex", result.ordinal).apply()
        }
    }

    fun getLetterResult(context: Context, letterIndex: Int): MatchResult {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val ordinal = prefs.getInt("$KEY_PREFIX$letterIndex", MatchResult.NONE.ordinal)
        return MatchResult.entries[ordinal]
    }

    fun getAllResults(context: Context, count: Int): Map<Int, MatchResult> {
        return (0 until count).associateWith { getLetterResult(context, it) }
    }

    fun getTotalStars(context: Context, count: Int): Int {
        return (0 until count).count { getLetterResult(context, it) == MatchResult.EXCELLENT }
    }

    fun clearAllResults(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    // Color preference persistence
    fun saveSelectedColor(context: Context, colorIndex: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_SELECTED_COLOR, colorIndex).apply()
    }

    fun getSelectedColor(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_SELECTED_COLOR, 0) // Default to first color (Blue)
    }

    // Stroke width persistence
    fun saveStrokeWidth(context: Context, width: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putFloat(KEY_STROKE_WIDTH, width).apply()
    }

    fun getStrokeWidth(context: Context): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_STROKE_WIDTH, 18f) // Default stroke width
    }

    // Tutorial persistence
    fun hasShownTutorial(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TUTORIAL_SHOWN, false)
    }

    fun markTutorialShown(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_TUTORIAL_SHOWN, true).apply()
    }
}