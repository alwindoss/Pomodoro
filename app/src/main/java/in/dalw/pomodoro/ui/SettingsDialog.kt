package `in`.dalw.pomodoro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SettingsDialog(
    workDurationMinutes: Int,
    shortBreakDurationMinutes: Int,
    longBreakDurationMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (work: Int, shortBreak: Int, longBreak: Int) -> Unit
) {
    var workInput by remember { mutableStateOf(workDurationMinutes.toString()) }
    var shortBreakInput by remember { mutableStateOf(shortBreakDurationMinutes.toString()) }
    var longBreakInput by remember { mutableStateOf(longBreakDurationMinutes.toString()) }

    var workError by remember { mutableStateOf(false) }
    var shortBreakError by remember { mutableStateOf(false) }
    var longBreakError by remember { mutableStateOf(false) }

    fun validateAndSave() {
        val workVal = workInput.toIntOrNull()
        val shortVal = shortBreakInput.toIntOrNull()
        val longVal = longBreakInput.toIntOrNull()

        val isWorkValid = workVal != null && workVal > 0 && workVal <= 180
        val isShortValid = shortVal != null && shortVal > 0 && shortVal <= 180
        val isLongValid = longVal != null && longVal > 0 && longVal <= 180

        workError = !isWorkValid
        shortBreakError = !isShortValid
        longBreakError = !isLongValid

        if (workVal != null && shortVal != null && longVal != null && isWorkValid && isShortValid && isLongValid) {
            onSave(workVal, shortVal, longVal)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Timer Settings") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = workInput,
                    onValueChange = {
                        workInput = it
                        workError = false
                    },
                    label = { Text("Pomodoro Duration (minutes)") },
                    isError = workError,
                    supportingText = if (workError) {
                        { Text("Enter a duration between 1 and 180 minutes") }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = shortBreakInput,
                    onValueChange = {
                        shortBreakInput = it
                        shortBreakError = false
                    },
                    label = { Text("Short Break Duration (minutes)") },
                    isError = shortBreakError,
                    supportingText = if (shortBreakError) {
                        { Text("Enter a duration between 1 and 180 minutes") }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = longBreakInput,
                    onValueChange = {
                        longBreakInput = it
                        longBreakError = false
                    },
                    label = { Text("Long Break Duration (minutes)") },
                    isError = longBreakError,
                    supportingText = if (longBreakError) {
                        { Text("Enter a duration between 1 and 180 minutes") }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { validateAndSave() }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
