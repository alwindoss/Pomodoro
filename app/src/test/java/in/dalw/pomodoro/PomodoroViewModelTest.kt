package `in`.dalw.pomodoro

import android.app.Application
import `in`.dalw.pomodoro.data.PomodoroPreferences
import `in`.dalw.pomodoro.ui.PomodoroViewModel
import `in`.dalw.pomodoro.ui.TimerMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakePreferences : PomodoroPreferences {
    override var workDurationMinutes: Int = 25
    override var shortBreakDurationMinutes: Int = 5
    override var longBreakDurationMinutes: Int = 15
}

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakePreferences: FakePreferences
    private lateinit var viewModel: PomodoroViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePreferences = FakePreferences()
        viewModel = PomodoroViewModel(Application(), fakePreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun defaultState_isPomodoroWith25Minutes() {
        val state = viewModel.uiState.value
        assertEquals(TimerMode.POMODORO, state.mode)
        assertEquals(25 * 60L, state.totalDurationSeconds)
        assertEquals(25 * 60L, state.timeRemainingSeconds)
        assertFalse(state.isRunning)
    }

    @Test
    fun selectShortBreak_updatesModeAndDuration() {
        viewModel.selectMode(TimerMode.SHORT_BREAK)
        val state = viewModel.uiState.value
        assertEquals(TimerMode.SHORT_BREAK, state.mode)
        assertEquals(5 * 60L, state.totalDurationSeconds)
        assertEquals(5 * 60L, state.timeRemainingSeconds)
        assertFalse(state.isRunning)
    }

    @Test
    fun selectLongBreak_updatesModeAndDuration() {
        viewModel.selectMode(TimerMode.LONG_BREAK)
        val state = viewModel.uiState.value
        assertEquals(TimerMode.LONG_BREAK, state.mode)
        assertEquals(15 * 60L, state.totalDurationSeconds)
        assertEquals(15 * 60L, state.timeRemainingSeconds)
        assertFalse(state.isRunning)
    }

    @Test
    fun saveSettings_updatesDurationsAndState() {
        viewModel.saveSettings(workMin = 30, shortBreakMin = 10, longBreakMin = 20)
        val state = viewModel.uiState.value

        assertEquals(30, state.workDurationMinutes)
        assertEquals(10, state.shortBreakDurationMinutes)
        assertEquals(20, state.longBreakDurationMinutes)

        // For current mode (Pomodoro), duration should be updated to 30 mins
        assertEquals(30 * 60L, state.totalDurationSeconds)
        assertEquals(30 * 60L, state.timeRemainingSeconds)
    }

    @Test
    fun resetTimer_resetsTimeRemainingToFullDuration() {
        viewModel.startTimer()
        assertTrue(viewModel.uiState.value.isRunning)

        viewModel.resetTimer()
        val state = viewModel.uiState.value
        assertFalse(state.isRunning)
        assertEquals(state.totalDurationSeconds, state.timeRemainingSeconds)
    }
}
