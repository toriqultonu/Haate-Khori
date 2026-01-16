package com.example.alphabettracer.data

import com.example.alphabettracer.content.AlphabetContent
import com.example.alphabettracer.model.AlphabetData

/**
 * Complete list of alphabet letters A-Z with associated words and sentences.
 * Delegates to AlphabetContent for centralized content management.
 *
 * @deprecated Use AlphabetContent.alphabetList directly for new code.
 */
val alphabetList: List<AlphabetData>
    get() = AlphabetContent.alphabetList
