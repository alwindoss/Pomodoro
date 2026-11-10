# Pomodoro Timer

A modern Android Pomodoro Timer application built with Kotlin, Jetpack Compose, and Material 3 design.

---

## 🚀 Features

- **Timer Modes**:
  - **Pomodoro (Focus)**: Work session (default 25 minutes).
  - **Short Break**: Quick pause (default 5 minutes).
  - **Long Break**: Extended break (default 15 minutes).
- **Controls**:
  - **Start / Pause**: Toggles the countdown timer with smooth visual circular progress.
  - **Reset**: Resets the timer to the full duration of the active mode.
- **Customizable Durations**:
  - Configure duration for Focus, Short Break, and Long Break via the **Settings** dialog.
  - Custom settings persist across app sessions using `SharedPreferences`.
- **Audio Completion Alert**:
  - Plays a notification tone when a session finishes.
- **Session Stats**:
  - Tracks completed Pomodoro focus sessions.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (ViewModel + StateFlow + UI State)
- **Asynchronous Execution**: Kotlin Coroutines (`viewModelScope`)
- **Data Persistence**: `SharedPreferences`
- **Testing**: JUnit 4 & `kotlinx-coroutines-test`

---

## 📁 Project Structure

```
app/src/
├── main/
│   ├── java/in/dalw/pomodoro/
│   │   ├── MainActivity.kt                  # Activity entry point
│   │   ├── data/
│   │   │   └── PreferencesManager.kt        # SharedPreferences persistence layer
│   │   └── ui/
│   │       ├── TimerMode.kt                 # Enum for timer modes (Pomodoro, Short Break, Long Break)
│   │       ├── PomodoroViewModel.kt         # ViewModel managing state & coroutine countdown
│   │       ├── PomodoroScreen.kt            # Main Compose UI layout & circular timer display
│   │       └── SettingsDialog.kt            # Compose Dialog for editing duration settings
│   └── AndroidManifest.xml
└── test/
    └── java/in/dalw/pomodoro/
        └── PomodoroViewModelTest.kt         # Unit tests for ViewModel logic & settings
```

---

## 🔧 Building & Testing

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17 / JDK 21+
- Android SDK 37 (Min SDK 24)

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```
