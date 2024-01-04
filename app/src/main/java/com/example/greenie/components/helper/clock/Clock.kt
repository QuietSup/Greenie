package com.example.greenie.components.helper.clock

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.greenie.alarm.AlarmItem
import com.example.greenie.alarm.AlarmScheduler
import com.example.greenie.data.models.Plant
import com.example.greenie.data.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Clock(
    setShowTimePicker: (Boolean) -> Unit,
    plant: Plant,
    reminderTooltip: String?,
    mainViewModel: MainViewModel,
    interval: Int,
    initialHour: Int = 0,
    initialMinute: Int = 0,
) {
    val state = rememberTimePickerState(
        is24Hour = true,
        initialHour = initialHour,
        initialMinute = initialMinute,
    )
    val formatter = remember { SimpleDateFormat("hh:mm", Locale.getDefault()) }
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    val scheduler = AlarmScheduler(LocalContext.current)
    var alarmItem: AlarmItem? = null

    TimePickerDialog(
        onCancel = { setShowTimePicker(false) },
        onConfirm = {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, state.hour)
            cal.set(Calendar.MINUTE, state.minute)
            cal.isLenient = false
            snackScope.launch {
                snackState.showSnackbar("Entered time: ${formatter.format(cal.time)}")
            }
            println("Picked time ${state.hour}:${state.minute}")
            alarmItem = AlarmItem(
                hours = state.hour,
                minutes = state.minute,
                message = reminderTooltip ?: "Error message",
                channelId = plant.id,
                textTitle = plant.name,
                interval = interval
            )

            alarmItem?.let(scheduler::schedule)

            if (alarmItem != null) {
                mainViewModel.upsertAlarm(
                    hours = alarmItem!!.hours,
                    minutes = alarmItem!!.minutes,
                    plantId = plant.id,
                    title = alarmItem!!.textTitle,
                    tooltip = reminderTooltip ?: "Error tooltip",
                    interval = interval
                )
            }
            setShowTimePicker(false)
        },
    ) {
        TimePicker(state = state)
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}