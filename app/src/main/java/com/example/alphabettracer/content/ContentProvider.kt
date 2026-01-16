package com.example.alphabettracer.content

import com.example.alphabettracer.model.AlphabetData

/**
 * Interface for providing content data.
 * Implementations can be local (hardcoded) or remote (API).
 * This abstraction allows easy swapping between data sources.
 */
interface ContentProvider {
    suspend fun getAlphabetData(): List<AlphabetData>
    suspend fun getWordSearchTopics(): List<WordSearchTopicContent>
    suspend fun getCountingCategories(): List<CountingCategoryContent>
    suspend fun getMemoryCategories(): List<MemoryCategoryContent>
    suspend fun getPatternSequences(): List<PatternSequenceContent>
    suspend fun getStickBuilderLevels(): List<StickBuilderLevelContent>
}

/**
 * Content models for future backend integration
 */

data class WordSearchTopicContent(
    val id: String,
    val name: String,
    val icon: String,
    val words: List<String>,
    val difficulty: Int = 1
)

data class CountingCategoryContent(
    val id: String,
    val name: String,
    val items: List<String>,  // Emojis or image URLs
    val minCount: Int = 1,
    val maxCount: Int = 10
)

data class MemoryCategoryContent(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Long,  // Color as Long for serialization
    val pairs: List<String>  // Emojis or image URLs
)

data class PatternSequenceContent(
    val id: String,
    val category: String,
    val sequence: List<String>,
    val answer: String,
    val options: List<String>,
    val difficulty: Int = 1
)

data class StickBuilderLevelContent(
    val id: Int,
    val equation: String,
    val answer: String,
    val segmentPattern: List<Int>,  // Which segments to light up (0-6)
    val difficulty: Int = 1
)

/**
 * Repository interface for managing content with caching
 */
interface ContentRepository {
    suspend fun getAlphabetData(forceRefresh: Boolean = false): List<AlphabetData>
    suspend fun getWordSearchTopics(forceRefresh: Boolean = false): List<WordSearchTopicContent>
    suspend fun getCountingCategories(forceRefresh: Boolean = false): List<CountingCategoryContent>
    suspend fun getMemoryCategories(forceRefresh: Boolean = false): List<MemoryCategoryContent>
    suspend fun getPatternSequences(forceRefresh: Boolean = false): List<PatternSequenceContent>
    suspend fun getStickBuilderLevels(forceRefresh: Boolean = false): List<StickBuilderLevelContent>
    fun clearCache()
}
