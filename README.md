# Haate Khori (হাতে খড়ি) - Alphabet Tracer

An interactive Android app designed to help kids learn to write English letters through fun, touch-based tracing with real-time feedback.

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?logo=jetpackcompose)
![Min SDK](https://img.shields.io/badge/Min%20SDK-31-orange)

---

## Features

### Interactive Letter Tracing
- Touch-based drawing canvas for practicing letter shapes
- Visual guide dots showing the correct tracing path
- Faded letter background for reference while drawing

### Smart Detection System
- Real-time analysis of drawn letters
- Checks both **coverage** (did you trace the whole letter?) and **accuracy** (did you stay on the path?)
- Four-tier feedback: Excellent, Good, Poor, or None

### Progress Tracking
- Persistent storage - progress saved across app restarts
- Color-coded letter grid showing mastery level:
  - 🟢 **Green** - Excellent (mastered)
  - 🟡 **Yellow** - Good (almost there)
  - 🔴 **Red** - Poor (needs practice)
  - ⬜ **White** - Not attempted
- Progress bar showing overall completion
- Star counter for excellent tracings

### Kid-Friendly Design
- Large, easy-to-tap letter grid
- Colorful and engaging UI
- Encouraging feedback messages
- Streak system with celebration dialogs

### Drawing Tools
- 6 color options (Blue, Red, Green, Orange, Purple, Black)
- Adjustable stroke width slider
- Pencil/Eraser toggle
- Undo last stroke
- Clear canvas
- Show/Hide guide toggle

---

## Screenshots

| Letter Grid | Tracing Screen |
|-------------|----------------|
| Choose any letter A-Z | Trace with visual guides |
| See your progress at a glance | Get instant feedback |

---

## Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin 2.0** | Primary programming language |
| **Jetpack Compose** | Modern declarative UI framework |
| **Material Design 3** | UI components and theming |
| **SharedPreferences** | Persistent progress storage |
| **Canvas API** | Custom drawing surface |

---

## Project Structure

```
app/src/main/java/com/example/alphabettracer/
├── MainActivity.kt          # Main app logic and all composables
└── ui/theme/
    ├── Color.kt             # Color definitions
    ├── Theme.kt             # App theme configuration
    └── Type.kt              # Typography settings
```

### Key Components

| Component | Description |
|-----------|-------------|
| `AlphabetTracingApp` | Main app scaffold with navigation |
| `LetterGridScreen` | A-Z grid with progress indicators |
| `TracingScreen` | Letter info, canvas, and controls |
| `TracingCanvas` | Drawing surface with gesture detection |
| `LetterStorage` | SharedPreferences helper for persistence |
| `getLetterPath()` | Generates reference points for each letter |
| `checkDrawingMatch()` | Analyzes user drawing against reference |

---

## How the Detection Works

1. **Letter Path Generation**: Each letter has 40-70+ interpolated points defining its shape
2. **User Drawing Capture**: Touch gestures are captured as coordinate points
3. **Coverage Analysis**: Checks how much of the reference path was traced
4. **Accuracy Analysis**: Checks how much of the user's drawing is on the path
5. **Score Calculation**: `score = (coverage × 50%) + (accuracy × 50%)`
6. **Result Mapping**:
   - `> 70%` → EXCELLENT
   - `> 55%` → GOOD
   - `> 35%` → POOR
   - `≤ 35%` → NONE

---

## Requirements

- **Android Studio**: Hedgehog or newer
- **Min SDK**: 31 (Android 12)
- **Target SDK**: 36 (Android 15)
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
3. Connect an Android device or start an emulator
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

## Future Enhancements

- [ ] Lowercase letters support
- [ ] Number tracing (0-9)
- [ ] Sound effects and audio feedback
- [ ] Animated tracing demonstrations
- [ ] Multiple language support (Bengali, Hindi, etc.)
- [ ] Parent/Teacher dashboard
- [ ] Custom word lists

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
  Made with ❤️ for kids learning to write
</p>
