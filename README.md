# Haate Khori (হাতে খড়ি) - Alphabet Tracer

An interactive Android app designed to help kids learn to write English letters through fun, touch-based tracing with real-time feedback, achievements, and word search games.

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min%20SDK-31-orange)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue)

---

## Features

### Interactive Letter Tracing
- Touch-based drawing canvas for practicing letter shapes (A-Z)
- Visual guide dots showing the correct tracing path
- Faded letter background for reference while drawing
- **Demo animation** showing how to trace each letter correctly

### Smart Detection System
- Real-time analysis of drawn letters
- Checks both **coverage** (did you trace the whole letter?) and **accuracy** (did you stay on the path?)
- Four-tier feedback: Excellent, Good, Poor, or None
- Tolerance-based matching (10% of canvas size)

### Progress Tracking
- Persistent storage - progress saved across app restarts
- Color-coded letter grid showing mastery level:
  - **Green** - Excellent (mastered)
  - **Yellow** - Good (almost there)
  - **Red** - Poor (needs practice)
  - **White** - Not attempted
- Progress bar showing overall completion percentage
- Star counter for excellent tracings

### Achievement System
Unlock 6 achievements as you progress:

| Achievement | Requirement |
|-------------|-------------|
| **First Star** | Complete your first excellent trace |
| **High Five** | Get 5 excellent traces |
| **Perfect Ten** | Get 10 excellent traces |
| **Quick Learner** | Master 13 letters |
| **Alphabet Champion** | Master all 26 letters |
| **Hot Streak** | Achieve 15+ consecutive excellent traces |

- Celebration dialogs with confetti animations on unlock
- Persistent achievement tracking

### Word Search Mini-Game
A fun word search puzzle game with 8 thematic topics:

| Topic | Words |
|-------|-------|
| Countries | USA, India, Japan, Brazil, etc. |
| Body Parts | Head, Hand, Eye, Nose, etc. |
| Vehicles | Car, Bus, Train, Plane, etc. |
| Food | Pizza, Bread, Rice, Soup, etc. |
| Animals | Lion, Tiger, Bear, Cat, etc. |
| Colors | Red, Blue, Green, Yellow, etc. |
| Fruits | Apple, Mango, Orange, Grape, etc. |
| Family | Mom, Dad, Sister, Brother, etc. |

**Game Features:**
- 10x10 grid with randomly placed words
- 4 search directions: Horizontal, Vertical, Diagonal (both ways)
- Interactive drag selection with color-coded found words
- Game state persistence - resume interrupted games
- Timer tracking with best times per topic

### Drawing Tools
- 6 color options (Blue, Red, Green, Orange, Purple, Black)
- Adjustable stroke width slider
- Pencil/Eraser toggle
- Undo last stroke
- Clear canvas
- Show/Hide guide toggle

### Kid-Friendly Design
- Large, easy-to-tap letter grid designed for small hands
- Vibrant, colorful UI with gradient backgrounds
- Encouraging feedback messages
- Rounded corners for a soft, friendly appearance

---

## Screenshots

| Letter Grid | Tracing Screen | Word Search |
|-------------|----------------|-------------|
| Choose any letter A-Z | Trace with visual guides | Find hidden words |
| See your progress at a glance | Get instant feedback | 8 fun topics |

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin 2.0** | Primary programming language (Java 11 compatible) |
| **Jetpack Compose** | Modern declarative UI framework |
| **Material Design 3** | UI components and theming |
| **Canvas API** | Custom drawing surface for letter tracing |
| **SharedPreferences** | Persistent progress and game state storage |
| **JSON (org.json)** | Game state serialization |

---

## Project Structure

```
app/src/main/java/com/example/alphabettracer/
├── MainActivity.kt                    # App entry point
├── data/                              # Data persistence layer
│   ├── AlphabetList.kt                # 26 letters with associated words
│   ├── LetterStorage.kt               # Progress persistence (SharedPreferences)
│   ├── AchievementStorage.kt          # Achievement tracking
│   ├── WordSearchData.kt              # 8 word search topics (67+ words)
│   └── WordSearchStorage.kt           # Game state persistence
├── model/                             # Data models
│   ├── AlphabetData.kt                # Letter + word + sentence data class
│   ├── DrawStroke.kt                  # User drawn stroke with color/width
│   ├── MatchResult.kt                 # Enum: NONE, POOR, GOOD, EXCELLENT
│   ├── ScreenState.kt                 # App navigation states
│   ├── Achievement.kt                 # Achievement definitions
│   └── WordSearchModels.kt            # Word search game models
└── ui/
    ├── components/                    # Reusable UI components
    │   ├── TracingCanvas.kt           # Main drawing canvas with detection
    │   ├── LetterGridItem.kt          # Single letter card component
    │   ├── AchievementBadge.kt        # Achievement display badge
    │   ├── AchievementPopup.kt        # Unlock celebration dialog
    │   └── ConfettiAnimation.kt       # Confetti celebration effect
    ├── screens/                       # Full-screen composables
    │   ├── AlphabetTracingApp.kt      # Main navigation container
    │   ├── LetterGridScreen.kt        # A-Z grid with progress
    │   ├── TracingScreen.kt           # Letter tracing interface
    │   ├── WordSearchTopicScreen.kt   # Topic selection screen
    │   └── WordSearchGameScreen.kt    # Word search puzzle game
    └── theme/                         # UI theming
        ├── Color.kt                   # Material3 + kid-friendly colors
        ├── Theme.kt                   # Dark/light theme configuration
        └── Type.kt                    # Typography settings
```

