package com.example.alphabettracer.data

import android.content.Context
import com.example.alphabettracer.model.PlacedWord
import com.example.alphabettracer.model.WordDirection
import org.json.JSONArray
import org.json.JSONObject

object WordSearchStorage {
    private const val PREFS_NAME = "word_search_prefs"
    private const val KEY_COMPLETED_PREFIX = "topic_completed_"
    private const val KEY_BEST_TIME_PREFIX = "topic_best_time_"
    private const val KEY_TOTAL_GAMES = "total_games_completed"
    private const val KEY_SAVED_GAME_PREFIX = "saved_game_"
    private const val KEY_TUTORIAL_SHOWN = "word_search_tutorial_shown"

    fun hasShownTutorial(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TUTORIAL_SHOWN, false)
    }

    fun markTutorialShown(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_TUTORIAL_SHOWN, true).apply()
    }

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

    // Save game state for a topic
    fun saveGameState(
        context: Context,
        topicId: String,
        grid: Array<CharArray>,
        placedWords: List<PlacedWord>,
        foundWordNames: List<String>,
        elapsedSeconds: Int,
        colorIndex: Int
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val gameState = JSONObject().apply {
            // Save grid as JSON array of strings
            val gridArray = JSONArray()
            for (row in grid) {
                gridArray.put(String(row))
            }
            put("grid", gridArray)

            // Save placed words
            val placedArray = JSONArray()
            for (placed in placedWords) {
                val placedObj = JSONObject().apply {
                    put("word", placed.word)
                    put("startRow", placed.startRow)
                    put("startCol", placed.startCol)
                    put("direction", placed.direction.name)
                }
                placedArray.put(placedObj)
            }
            put("placedWords", placedArray)

            // Save found word names
            val foundArray = JSONArray()
            for (name in foundWordNames) {
                foundArray.put(name)
            }
            put("foundWords", foundArray)

            put("elapsedSeconds", elapsedSeconds)
            put("colorIndex", colorIndex)
        }

        prefs.edit()
            .putString("$KEY_SAVED_GAME_PREFIX$topicId", gameState.toString())
            .apply()
    }

    // Load saved game state for a topic
    fun loadGameState(context: Context, topicId: String): SavedGameState? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString("$KEY_SAVED_GAME_PREFIX$topicId", null) ?: return null

        return try {
            val gameState = JSONObject(jsonString)

            // Load grid
            val gridArray = gameState.getJSONArray("grid")
            val gridSize = gridArray.length()
            val grid = Array(gridSize) { row ->
                gridArray.getString(row).toCharArray()
            }

            // Load placed words
            val placedArray = gameState.getJSONArray("placedWords")
            val placedWords = mutableListOf<PlacedWord>()
            for (i in 0 until placedArray.length()) {
                val placedObj = placedArray.getJSONObject(i)
                placedWords.add(
                    PlacedWord(
                        word = placedObj.getString("word"),
                        startRow = placedObj.getInt("startRow"),
                        startCol = placedObj.getInt("startCol"),
                        direction = WordDirection.valueOf(placedObj.getString("direction"))
                    )
                )
            }

            // Load found word names
            val foundArray = gameState.getJSONArray("foundWords")
            val foundWordNames = mutableListOf<String>()
            for (i in 0 until foundArray.length()) {
                foundWordNames.add(foundArray.getString(i))
            }

            SavedGameState(
                grid = grid,
                placedWords = placedWords,
                foundWordNames = foundWordNames,
                elapsedSeconds = gameState.getInt("elapsedSeconds"),
                colorIndex = gameState.getInt("colorIndex")
            )
        } catch (e: Exception) {
            null
        }
    }

    // Check if there's a saved game for a topic
    fun hasSavedGame(context: Context, topicId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.contains("$KEY_SAVED_GAME_PREFIX$topicId")
    }

    // Clear saved game for a topic
    fun clearSavedGame(context: Context, topicId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove("$KEY_SAVED_GAME_PREFIX$topicId")
            .apply()
    }
}

// Data class to hold saved game state
data class SavedGameState(
    val grid: Array<CharArray>,
    val placedWords: List<PlacedWord>,
    val foundWordNames: List<String>,
    val elapsedSeconds: Int,
    val colorIndex: Int
)
