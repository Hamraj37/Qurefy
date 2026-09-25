# Qurafy 📖 — Modern Android Quran Reader

[![Android Version](https://img.shields.io/badge/Android-API%2030%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)

An elegant, modern Android application for reading and studying the Holy Quran with Roman Urdu and Hindi translation support, high-definition PDF rendering, interactive text annotation tools, and glassmorphism UI styling.

---

## ✨ Features

- **📖 Authentic PDF Quran Reader**: Asynchronously renders high-resolution pages of `quran-roman-urdu-hindi.pdf` using Android's native `PdfRenderer` API backed by an in-memory `LruCache` for smooth, zero-lag page transitions.
- **✏️ Interactive Text Annotation & Marking**:
  - Tap the header Pencil tool to draw, mark, or highlight text directly on PDF pages.
  - **Highlighter Mode**: Translucent gold highlight stroke for marking ayats.
  - **Color Pencils**: Red, Green, Blue, and Black stroke colors.
  - **Undo & Clear**: Easily undo recent lines or clear all page annotations.
  - **Per-Page Persistence**: Annotations are automatically saved per page across app restarts.
- **🔍 Two-Finger Pinch Zoom & Pan**:
  - Smooth 2-finger pinch gesture scaling from 1x to 3.5x.
  - Double-tap zoom toggle (1x / 2x).
  - Accurate touch coordinate mapping when drawing on zoomed PDF pages.
  - Ununimpeded single-finger horizontal swiping at 1x scale.
- **🌙 Night Mode / Dark Theme**: Inverts page bitmap colors dynamically for comfortable night reading without straining eyes.
- **🏠 Home Page Dashboard**:
  - **Continue Reading Card**: Shows last read page number, progress percentage, and time-based Islamic greetings (*"Assalamu Alaikum • Good Morning 🌅"*).
  - **Saved Bookmarks Manager**: One-tap navigation to all saved bookmarks with quick delete options.
- **🔖 Bookmark & Jump System**: Save any page with a single tap and jump to any page (1 to 604) using the scrollable jump dialog.
- **💎 Liquid Glass UI**: Glassmorphism translucent floating overlays with glossy gradient borders and elevation depth.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Idiomatic Kotlin
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow
- **Asynchronous Execution**: Kotlin Coroutines & `StateFlow`
- **PDF Rendering**: Native Android `android.graphics.pdf.PdfRenderer`
- **Persistence**: Jetpack DataStore Preferences with Moshi JSON serialization
- **Audio Engine**: AndroidX Media3 ExoPlayer & `MediaSessionService`

---

## 📁 Project Structure

```
Qurafy/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/
│   │   │   │   └── quran-roman-urdu-hindi.pdf  # Main Quran PDF Asset (Git LFS)
│   │   │   ├── java/com/qurafy/hamraj37/
│   │   │   │   ├── data/                      # Repositories, Datasets & Models
│   │   │   │   ├── pdf/                        # PdfRenderer Manager & Caching
│   │   │   │   ├── service/                    # Media3 Audio Service
│   │   │   │   └── ui/                         # Jetpack Compose Screens & Theme
│   │   │   └── res/                            # Drawables, Adaptive Icons & Strings
│   │   └── test/                               # Unit Test Suites
│   └── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1+) or newer
- **JDK**: 17 or higher
- **Android SDK**: Min SDK 30 (Android 11) | Target SDK 36

### Building from Source

1. **Clone the repository** (ensure [Git LFS](https://git-lfs.com) is installed):
   ```bash
   git clone https://github.com/Hamraj37/Qurefy.git
   cd Qurefy
   git lfs pull
   ```

2. **Build the Debug APK**:
   ```bash
   ./gradlew app:assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew app:testDebugUnitTest
   ```

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
