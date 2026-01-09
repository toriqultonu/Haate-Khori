package com.example.alphabettracer.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.alphabettracer.R
import com.example.alphabettracer.data.LetterStorage

/**
 * SoundManager - Handles all sound effects for the app
 * Uses SoundPool for low-latency playback of short sounds
 */
object SoundManager {
    private var soundPool: SoundPool? = null
    private var isInitialized = false

    // Sound IDs
    private var soundExcellent: Int = 0
    private var soundGood: Int = 0
    private var soundTryAgain: Int = 0
    private var soundButtonClick: Int = 0
    private var soundAchievement: Int = 0
    private var soundConfetti: Int = 0
    private var soundStrokeStart: Int = 0
    private var soundStrokeEnd: Int = 0
    private var soundLetterSelect: Int = 0
    private var soundNextLetter: Int = 0
    private var soundColorPick: Int = 0
    private var soundStreak: Int = 0
    private var soundErase: Int = 0
    private var soundClear: Int = 0
    private var soundDemoStart: Int = 0
    private var soundUndo: Int = 0

    /**
     * Initialize the SoundPool and load all sounds
     * Call this once when the app starts
     */
    fun initialize(context: Context) {
        if (isInitialized) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load all sounds
        soundPool?.let { pool ->
            soundExcellent = pool.load(context, R.raw.sound_excellent, 1)
            soundGood = pool.load(context, R.raw.sound_good, 1)
            soundTryAgain = pool.load(context, R.raw.sound_try_again, 1)
            soundButtonClick = pool.load(context, R.raw.sound_button_click, 1)
            soundAchievement = pool.load(context, R.raw.sound_achievement, 1)
            soundConfetti = pool.load(context, R.raw.sound_confetti, 1)
            soundStrokeStart = pool.load(context, R.raw.sound_stroke_start, 1)
            soundStrokeEnd = pool.load(context, R.raw.sound_stroke_end, 1)
            soundLetterSelect = pool.load(context, R.raw.sound_letter_select, 1)
            soundNextLetter = pool.load(context, R.raw.sound_next_letter, 1)
            soundColorPick = pool.load(context, R.raw.sound_color_pick, 1)
            soundStreak = pool.load(context, R.raw.sound_streak, 1)
            soundErase = pool.load(context, R.raw.sound_clear, 1)
            soundClear = pool.load(context, R.raw.sound_clear, 1)
            soundDemoStart = pool.load(context, R.raw.sound_demo_start, 1)
            soundUndo = pool.load(context, R.raw.sound_undo, 1)
        }

        isInitialized = true
    }

    /**
     * Play a sound if sound is enabled
     */
    private fun playSound(context: Context, soundId: Int, volume: Float = 1.0f) {
        if (!LetterStorage.getSoundEnabled(context)) return
        if (soundId == 0) return
        soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
    }

    // High Priority Sounds
    fun playExcellent(context: Context) = playSound(context, soundExcellent)
    fun playGood(context: Context) = playSound(context, soundGood)
    fun playTryAgain(context: Context) = playSound(context, soundTryAgain)
    fun playButtonClick(context: Context) = playSound(context, soundButtonClick, 0.7f)
    fun playAchievement(context: Context) = playSound(context, soundAchievement)
    fun playConfetti(context: Context) = playSound(context, soundConfetti)
    fun playStrokeStart(context: Context) = playSound(context, soundStrokeStart, 0.5f)
    fun playStrokeEnd(context: Context) = playSound(context, soundStrokeEnd, 0.5f)

    // Medium Priority Sounds
    fun playLetterSelect(context: Context) = playSound(context, soundLetterSelect, 0.8f)
    fun playNextLetter(context: Context) = playSound(context, soundNextLetter, 0.7f)
    fun playColorPick(context: Context) = playSound(context, soundColorPick, 0.6f)
    fun playStreak(context: Context) = playSound(context, soundStreak)
    fun playErase(context: Context) = playSound(context, soundErase, 0.6f)
    fun playClear(context: Context) = playSound(context, soundClear, 0.7f)

    // Optional Sounds
    fun playDemoStart(context: Context) = playSound(context, soundDemoStart, 0.7f)
    fun playUndo(context: Context) = playSound(context, soundUndo, 0.6f)

    /**
     * Toggle sound on/off
     */
    fun toggleSound(context: Context): Boolean {
        val newState = !LetterStorage.getSoundEnabled(context)
        LetterStorage.setSoundEnabled(context, newState)
        return newState
    }

    /**
     * Check if sound is enabled
     */
    fun isSoundEnabled(context: Context): Boolean {
        return LetterStorage.getSoundEnabled(context)
    }

    /**
     * Release resources when app is destroyed
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        isInitialized = false
    }
}
