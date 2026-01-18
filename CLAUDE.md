# CLAUDE.md - HaateKhori Project Context

## Project Overview

**HaateKhori** (Bengali: "হাতে খড়ি" - meaning "first step in learning") is an interactive Android educational app designed to help kids learn to write English letters through touch-based tracing with real-time feedback, achievements, and multiple mini-games.

### Tech Stack
- **Language:** Kotlin 2.0
- **UI Framework:** Jetpack Compose with Material Design 3
- **Navigation:** Navigation Compose
- **Data Persistence:** SharedPreferences
- **Custom Drawing:** Canvas API
- **Min SDK:** 31 (Android 12)
- **Target SDK:** 36 (Android 15)

---

## Project Structure

```
app/src/main/java/com/example/alphabettracer/
├── MainActivity.kt                     # App entry point
├── constants/                          # App-wide constants
│   ├── AppColors.kt                    # Color palette definitions
│   ├── AppStrings.kt                   # String constants & emojis
│   ├── AppDimensions.kt                # Sizing, padding, typography
│   └── AppConfig.kt                    # Configuration settings
├── content/                            # Content data providers
│   ├── ContentProvider.kt              # Base interface for content
│   ├── LocalContentProvider.kt         # Local content implementation
│   ├── AlphabetContent.kt              # 26 letters with words/sentences
│   ├── WordSearchContent.kt            # 8 word search topics
│   ├── CountingContent.kt              # Counting game categories
│   ├── MemoryContent.kt                # Memory match categories
│   ├── PatternContent.kt               # Pattern game sequences
│   ├── StickBuilderContent.kt          # 7-segment display levels
│   └── ColoringContent.kt              # 12 coloring pages
├── data/                               # Data models & persistence
│   ├── LetterStorage.kt                # Letter progress persistence
│   ├── AchievementStorage.kt           # Achievement tracking
│   ├── WordSearchStorage.kt            # Word search game state
│   ├── StickBuilderStorage.kt          # Stick builder progress
│   ├── CountingGameStorage.kt          # Counting game stats
│   ├── MemoryMatchStorage.kt           # Memory match stats
│   ├── PatternStorage.kt               # Pattern game stats
│   ├── AlphabetList.kt                 # Alphabet data wrapper
│   └── *Data.kt                        # Various data definitions
├── model/                              # Data models & enums
│   ├── AlphabetData.kt                 # Letter + Word + Sentence
│   ├── DrawStroke.kt                   # User drawn stroke
│   ├── MatchResult.kt                  # NONE, POOR, GOOD, EXCELLENT
│   ├── Achievement.kt                  # 6 achievements
│   ├── ScreenState.kt                  # Navigation states
│   ├── WordSearchModels.kt             # Word search models
│   └── MemoryCard.kt                   # Memory game models
├── navigation/                         # Navigation system
│   ├── AppNavigation.kt                # Routes & nav functions
│   └── AppNavHost.kt                   # NavHost with transitions
└── ui/
    ├── screens/                        # Full-screen composables
    │   ├── AlphabetTracingApp.kt       # Main app container
    │   ├── LetterGridScreen.kt         # Home screen
    │   ├── LetterSelectionScreen.kt    # A-Z letter selection
    │   ├── TracingScreen.kt            # Letter tracing
    │   ├── WordSearchTopicScreen.kt    # Topic selection
    │   ├── WordSearchGameScreen.kt     # Word puzzle game
    │   ├── StickBuilderScreen.kt       # 7-segment builder
    │   ├── CountingGameScreen.kt       # Count emojis game
    │   ├── MemoryMatchScreen.kt        # Card matching game
    │   ├── PatternGameScreen.kt        # Pattern recognition
    │   └── ColoringScreen.kt           # Coloring book
    ├── components/                     # Reusable composables
    │   ├── TracingCanvas.kt            # Drawing canvas
    │   ├── ColoringCanvas.kt           # Coloring canvas
    │   ├── LetterGridItem.kt           # Letter card
    │   ├── AchievementBadge.kt         # Achievement badge
    │   ├── AchievementPopup.kt         # Unlock dialog
    │   ├── ConfettiAnimation.kt        # Celebration particles
    │   ├── AchievementSection.kt       # Achievement display
    │   └── WordSearchTutorial.kt       # Interactive tutorial overlay
    └── theme/                          # Material theming
        ├── Theme.kt
        ├── Color.kt
        └── Type.kt
```

---

## Features & Screens

### 1. Letter Tracing (Core Feature)
**Files:** `TracingScreen.kt`, `TracingCanvas.kt`