### Key Components

| Component | Description |
|-----------|-------------|
| `AlphabetTracingApp` | Main app scaffold with state-based navigation |
| `LetterGridScreen` | A-Z grid with color-coded progress indicators |
| `TracingScreen` | Letter info, drawing canvas, and tool controls |
| `TracingCanvas` | Drawing surface with gesture detection and matching |
| `WordSearchGameScreen` | Interactive word search puzzle with drag selection |
| `LetterStorage` | SharedPreferences helper for letter progress |
| `AchievementStorage` | Tracks unlocked achievements and streaks |
| `WordSearchStorage` | Saves/loads game state as JSON |

---

## How the Detection Works

1. **Letter Path Generation**: Each letter has 40-70+ interpolated points defining its shape using math-based curves and lines
2. **User Drawing Capture**: Touch gestures are captured as coordinate points with color and stroke width
3. **Coverage Analysis**: Checks how much of the reference path was traced (minimum 40% required)
4. **Accuracy Analysis**: Checks how much of the user's drawing is on the path (minimum 30% required)
5. **Score Calculation**: `score = (coverage × 50%) + (accuracy × 50%)`
6. **Result Mapping**:
   - `> 70%` → EXCELLENT
   - `> 55%` → GOOD
   - `> 35%` → POOR
   - `≤ 35%` → NONE

---

## Navigation Structure

The app uses state-based navigation with four screens:

```
LETTER_GRID ──────► TRACING
     │                 │
     ▼                 ▼
WORD_SEARCH_TOPICS ► WORD_SEARCH_GAME
```

---

## Requirements

- **Android Studio**: Hedgehog or newer
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 36 (Android 15)
- **Compile SDK**: 36
- **Kotlin**: 2.0.21+

---

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/yourusername/HaateKhori.git
cd HaateKhori
```

### Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Connect an Android device or start an emulator (Android 12+)
4. Click **Run** or press `Shift + F10`

### Build APK
```bash
./gradlew assembleDebug
```
APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

---

## Alphabet Data

All 26 letters are included with associated words:

| Letter | Word | Letter | Word |
|--------|------|--------|------|
| A | Apple | N | Nest |
| B | Ball | O | Orange |
| C | Cat | P | Penguin |
| D | Dog | Q | Queen |
| E | Elephant | R | Rainbow |
| F | Fish | S | Sun |
| G | Giraffe | T | Tiger |
| H | House | U | Umbrella |
| I | Ice cream | V | Violin |
| J | Jelly | W | Whale |
| K | Kite | X | Xylophone |
| L | Lion | Y | Yak |
| M | Monkey | Z | Zebra |

---

## Data Persistence

The app uses SharedPreferences for persistent storage:

| Preference File | Data Stored |
|-----------------|-------------|
| `haate_khori_prefs` | Letter results, color preferences, stroke width |
| `haate_khori_achievements` | Unlocked achievements, max streak, colors used |
| `word_search_prefs` | Game states (JSON serialized) |

Progress is never downgraded - only improvements are saved.

---

## Future Enhancements

- [ ] Lowercase letters support
- [ ] Number tracing (0-9)
- [ ] Sound effects and audio feedback
- [ ] Multiple language support (Bengali, Hindi, etc.)
- [ ] Parent/Teacher dashboard
- [ ] Custom word lists
- [ ] More word search topics

---

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## License

This project is open source and available under the [MIT License](LICENSE).

---

## Acknowledgments

- Built with Jetpack Compose and Material Design 3
- Inspired by traditional "হাতে খড়ি" (first writing lesson) concept
- Designed for young learners starting their writing journey

---

<p align="center">
  Made with love for kids learning to write
</p>
