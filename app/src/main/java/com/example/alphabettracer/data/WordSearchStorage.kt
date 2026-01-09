package com.example.alphabettracer.data

import android.content.Context

object WordSearchStorage {
    private const val PREFS_NAME = "word_search_prefs"
    private const val KEY_COMPLETED_PREFIX = "topic_completed_"
    private const val KEY_BEST_TIME_PREFIX = "topic_best_time_"
    private const val KEY_TOTAL_GAMES = "total_games_completed"

    fun saveTopicCompleted(context: Context, topicId: String, timeTakenSeconds: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Mark as completed
        editor.putBoolean("$KEY_COMPLETED_PREFIX$topicId", true)

        // Save best time if better
        val currentBest = getBestTime(context, topicId)
        if (currentBest == 0L || timeTakenSeconds < currentBest) {
            editor.putLong("$KEY_BEST_TIME_PREFIX$topicId", timeTakenSeconds)
        }

        // Increment total games
        val totalGames = getTotalGamesCompleted(context)
        editor.putInt(KEY_TOTAL_GAMES, totalGames + 1)

        editor.apply()
    }

    fun isTopicCompleted(context: Context, topicId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("$KEY_COMPLETED_PREFIX$topicId", false)
    }

    fun getBestTime(context: Context, topicId: String): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong("$KEY_BEST_TIME_PREFIX$topicId", 0L)
    }

    fun getTotalGamesCompleted(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_TOTAL_GAMES, 0)
    }

    fun getCompletedTopicsCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return wordSearchTopics.count { topic ->
            prefs.getBoolean("$KEY_COMPLETED_PREFIX${topic.id}", false)
        }
    }

    fun clearAllProgress(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
