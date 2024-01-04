package com.example.greenie.components.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.greenie.PreferencesDataStore
import com.example.greenie.R
import com.example.greenie.alarm.AlarmScheduler
import com.example.greenie.components.helper.clock.Clock
import com.example.greenie.data.models.Alarm
import com.example.greenie.data.models.Plant
import com.example.greenie.data.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Settings(
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    preferencesDataStore: PreferencesDataStore,
    navController: NavHostController
) {
    Scaffold(
//        topBar = {
//            Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
//        },
        modifier = Modifier
            .padding(PaddingValues(12.dp))

    ) { innerPadding ->

     SettingsList(innerPadding, mainViewModel, scope, preferencesDataStore, snackbarHostState, navController)
    }
}

@Composable
fun SettingsList(
    innerPadding: PaddingValues,
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    preferencesDataStore: PreferencesDataStore,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
) {
    val context = LocalContext.current

    val scheduler = AlarmScheduler(context = context)

    var (showTimePicker, setShowTimePicker) = remember { mutableStateOf(false) }

    var plantAlarm: Plant? by remember {
        mutableStateOf(null)
    }
    var tooltipAlarm: String? by remember {
        mutableStateOf(null)
    }

    var initialHour by remember {
        mutableIntStateOf(0)
    }

    var initialMinute by remember {
        mutableIntStateOf(0)
    }

    var interval by remember {
        mutableIntStateOf(1)
    }

    var darkModeSystem = preferencesDataStore.darkModeSystem.collectAsState(initial = true)
    var darkMode = preferencesDataStore.darkMode.collectAsState(initial = true)

    val alarms by mainViewModel.getAllAlarms().observeAsState(
        emptyList()
    )

    if (showTimePicker && plantAlarm != null && tooltipAlarm != null) {
        Clock(
            setShowTimePicker = setShowTimePicker,
            plantAlarm!!,
            tooltipAlarm,
            mainViewModel,
            interval = interval,
            initialHour = initialHour,
            initialMinute = initialMinute,
        )
    }

    LazyColumn(contentPadding = innerPadding) {
        item {
            SettingsTitle(title = "Help")
            ListItem(
                headlineContent = { Text(text = "How to use Greenie") },
                leadingContent = {
                    Icon(Icons.Outlined.Info,
                    contentDescription = "")
                                 },
                trailingContent = {
                    IconButton(onClick = {
                        navController.navigate("guide") {
                            navController.popBackStack("guide", true)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
        item {
            Divider(Modifier.padding(bottom = 8.dp))
        }
        item {
            SettingsTitle(title = "Theme")
            ListItem(
                headlineContent = { Text(text = "Use System Theme") },
                leadingContent = { Icon(painter = painterResource(id =
                if (darkModeSystem.value)
                    R.drawable.night_sight_auto
                else
                    R.drawable.night_sight_auto_off),
                    contentDescription = "")},
                trailingContent = {
                    Switch(
                        checked = darkModeSystem.value,
                        onCheckedChange = {
                            scope.launch {
                                preferencesDataStore.setDarkModeSystem(it)
                            }
                        }
                    )
                }
            )
            ListItem(
                modifier =
                if (!darkModeSystem.value)
                    Modifier.alpha(1f)
                else
                    Modifier.alpha(0.5f),
                headlineContent = { Text(text = "Dark Mode") },
                leadingContent = { Icon(painter =
                if (darkMode.value)
                    painterResource(id = R.drawable.dark_mode)
                else
                    painterResource(id = R.drawable.light_mode),
                    contentDescription = "")},
                trailingContent = {
                    Switch(
                        checked = darkMode.value,
                        onCheckedChange = {
                            scope.launch {
                                preferencesDataStore.setDarkMode(it)
                            }
                        },
                        enabled = !darkModeSystem.value
                    )
                }
            )
        }

        item{
            Divider()
        }

        items(alarms) {
            val plant by mainViewModel.getPlantById(it.plantId).observeAsState(null)

            ListItem(
                headlineContent = { Text(text = it.title) },
                leadingContent = { Text(text = "${it.hours}:${it.minutes}") },
                supportingContent = { Text(text = it.tooltip) },
                trailingContent = {
                    IconButton(
                        onClick = {
                            if (it.isActive == 1) {
                                try {
                                    it.plantId.let(scheduler::cancel)
                                    mainViewModel.updateAlarm(
                                        Alarm(
                                            hours = it.hours,
                                            minutes = it.minutes,
                                            plantId = it.plantId,
                                            title = it.title,
                                            tooltip = it.tooltip,
                                            isActive = 0,
                                            interval = it.interval
                                        )
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Reminder is set",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } catch (_: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Error occurred. Reminder wasn't set",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            } else {
                                try {
                                    if (plant != null) {
                                        plantAlarm = plant
                                        tooltipAlarm = it.tooltip
                                        initialHour = it.hours
                                        initialMinute = it.minutes
                                        interval = it.interval
                                        setShowTimePicker(true)
                                    }
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Reminder is cancelled",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                } catch (_: Exception) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Error occurred. Reminder wasn't cancelled",
                                            withDismissAction = true,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector =
                            if (it.isActive == 1) {
                                Icons.Filled.Notifications
                            } else {
                                Icons.Outlined.Notifications
                            },
                            contentDescription = ""
                        )
                    }
                }
            )
        }

    }
}

@Composable
fun SettingsTitle(title: String){
    Text(text = title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.alpha(0.7f), fontWeight = FontWeight.Bold)
}