package com.example.greenie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.greenie.components.camera.CameraScreen
import com.example.greenie.components.guide.Guide
import com.example.greenie.components.journal.Journal
import com.example.greenie.components.journal.JournalItem
import com.example.greenie.components.settings.Settings
import com.example.greenie.data.MainDatabase
import com.example.greenie.data.repositories.AlarmRepository
import com.example.greenie.data.repositories.DiagnosisRepository
import com.example.greenie.data.repositories.DiseasePhotoRepository
import com.example.greenie.data.repositories.DiseaseRepository
import com.example.greenie.data.repositories.PlantRepository
import com.example.greenie.data.repositories.TextRecordRepository
import com.example.greenie.data.repositories.TreatmentRepository
import com.example.greenie.data.viewmodels.MainViewModel
import com.example.greenie.data.viewmodels.ViewModelFactory
import com.example.greenie.ui.theme.GreenieTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by lazy {
        val db = MainDatabase.getDatabase(this)
        ViewModelProvider(
            this,
            ViewModelFactory(
                AlarmRepository(db),
                DiagnosisRepository(db),
                DiseaseRepository(db),
                DiseasePhotoRepository(db),
                PlantRepository(db),
                TextRecordRepository(db),
                TreatmentRepository(db),
            )
        ).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val preferencesDataStore = PreferencesDataStore(LocalContext.current)
            val scope = rememberCoroutineScope()

            var darkModeSystem = preferencesDataStore.darkModeSystem.collectAsState(initial = true)
            var darkMode = preferencesDataStore.darkMode.collectAsState(initial = true)

            GreenieTheme(
                darkModeSystem = darkModeSystem.value,
                darkMode = darkMode.value,
            ) {
                App(mainViewModel, scope, preferencesDataStore)
//                Guide()
            }
        }
    }
}

data class TabInfo(
    val label: String,
    val icon: Painter,
    val selectedIcon: Painter,
    val route: String,
    val route2: String? = null,
)

@Composable
fun App(
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    preferencesDataStore: PreferencesDataStore
) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(1) }
    val firstLaunch by preferencesDataStore.firstLaunch.collectAsState(initial = false)

    val tabs = listOf(
        TabInfo(
            label = "Camera",
            icon = painterResource(id = R.drawable.photo_camera),
            selectedIcon = painterResource(id = R.drawable.photo_camera_filled),
            route = "camera"
        ),
        TabInfo(
            label = "Journal",
            icon = painterResource(id = R.drawable.diagnosis),
            selectedIcon = painterResource(id = R.drawable.diagnosis_filled),
            route = "journal",
            route2 = "all_items"
        ),
        TabInfo(
            label = "Settings",
            icon = painterResource(id = R.drawable.settings),
            selectedIcon = painterResource(id = R.drawable.settings_filled),
            route = "settings"
        ),
    )

    navController.addOnDestinationChangedListener { _, destination, _ ->
        println(destination.route)

        tabs.forEachIndexed { i, t ->
            if (t.route == destination.route || t.route2 == destination.route) {
                println(destination.route)
                selectedItem = i
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    val selected = selectedItem == index
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selected) tab.selectedIcon
                                else tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = selected,
                        onClick = {
                            selectedItem = index
                            navController.navigate(tab.route) {
                                navController.popBackStack(tab.route, true)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (!firstLaunch) "journal" else "guide",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(
                route="camera",
                content = { CameraScreen(mainViewModel = mainViewModel, scope, snackbarHostState) }
            )
            navigation(
                route="journal",
                startDestination = "all_items"
            ) {
                composable(
                    route="all_items",
                    content = { Journal(navController, mainViewModel, scope, snackbarHostState) }
                )
                composable("journal_item/{plantId}") {navBackStackEntry ->
                    val plantId = navBackStackEntry.arguments?.getString("plantId")
                    plantId?.let {
                        JournalItem(navController, plantId.toInt(), mainViewModel, scope, snackbarHostState)
                    }
                }
            }
            composable(
                route="settings",
                content = { Settings(mainViewModel = mainViewModel, scope, snackbarHostState, preferencesDataStore, navController) }
            )
            composable(
                route="guide",
                content = { Guide(navController, preferencesDataStore, scope) }
            )
        }
    }
}
