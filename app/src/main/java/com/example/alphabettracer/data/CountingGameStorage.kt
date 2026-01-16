package com.example.alphabettracer.data

import android.content.Context

/**
 * Storage for Counting Game progress
 */
object CountingGameStorage {
    private const val PREFS_NAME = "counting_game_prefs"
    private const val KEY_HIGH_SCORE = "high_score"
    private const val KEY_TOTAL_CORRECT = "total_correct"
    private const val KEY_GAMES_PLAYED = "games_played"
    private const val KEY_BEST_STREAK = "best_streak"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getHighScore(context: Context): Int =
        getPrefs(context).getInt(KEY_HIGH_SCORE, 0)

    fun saveHighScore(context: Context, score: Int) {
        val currentHigh = getHighScore(context)
        if (score > currentHigh) {
            getPrefs(context).edit().putInt(KEY_HIGH_SCORE, score).apply()
        }
    }

    fun getTotalCorrect(context: Context): Int =
        getPrefs(context).getInt(KEY_TOTAL_CORRECT, 0)

    fun incrementTotalCorrect(context: Context) {
        val current = getTotalCorrect(context)
        getPrefs(context).edit().putInt(KEY_TOTAL_CORRECT, current + 1).apply()
    }

    fun getGamesPlayed(context: Context): Int =
        getPrefs(context).getInt(KEY_GAMES_PLAYED, 0)

    fun incrementGamesPlayed(context: Context) {
        val current = getGamesPlayed(context)
        getPrefs(context).edit().putInt(KEY_GAMES_PLAYED, current + 1).apply()
    }

    fun getBestStreak(context: Context): Int =
        getPrefs(context).getInt(KEY_BEST_STREAK, 0)

    fun saveBestStreak(context: Context, streak: Int) {
        val currentBest = getBestStreak(context)
        if (streak > currentBest) {
            getPrefs(context).edit().putInt(KEY_BEST_STREAK, streak).apply()
        }
    }
}
