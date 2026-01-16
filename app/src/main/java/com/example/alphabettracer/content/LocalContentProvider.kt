package com.example.alphabettracer.content

import com.example.alphabettracer.model.AlphabetData

/**
 * Local implementation of ContentProvider using hardcoded data.
 * This can be replaced with RemoteContentProvider for backend integration.
 */
class LocalContentProvider : ContentProvider {

    override suspend fun getAlphabetData(): List<AlphabetData> = AlphabetContent.alphabetList

    override suspend fun getWordSearchTopics(): List<WordSearchTopicContent> = WordSearchContent.topics

    override suspend fun getCountingCategories(): List<CountingCategoryContent> = CountingContent.categories

    override suspend fun getMemoryCategories(): List<MemoryCategoryContent> = MemoryContent.categories

    override suspend fun getPatternSequences(): List<PatternSequenceContent> = PatternContent.sequences

    override suspend fun getStickBuilderLevels(): List<StickBuilderLevelContent> = StickBuilderContent.levels
}

/**
 * Default content repository with simple caching
 */
class DefaultContentRepository(
    private val provider: ContentProvider = LocalContentProvider()
) : ContentRepository {

    private var alphabetCache: List<AlphabetData>? = null
    private var wordSearchCache: List<WordSearchTopicContent>? = null
    private var countingCache: List<CountingCategoryContent>? = null
    private var memoryCache: List<MemoryCategoryContent>? = null
    private var patternCache: List<PatternSequenceContent>? = null
    private var stickBuilderCache: List<StickBuilderLevelContent>? = null

    override suspend fun getAlphabetData(forceRefresh: Boolean): List<AlphabetData> {
        if (forceRefresh || alphabetCache == null) {
            alphabetCache = provider.getAlphabetData()
        }
        return alphabetCache!!
    }

    override suspend fun getWordSearchTopics(forceRefresh: Boolean): List<WordSearchTopicContent> {
        if (forceRefresh || wordSearchCache == null) {
            wordSearchCache = provider.getWordSearchTopics()
        }
        return wordSearchCache!!
    }

    override suspend fun getCountingCategories(forceRefresh: Boolean): List<CountingCategoryContent> {
        if (forceRefresh || countingCache == null) {
            countingCache = provider.getCountingCategories()
        }
        return countingCache!!
    }

    override suspend fun getMemoryCategories(forceRefresh: Boolean): List<MemoryCategoryContent> {
        if (forceRefresh || memoryCache == null) {
            memoryCache = provider.getMemoryCategories()
        }
        return memoryCache!!
    }

    override suspend fun getPatternSequences(forceRefresh: Boolean): List<PatternSequenceContent> {
        if (forceRefresh || patternCache == null) {
            patternCache = provider.getPatternSequences()
        }
        return patternCache!!
    }

    override suspend fun getStickBuilderLevels(forceRefresh: Boolean): List<StickBuilderLevelContent> {
        if (forceRefresh || stickBuilderCache == null) {
            stickBuilderCache = provider.getStickBuilderLevels()
        }
        return stickBuilderCache!!
    }

    override fun clearCache() {
        alphabetCache = null
        wordSearchCache = null
        countingCache = null
        memoryCache = null
        patternCache = null
        stickBuilderCache = null
    }

    companion object {
        @Volatile
        private var INSTANCE: DefaultContentRepository? = null

        fun getInstance(): DefaultContentRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DefaultContentRepository().also { INSTANCE = it }
            }
        }
    }
}
