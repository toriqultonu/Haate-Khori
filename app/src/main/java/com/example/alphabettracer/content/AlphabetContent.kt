package com.example.alphabettracer.content

import com.example.alphabettracer.model.AlphabetData

/**
 * Alphabet content data.
 * Contains all 26 letters with associated words and sentences.
 * Can be replaced with backend data in the future.
 */
object AlphabetContent {

    val alphabetList: List<AlphabetData> = listOf(
        AlphabetData('A', "Apple", "A is for Apple"),
        AlphabetData('B', "Ball", "B is for Ball"),
        AlphabetData('C', "Cat", "C is for Cat"),
        AlphabetData('D', "Dog", "D is for Dog"),
        AlphabetData('E', "Elephant", "E is for Elephant"),
        AlphabetData('F', "Fish", "F is for Fish"),
        AlphabetData('G', "Giraffe", "G is for Giraffe"),
        AlphabetData('H', "House", "H is for House"),
        AlphabetData('I', "Ice cream", "I is for Ice cream"),
        AlphabetData('J', "Jelly", "J is for Jelly"),
        AlphabetData('K', "Kite", "K is for Kite"),
        AlphabetData('L', "Lion", "L is for Lion"),
        AlphabetData('M', "Monkey", "M is for Monkey"),
        AlphabetData('N', "Nest", "N is for Nest"),
        AlphabetData('O', "Orange", "O is for Orange"),
        AlphabetData('P', "Penguin", "P is for Penguin"),
        AlphabetData('Q', "Queen", "Q is for Queen"),
        AlphabetData('R', "Rainbow", "R is for Rainbow"),
        AlphabetData('S', "Sun", "S is for Sun"),
        AlphabetData('T', "Tiger", "T is for Tiger"),
        AlphabetData('U', "Umbrella", "U is for Umbrella"),
        AlphabetData('V', "Violin", "V is for Violin"),
        AlphabetData('W', "Whale", "W is for Whale"),
        AlphabetData('X', "Xylophone", "X is for Xylophone"),
        AlphabetData('Y', "Yak", "Y is for Yak"),
        AlphabetData('Z', "Zebra", "Z is for Zebra")
    )

    /**
     * Emoji mappings for each letter
     */
    val letterEmojis: Map<Char, String> = mapOf(
        'A' to "🍎", 'B' to "🏀", 'C' to "🐱", 'D' to "🐕",
        'E' to "🐘", 'F' to "🐸", 'G' to "🍇", 'H' to "🏠",
        'I' to "🍦", 'J' to "🃏", 'K' to "🔑", 'L' to "🦁",
        'M' to "🐵", 'N' to "👃", 'O' to "🐙", 'P' to "🐷",
        'Q' to "👸", 'R' to "🌈", 'S' to "⭐", 'T' to "🐯",
        'U' to "☂️", 'V' to "🎻", 'W' to "🐳", 'X' to "❌",
        'Y' to "🪀", 'Z' to "🦓"
    )

    fun getEmojiForLetter(letter: String): String {
        return letterEmojis[letter.uppercase().firstOrNull()] ?: "📝"
    }

    fun getAlphabetByLetter(letter: Char): AlphabetData? {
        return alphabetList.find { it.letter.equals(letter, ignoreCase = true) }
    }

    fun getAlphabetByIndex(index: Int): AlphabetData? {
        return alphabetList.getOrNull(index)
    }
}