- Touch-based letter drawing for A-Z
- Visual guide dots showing correct tracing path
- Faded letter background reference
- Demo animation showing correct tracing
- 6 color options with adjustable stroke width
- Pencil/Eraser toggle and undo functionality
- Real-time detection scoring:
  - EXCELLENT (>70%): Green indicator
  - GOOD (>55%): Yellow indicator
  - POOR (>35%): Orange indicator
  - NONE: No indicator

### 2. Home Screen
**File:** `LetterGridScreen.kt`

- Progress overview with completion count
- Quick access to all game modes:
  - Practice Letters (big card)
  - Coloring Book (big card)
  - Word Search, Stick Builder (medium buttons)
  - Count, Memory, Pattern (mini buttons)

### 3. Word Search
**Files:** `WordSearchTopicScreen.kt`, `WordSearchGameScreen.kt`, `WordSearchTutorial.kt`

- 8 topics: Countries, Body Parts, Vehicles, Food, Animals, Colors, Fruits, Family
- 10x10 grid with 4 search directions
- Drag selection with color highlighting
- Timer tracking with best times
- Save/resume game state
- **First-time interactive tutorial:**
  - Shows on first game launch with animated hand pointer
  - 4-step walkthrough: Introduction, Touch & Drag, Any Direction, Ready to Play
  - Animated hand demonstrates horizontal and diagonal selection on actual grid
  - Auto-advances every 3.5 seconds or tap to continue
  - Skip button available to dismiss immediately
  - Tutorial state persisted in SharedPreferences

### 4. Stick Builder
**File:** `StickBuilderScreen.kt`

- 30 levels building 7-segment display numbers
- Levels 1-10: Build single digits (0-9)
- Levels 11-20: Addition equations
- Levels 21-30: Subtraction equations
- Drag colored sticks to match patterns
- Confetti celebration on completion

### 5. Counting Game
**File:** `CountingGameScreen.kt`

- 5 emoji categories with 3 difficulty levels
- 10 questions per game
- Multiple choice answers
- Score and streak tracking

### 6. Memory Match
**File:** `MemoryMatchScreen.kt`

- 6 categories: Animals, Fruits, Numbers, Shapes, Sports, Food
- 4 difficulties: Easy (4 pairs) to Expert (12 pairs)
- Card flip animations
- Move/time tracking with star rating

### 7. Pattern Recognition
**File:** `PatternGameScreen.kt`

- 4 pattern types: Shapes, Colors, Numbers, Emojis
- "What comes next?" puzzles
- 10 questions per game with 4 options

### 8. Coloring Book
**Files:** `ColoringScreen.kt`, `ColoringCanvas.kt`

- 12 pages: Star, Heart, Sun, Flower, Butterfly, House, Fish, Car, Tree, Rainbow, Rocket, Ice Cream
- Free-form drawing with 10 colors
- Adjustable brush size
- Eraser, undo, and clear functions

---

## Data Models

### Core Enums

```kotlin
enum class MatchResult { NONE, POOR, GOOD, EXCELLENT }

enum class Achievement {
    FIRST_STAR,        // 1 excellent trace
    HIGH_FIVE,         // 5 excellent traces
    PERFECT_TEN,       // 10 excellent traces
    QUICK_LEARNER,     // Master 13 letters
    ALPHABET_CHAMPION, // Master all 26 letters
    HOT_STREAK         // 15+ consecutive excellent
}
```

### Key Data Classes

```kotlin
data class AlphabetData(letter: Char, word: String, sentence: String)
data class DrawStroke(points: List<Offset>, color: Color, width: Float)
data class DrawingPath(points: List<Offset>, color: Color, strokeWidth: Float)
data class ColoringPage(id: String, name: String, emoji: String, difficulty: Int)
data class WordSearchTopic(id: String, name: String, icon: String, color: Color, words: List<String>)
data class MemoryCard(id: Int, content: String, pairId: Int, isFlipped: Boolean, isMatched: Boolean)
data class PatternQuestion(sequence: List<String>, correctAnswer: String, options: List<String>)
```

---

## Navigation

### Routes (Sealed Class)
```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object LetterSelection : Screen("letter_selection")
    data object Tracing : Screen("tracing/{letterIndex}")
    data object WordSearchTopics : Screen("word_search_topics")
    data object WordSearchGame : Screen("word_search_game/{topicId}")
    data object StickBuilder : Screen("stick_builder")
    data object CountingGame : Screen("counting_game")
    data object MemoryMatch : Screen("memory_match")
    data object PatternGame : Screen("pattern_game")
    data object Coloring : Screen("coloring")
}
```

### Navigation Functions
```kotlin
navController.navigateToHome()
navController.navigateToLetterSelection()
navController.navigateToTracing(letterIndex: Int)
navController.navigateToWordSearchTopics()
navController.navigateToWordSearchGame(topicId: String)
navController.navigateToStickBuilder()
navController.navigateToCountingGame()
navController.navigateToMemoryMatch()
navController.navigateToPatternGame()
navController.navigateToColoring()
```

