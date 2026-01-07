package com.example.alphabettracer.data

import android.content.Context
import com.example.alphabettracer.model.MatchResult

object LetterStorage {
    private const val PREFS_NAME = "haate_khori_prefs"
    private const val KEY_PREFIX = "letter_result_"

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
}