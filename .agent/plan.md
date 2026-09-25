# Project Plan

A Quran PDF reader Android application (Qurafy) that allows users to read the Holy Quran in PDF format with smooth page navigation, Surah/Juz index, bookmarking, last read position saving, night mode / theme customization, audio recitation playback option, and search/jump to page features.

## Project Brief

# Qurafy - Project Brief

## Features
* **PDF Reader & Navigation**: Smooth PDF Quran rendering with horizontal page swiping, page jump, and indexed navigation by Surah and Juz.
* **Last Read & Bookmarks**: Automatic saving of last read page position and custom bookmarking for favorite pages or verses.
* **Search & Jump to Page**: Quick lookup for Surahs and direct page number entry to jump instantly to any part of the Quran.
* **Audio Recitation**: Built-in audio player supporting background playback of Quran recitations.
* **Night Mode & Themes**: Light and dark/night themes customizable for optimal reading comfort.

## High-Level Tech Stack
* **Language**: Kot[AndroidManifest.xml](../app/src/main/AndroidManifest.xml)lin
* **UI Framework**: Jetpack Compose (Material 3)
* **Navigation**: Jetpack Navigation 3 (state-driven)
* **Adaptive Layouts**: Compose Material Adaptive library
* **Asynchronous Programming**: Kotlin Coroutines & Flow
* **PDF Rendering**: Android `PdfRenderer` API / AndroidX PDF Engine
* **Audio Playback**: Jetpack Media3 (ExoPlayer)
* **Preferences & State Persistence**: Jetpack DataStore

## Implementation Steps

### Task_1_CoreArchitectureAndDataStore: Set up project dependencies (Media3 ExoPlayer, DataStore, Navigation), core data models for Surah/Juz index metadata, and DataStore persistence for last read page, night mode, and bookmarks.
- **Status:** COMPLETED
- **Updates:** Configured dependencies for DataStore, Media3 ExoPlayer, and Material 3. Created Surah and Juz models with complete metadata for 114 Surahs and 30 Juzs. Implemented UserPreferencesRepository for last read page, bookmarks, and night mode settings. Unit tests passed and assembleDebug built successfully.
- **Acceptance Criteria:**
  - Project dependencies configured
  - Surah and Juz index data models created
  - DataStore repository implemented for last read, bookmarks, and theme preference
  - build pass

### Task_2_PdfViewerAndNavigation: Implement Quran PDF reader screen with page swiping, Surah/Juz navigation drawer/bottom sheet, jump to page functionality, auto-saving last read position, and night mode theme toggle.
- **Status:** COMPLETED
- **Updates:** Implemented Quran PDF reader screen with HorizontalPager (604 pages), sample Quran PDF generator and caching PdfRendererManager, Surah (1-114) and Juz (1-30) index bottom sheet with search, jump-to-page dialog, auto-saving last read position, and night mode page filter and theme toggle. Build and unit tests passed.
- **Acceptance Criteria:**
  - Quran PDF pages render and swipe smoothly
  - Surah and Juz index navigation correctly navigates to target pages
  - Jump to page dialog jumps to specified page
  - Last read page position automatically persists
  - Night mode toggles light/dark themes
  - build pass

### Task_3_AudioPlayerAndBookmarks: Implement Media3 ExoPlayer audio playback for Quran recitation, including player controls UI and background service support, alongside bookmarking UI and persistence integration.
- **Status:** COMPLETED
- **Updates:** Implemented Media3 ExoPlayer background playback service (QuranAudioService), reciter selection (5 popular reciters), AudioPlayerBar and AudioPlayerSheet Compose components with full playback controls, speed controls, bookmarking toggle in top bar, and updated Bookmarks tab in index sheet. Build and unit tests passed.
- **Acceptance Criteria:**
  - Audio recitation streams and plays/pauses via Media3 ExoPlayer
  - Audio player UI controls integrated with reading screen
  - Bookmarks can be saved, listed, and jumped to
  - build pass

### Task_4_RunAndVerify: Run and verify application stability, instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Verified application on target Android emulator/device. Build passed, app installed and launched cleanly with zero crashes in logcat. All features verified: 604-page PDF rendering and smooth swiping, Surah and Juz navigation index sheet with search, jump-to-page dialog, bookmarking persistence, night mode theme and color matrix PDF inversion, and Media3 ExoPlayer audio recitation player with reciter selection and background service support.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - App stability and features verified successfully
- **Duration:** N/A