---

## Content Providers

### Interface
```kotlin
interface ContentProvider {
    suspend fun getAlphabetData(): List<AlphabetData>
    suspend fun getWordSearchTopics(): List<WordSearchTopicContent>
    suspend fun getCountingCategories(): List<CountingCategoryContent>
    suspend fun getMemoryCategories(): List<MemoryCategoryContent>
    suspend fun getPatternSequences(): List<PatternSequenceContent>
    suspend fun getStickBuilderLevels(): List<StickBuilderLevelContent>
}
```

### Content Data Classes (for API integration)
```kotlin
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
    val items: List<String>,
    val minCount: Int = 1,
    val maxCount: Int = 10
)

data class MemoryCategoryContent(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Long,
    val pairs: List<String>
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
    val segmentPattern: List<Int>,
    val difficulty: Int = 1
)
```

---

## Storage (SharedPreferences)

| Storage Class | Preferences Name | Purpose |
|---------------|------------------|---------|
| `LetterStorage` | `haate_khori_prefs` | Letter progress, color/stroke preferences |
| `AchievementStorage` | `haate_khori_achievements` | Achievements, streaks |
| `WordSearchStorage` | `word_search_prefs` | Game state, completion, times, tutorial shown flag |
| `StickBuilderStorage` | `stick_builder_prefs` | Level progress |
| `CountingGameStorage` | `counting_game_prefs` | Scores, stats |
| `MemoryMatchStorage` | `memory_match_prefs` | Best moves/times |
| `PatternStorage` | `pattern_game_prefs` | Scores, stats |

---

## Letter Tracing Detection Algorithm

### How It Works
1. **Path Generation:** Each letter has 40-70+ interpolated points defining its shape
2. **User Drawing:** Captures list of Offset points as user drags
3. **Coverage Analysis:** % of letter path traced by user (min 40%)
4. **Accuracy Analysis:** % of user's strokes on the letter path (min 30%)
5. **Score Calculation:** `(coverage × 50%) + (accuracy × 50%)`
6. **Result Mapping:**
   - `>70%` → EXCELLENT
   - `>55%` → GOOD
   - `>35%` → POOR
   - `≤35%` → NONE

### Tolerance
- 10% of canvas size for point matching
- Allows imperfect tracing while maintaining quality

---

## Key Components

### TracingCanvas
Custom canvas for letter tracing with:
- Gesture detection (drag to draw)
- Configurable colors (6 options) and stroke widths (8-40dp)
- Letter path generation for all 26 letters
- Demo animation playback
- Detection algorithm implementation

### ColoringCanvas
Free-form drawing canvas with:
- Point-based smooth strokes (same technique as TracingCanvas)
- Eraser support (draws white)
- Adjustable brush size (5-50dp)
- Shape outlines drawn on top of coloring

### ConfettiAnimation
Particle-based celebration:
- 80 colored particles
- Falling motion with wobble and rotation
- 3-second duration
- Callback on completion

### WordSearchTutorial
Interactive first-time tutorial overlay:
- Semi-transparent overlay on game screen
- 4-step instruction flow with step indicator dots
- Animated hand pointer (👆) demonstrating drag gestures
- Canvas-based selection trail animation (horizontal/diagonal)
- Auto-advance with tap-to-continue option
- Skip button for experienced users
- Persists "tutorial shown" flag via `WordSearchStorage`

---

## UI Design Principles

- **Kid-Friendly:** Vibrant colors, large touch targets (48dp+), rounded corners
- **Encouraging:** Positive feedback messages, celebration animations
- **Accessible:** High contrast, color-blind friendly palette
- **Consistent:** Material Design 3 with custom theming
- **Animated:** Staggered entry animations, smooth transitions

---

## Color Palette (Kid-Friendly)

```kotlin
// Primary
val Primary = Color(0xFF6200EE)        // Purple

// Status Colors
val Success = Color(0xFF4CAF50)        // Green
val Warning = Color(0xFFFFC107)        // Yellow
val Error = Color(0xFFF44336)          // Red

// Result Colors
val ResultExcellent = Color(0xFF4CAF50) // Green
val ResultGood = Color(0xFFFFC107)      // Yellow
val ResultPoor = Color(0xFFFF9800)      // Orange

// Game Colors
val WordSearch = Color(0xFF2196F3)     // Blue
val StickBuilder = Color(0xFF8B4513)   // Brown
val Counting = Color(0xFF4CAF50)       // Green
val MemoryMatch = Color(0xFF9C27B0)    // Purple
val Pattern = Color(0xFFFF9800)        // Orange
val Coloring = Color(0xFFE91E63)       // Pink
```

