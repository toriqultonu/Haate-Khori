package com.example.alphabettracer.data

import android.content.Context

/**
 * Storage for Memory Match Game progress
 */
object MemoryMatchStorage {
    private const val PREFS_NAME = "memory_match_prefs"
    private const val KEY_GAMES_WON = "games_won"
    private const val KEY_TOTAL_GAMES = "total_games"
    private const val KEY_BEST_MOVES_PREFIX = "best_moves_"
    private const val KEY_BEST_TIME_PREFIX = "best_time_"
    private const val KEY_TOTAL_MATCHES = "total_matches"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getGamesWon(context: Context): Int =
        getPrefs(context).getInt(KEY_GAMES_WON, 0)

    fun incrementGamesWon(context: Context) {
        val current = getGamesWon(context)
        getPrefs(context).edit().putInt(KEY_GAMES_WON, current + 1).apply()
    }

    fun getTotalGames(context: Context): Int =
        getPrefs(context).getInt(KEY_TOTAL_GAMES, 0)

    fun incrementTotalGames(context: Context) {
        val current = getTotalGames(context)
        getPrefs(context).edit().putInt(KEY_TOTAL_GAMES, current + 1).apply()
    }

    fun getBestMoves(context: Context, difficulty: String): Int =
        getPrefs(context).getInt("$KEY_BEST_MOVES_PREFIX$difficulty", Int.MAX_VALUE)

    fun saveBestMoves(context: Context, difficulty: String, moves: Int) {
        val currentBest = getBestMoves(context, difficulty)
        if (moves < currentBest) {
            getPrefs(context).edit().putInt("$KEY_BEST_MOVES_PREFIX$difficulty", moves).apply()
        }
    }

    fun getBestTime(context: Context, difficulty: String): Int =
        getPrefs(context).getInt("$KEY_BEST_TIME_PREFIX$difficulty", Int.MAX_VALUE)

    fun saveBestTime(context: Context, difficulty: String, timeSeconds: Int) {
        val currentBest = getBestTime(context, difficulty)
        if (timeSeconds < currentBest) {
            getPrefs(context).edit().putInt("$KEY_BEST_TIME_PREFIX$difficulty", timeSeconds).apply()
        }
    }

    fun getTotalMatches(context: Context): Int =
        getPrefs(context).getInt(KEY_TOTAL_MATCHES, 0)

    fun addMatches(context: Context, count: Int) {
        val current = getTotalMatches(context)
        getPrefs(context).edit().putInt(KEY_TOTAL_MATCHES, current + count).apply()
    }
}
