package com.example.alphabettracer.data

import android.content.Context
import com.example.alphabettracer.model.Achievement
import com.example.alphabettracer.model.MatchResult

object AchievementStorage {
    private const val PREFS_NAME = "haate_khori_achievements"
    private const val KEY_PREFIX = "achievement_"
    private const val KEY_COLORS_USED = "colors_used"
    private const val KEY_MAX_STREAK = "max_streak"

    fun isAchievementUnlocked(context: Context, achievement: Achievement): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("$KEY_PREFIX${achievement.id}", false)
    }

    fun unlockAchievement(context: Context, achievement: Achievement) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("$KEY_PREFIX${achievement.id}", true).apply()
    }

    fun getUnlockedAchievements(context: Context): Set<Achievement> {
        return Achievement.entries.filter { isAchievementUnlocked(context, it) }.toSet()
    }

    fun recordColorUsed(context: Context, colorIndex: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colorsUsed = prefs.getStringSet(KEY_COLORS_USED, mutableSetOf()) ?: mutableSetOf()
        val updatedColors = colorsUsed.toMutableSet().apply { add(colorIndex.toString()) }
        prefs.edit().putStringSet(KEY_COLORS_USED, updatedColors).apply()
    }

    fun getColorsUsedCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_COLORS_USED, emptySet())?.size ?: 0
    }

    fun updateMaxStreak(context: Context, streak: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentMax = prefs.getInt(KEY_MAX_STREAK, 0)
        if (streak > currentMax) {
            prefs.edit().putInt(KEY_MAX_STREAK, streak).apply()
        }
    }

    fun getMaxStreak(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_MAX_STREAK, 0)
    }

    /**
     * Check all achievements and return any newly unlocked ones
     */
    fun checkAndUnlockAchievements(
        context: Context,
        totalStars: Int,
        currentStreak: Int,
        colorsUsed: Int
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        // First Star - first excellent trace
        if (totalStars >= 1 && !isAchievementUnlocked(context, Achievement.FIRST_STAR)) {
            unlockAchievement(context, Achievement.FIRST_STAR)
            newlyUnlocked.add(Achievement.FIRST_STAR)
        }

        // High Five - 5 excellent traces
        if (totalStars >= 5 && !isAchievementUnlocked(context, Achievement.HIGH_FIVE)) {
            unlockAchievement(context, Achievement.HIGH_FIVE)
            newlyUnlocked.add(Achievement.HIGH_FIVE)
        }

        // Perfect Ten - 10 excellent traces
        if (totalStars >= 10 && !isAchievementUnlocked(context, Achievement.PERFECT_TEN)) {
            unlockAchievement(context, Achievement.PERFECT_TEN)
            newlyUnlocked.add(Achievement.PERFECT_TEN)
        }

        // Quick Learner - 13 letters mastered
        if (totalStars >= 13 && !isAchievementUnlocked(context, Achievement.QUICK_LEARNER)) {
            unlockAchievement(context, Achievement.QUICK_LEARNER)
            newlyUnlocked.add(Achievement.QUICK_LEARNER)
        }

        // Alphabet Champion - all 26 letters mastered
        if (totalStars >= 26 && !isAchievementUnlocked(context, Achievement.ALPHABET_CHAMPION)) {
            unlockAchievement(context, Achievement.ALPHABET_CHAMPION)
            newlyUnlocked.add(Achievement.ALPHABET_CHAMPION)
        }

        // Hot Streak - 5 excellent in a row
        if (currentStreak >= 5 && !isAchievementUnlocked(context, Achievement.HOT_STREAK)) {
            unlockAchievement(context, Achievement.HOT_STREAK)
            newlyUnlocked.add(Achievement.HOT_STREAK)
        }

        // Rainbow Artist - used all 6 colors
        if (colorsUsed >= 6 && !isAchievementUnlocked(context, Achievement.RAINBOW_ARTIST)) {
            unlockAchievement(context, Achievement.RAINBOW_ARTIST)
            newlyUnlocked.add(Achievement.RAINBOW_ARTIST)
        }

        return newlyUnlocked
    }

    fun clearAllAchievements(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