---

## Dependencies

```kotlin
// Core
androidx-core-ktx
androidx-lifecycle-runtime-ktx
androidx-activity-compose

// Compose
androidx-compose-bom
compose-ui
compose-ui-graphics
compose-material3

// Navigation
androidx-navigation-compose

// JSON (for state serialization)
org.json
```

---

## Common Development Tasks

### Adding a New Game Screen
1. Create screen composable in `ui/screens/`
2. Add content data in `content/` package
3. Add storage class in `data/` package
4. Add route to `navigation/AppNavigation.kt`
5. Add composable to `navigation/AppNavHost.kt`
6. Add navigation callback to `LetterGridScreen.kt`

### Adding New Content
1. Update content object in `content/` package
2. If API-backed: Update `ContentProvider.kt` interface
3. Add corresponding data class if needed

### Modifying Letter Tracing
- Letter paths: `TracingCanvas.kt` → `getLetterPath()` function
- Detection algorithm: `TracingCanvas.kt` → `checkDrawingMatch()` function
- Stroke styling: `TracingCanvas.kt` → stroke configuration

### Adding New Achievement
1. Add enum value to `model/Achievement.kt`
2. Add unlock logic to `AchievementStorage.kt`
3. Achievement automatically tracked and displayed

### Adding First-Time Tutorial to a Game
1. Add `hasShownTutorial()` and `markTutorialShown()` functions to game's storage class
2. Create tutorial component in `ui/components/` (see `WordSearchTutorial.kt` as reference)
3. Add tutorial state and visibility check in game screen using `LaunchedEffect`
4. Wrap game content in `Box` and overlay tutorial component
5. Pause game timer while tutorial is showing

---

## API Integration Notes

The app is designed for future backend integration:

1. **Content Provider Interface:** All content can be fetched from API
2. **Data Classes:** Ready for JSON serialization
3. **Repository Pattern:** `ContentRepository` interface with caching support
4. **API Specification:** See `docs/API_SPECIFICATION.html` for full API docs

### Endpoints Needed
- `GET /api/v1/alphabet` - Letter data
- `GET /api/v1/word-search/topics` - Word search topics
- `GET /api/v1/counting/categories` - Counting categories
- `GET /api/v1/memory/categories` - Memory match categories
- `GET /api/v1/patterns` - Pattern sequences
- `GET /api/v1/stick-builder/levels` - Stick builder levels
- `GET /api/v1/coloring/pages` - Coloring pages
- `GET /api/v1/content/all` - Bulk content for offline

---

## Testing

- **Unit Tests:** `app/src/test/` - JUnit tests
- **UI Tests:** `app/src/androidTest/` - Espresso & Compose tests

---

## Git Workflow

- **Main Branch:** `main` - Production ready
- **Feature Branch:** `feature` - Active development

Recent commits focus on:
- Adding mini-games (Counting, Memory, Pattern)
- Confetti animations
- Coloring book feature
- Navigation improvements
- Word Search interactive tutorial with animated hand pointer

---

## Quick Reference

### File Locations
| What | Where |
|------|-------|
| Main entry | `MainActivity.kt` |
| App container | `ui/screens/AlphabetTracingApp.kt` |
| Home screen | `ui/screens/LetterGridScreen.kt` |
| Navigation | `navigation/AppNavigation.kt`, `AppNavHost.kt` |
| Letter tracing | `ui/screens/TracingScreen.kt`, `ui/components/TracingCanvas.kt` |
| Coloring | `ui/screens/ColoringScreen.kt`, `ui/components/ColoringCanvas.kt` |
| Word Search tutorial | `ui/components/WordSearchTutorial.kt` |
| All content | `content/*.kt` |
| All storage | `data/*Storage.kt` |
| All models | `model/*.kt` |
| Constants | `constants/*.kt` |
| Theme | `ui/theme/*.kt` |

### Common Patterns
- **State:** `remember { mutableStateOf(...) }`
- **Persistence:** `Context.getSharedPreferences(...)`
- **Navigation:** `navController.navigate(Screen.Route.route)`
- **Animation:** `AnimatedVisibility`, `animateFloatAsState`
- **Drawing:** `Canvas { ... }` with `drawPath`, `drawCircle`, etc.

---

## Notes for AI Assistants

1. **This is a kids app** - Keep UI simple, colorful, and encouraging
2. **Compose-first** - All UI is Jetpack Compose, no XML layouts
3. **No external networking yet** - All content is local
4. **SharedPreferences for storage** - No Room/SQLite database
5. **Single activity** - Navigation handled by Navigation Compose
6. **Content is separated** - Easy to swap for API data later
7. **Kid-safe** - No ads, no in-app purchases, no external links
