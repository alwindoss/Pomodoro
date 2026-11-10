package `in`.dalw.pomodoro.data

import android.content.Context
import android.content.SharedPreferences

interface PomodoroPreferences {
    var workDurationMinutes: Int
    var shortBreakDurationMinutes: Int
    var longBreakDurationMinutes: Int
}

class PreferencesManager(context: Context) : PomodoroPreferences {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pomodoro_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_WORK_DURATION = "work_duration_minutes"
        const val KEY_SHORT_BREAK_DURATION = "short_break_duration_minutes"
        const val KEY_LONG_BREAK_DURATION = "long_break_duration_minutes"

        const val DEFAULT_WORK_MINUTES = 25
        const val DEFAULT_SHORT_BREAK_MINUTES = 5
        const val DEFAULT_LONG_BREAK_MINUTES = 15
    }

    override var workDurationMinutes: Int
        get() = prefs.getInt(KEY_WORK_DURATION, DEFAULT_WORK_MINUTES)
        set(value) = prefs.edit().putInt(KEY_WORK_DURATION, value).apply()

    override var shortBreakDurationMinutes: Int
        get() = prefs.getInt(KEY_SHORT_BREAK_DURATION, DEFAULT_SHORT_BREAK_MINUTES)
        set(value) = prefs.edit().putInt(KEY_SHORT_BREAK_DURATION, value).apply()

    override var longBreakDurationMinutes: Int
        get() = prefs.getInt(KEY_LONG_BREAK_DURATION, DEFAULT_LONG_BREAK_MINUTES)
        set(value) = prefs.edit().putInt(KEY_LONG_BREAK_DURATION, value).apply()
}
