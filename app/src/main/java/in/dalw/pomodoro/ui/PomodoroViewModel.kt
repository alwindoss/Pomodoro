package `in`.dalw.pomodoro.ui

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.dalw.pomodoro.data.PreferencesManager
import `in`.dalw.pomodoro.data.PomodoroPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PomodoroUiState(
    val mode: TimerMode = TimerMode.POMODORO,
    val timeRemainingSeconds: Long = 25 * 60L,
    val totalDurationSeconds: Long = 25 * 60L,
    val isRunning: Boolean = false,
    val completedSessions: Int = 0,
    val workDurationMinutes: Int = PreferencesManager.DEFAULT_WORK_MINUTES,
    val shortBreakDurationMinutes: Int = PreferencesManager.DEFAULT_SHORT_BREAK_MINUTES,
    val longBreakDurationMinutes: Int = PreferencesManager.DEFAULT_LONG_BREAK_MINUTES,
    val showSettingsDialog: Boolean = false
) {
    val progress: Float
        get() = if (totalDurationSeconds > 0) {
            timeRemainingSeconds.toFloat() / totalDurationSeconds.toFloat()
        } else {
            0f
        }

    val formattedTime: String
        get() {
            val minutes = timeRemainingSeconds / 60
            val seconds = timeRemainingSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}

class PomodoroViewModel(
    application: Application,
    private val preferencesManager: PomodoroPreferences
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        PreferencesManager(application)
    )

    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val work = preferencesManager.workDurationMinutes
        val shortBreak = preferencesManager.shortBreakDurationMinutes
        val longBreak = preferencesManager.longBreakDurationMinutes

        val initialDurationSeconds = getDurationForMode(TimerMode.POMODORO, work, shortBreak, longBreak)

        _uiState.update {
            it.copy(
                workDurationMinutes = work,
                shortBreakDurationMinutes = shortBreak,
                longBreakDurationMinutes = longBreak,
                totalDurationSeconds = initialDurationSeconds,
                timeRemainingSeconds = initialDurationSeconds
            )
        }
    }

    private fun getDurationForMode(
        mode: TimerMode,
        work: Int = _uiState.value.workDurationMinutes,
        shortBreak: Int = _uiState.value.shortBreakDurationMinutes,
        longBreak: Int = _uiState.value.longBreakDurationMinutes
    ): Long {
        val minutes = when (mode) {
            TimerMode.POMODORO -> work
            TimerMode.SHORT_BREAK -> shortBreak
            TimerMode.LONG_BREAK -> longBreak
        }
        return minutes * 60L
    }

    fun selectMode(mode: TimerMode) {
        if (_uiState.value.mode == mode) return
        pauseTimer()
        val durationSeconds = getDurationForMode(mode)
        _uiState.update {
            it.copy(
                mode = mode,
                totalDurationSeconds = durationSeconds,
                timeRemainingSeconds = durationSeconds,
                isRunning = false
            )
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return
        if (_uiState.value.timeRemainingSeconds <= 0) {
            resetTimer()
        }

        _uiState.update { it.copy(isRunning = true) }

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingSeconds > 0 && _uiState.value.isRunning) {
                delay(1000L)
                val remaining = _uiState.value.timeRemainingSeconds - 1
                if (remaining <= 0) {
                    onTimerFinished()
                    break
                } else {
                    _uiState.update { it.copy(timeRemainingSeconds = remaining) }
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        pauseTimer()
        val durationSeconds = getDurationForMode(_uiState.value.mode)
        _uiState.update {
            it.copy(
                totalDurationSeconds = durationSeconds,
                timeRemainingSeconds = durationSeconds,
                isRunning = false
            )
        }
    }

    private fun onTimerFinished() {
        timerJob?.cancel()
        timerJob = null
        val finishedMode = _uiState.value.mode
        val newCompletedSessions = if (finishedMode == TimerMode.POMODORO) {
            _uiState.value.completedSessions + 1
        } else {
            _uiState.value.completedSessions
        }

        _uiState.update {
            it.copy(
                timeRemainingSeconds = 0,
                isRunning = false,
                completedSessions = newCompletedSessions
            )
        }

        playCompletionSound()
    }

    private fun playCompletionSound() {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 1000)
        } catch (_: Exception) {
            // Ignore sound errors on devices without audio hardware or permissions
        }
    }

    fun openSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun dismissSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun saveSettings(workMin: Int, shortBreakMin: Int, longBreakMin: Int) {
        preferencesManager.workDurationMinutes = workMin
        preferencesManager.shortBreakDurationMinutes = shortBreakMin
        preferencesManager.longBreakDurationMinutes = longBreakMin

        val currentMode = _uiState.value.mode
        val isTimerRunning = _uiState.value.isRunning

        val newModeDuration = when (currentMode) {
            TimerMode.POMODORO -> workMin * 60L
            TimerMode.SHORT_BREAK -> shortBreakMin * 60L
            TimerMode.LONG_BREAK -> longBreakMin * 60L
        }

        _uiState.update {
            it.copy(
                workDurationMinutes = workMin,
                shortBreakDurationMinutes = shortBreakMin,
                longBreakDurationMinutes = longBreakMin,
                showSettingsDialog = false,
                totalDurationSeconds = newModeDuration,
                timeRemainingSeconds = if (!isTimerRunning) newModeDuration else it.timeRemainingSeconds
            )
        }
    }
}
