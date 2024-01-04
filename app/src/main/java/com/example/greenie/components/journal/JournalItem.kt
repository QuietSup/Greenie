package com.example.greenie.components.journal

import android.icu.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.greenie.data.models.Diagnosis
import com.example.greenie.data.models.Plant
import com.example.greenie.data.models.Record
import com.example.greenie.data.models.TextRecord
import com.example.greenie.data.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalItem(
    navController: NavHostController,
    plantId: Int,
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var (text, setText) = rememberSaveable { mutableStateOf("") }

    val plant by mainViewModel.getPlantById(plantId).observeAsState(null)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(scrollBehavior, navController, plant)
        },
        bottomBar = {
            TextField(
                value = text,
                onValueChange = { x: String -> setText(x) },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text("take a note") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (plant != null && text.trim().isNotEmpty()) {
                            println("Insert Text Record")
                            mainViewModel.insertTextRecord(text, plantId)
                            mainViewModel.updatePlant(
                                id = plant!!.id,
                                name = plant!!.name,
                                photoFolder = plant!!.photoFolder
                                )
                            setText("")
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Send, contentDescription ="")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (plant != null) {
            ItemContent(innerPadding, mainViewModel, plantId)
        }
        else {
            Text(text = "Sorry, no plant found", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun MessageRecord(textRecord: TextRecord) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = textRecord.createdAt
    val yy = calendar.get(Calendar.YEAR)
    val mm = calendar.get(Calendar.MONTH)
    val dd = calendar.get(Calendar.DAY_OF_MONTH)
    val h = calendar.get(Calendar.HOUR_OF_DAY)
    val m = calendar.get(Calendar.MINUTE)
    val min =
        if (m < 10) "0$m"
        else m.toString()

    val time = "$dd/$mm/$yy $h:$min"

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = textRecord.text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.Start)
        )

        Text(
            text = time,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(Alignment.End)
                .alpha(0.5f)
        )
    }
}

@Composable
fun ItemContent(innerPadding: PaddingValues, mainViewModel: MainViewModel, plantId: Int) {

    var records by remember { mutableStateOf(emptyList<Record>()) }

    val textRecords by mainViewModel.getTextRecordsByPlantId(plantId).observeAsState(emptyList())
    val diagnoses by mainViewModel.getDiagnosesByPlantId(plantId).observeAsState(emptyList())

    LaunchedEffect(key1 = textRecords, key2 = diagnoses) {
        records = diagnoses + textRecords
        records = records.sortedBy { it.createdAt }
    }

    if (records.isNotEmpty()) {
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = records.lastIndex
        )
        LaunchedEffect(key1 = records) {
            listState.scrollToItem(records.lastIndex)
        }

        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            records.forEach {
                when (it) {
                    is TextRecord ->
                        item {
                            MessageRecord(textRecord = it)
                        }

                    is Diagnosis ->
                        item {
                            RecordDisease(it, mainViewModel)
                        }
                }
            }
            if (records.isEmpty()) {
                item {
                    Text(
                        text = "Nothing here yet",
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(scrollBehavior: TopAppBarScrollBehavior, navController: NavHostController, plant: Plant?) {
    MediumTopAppBar(
        title = {
            Text(
                plant?.name ?: "Medium Top App Bar",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate("journal") {
                    navController.popBackStack("journal", false)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDisease(diagnosis: Diagnosis, mainViewModel: MainViewModel) {
    var (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val photo = File(LocalContext.current.filesDir, "${diagnosis.id}.png")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = diagnosis.createdAt
    val yy = calendar.get(Calendar.YEAR)
    val mm = calendar.get(Calendar.MONTH)
    val dd = calendar.get(Calendar.DAY_OF_MONTH)
    val h = calendar.get(Calendar.HOUR_OF_DAY)
    val m = calendar.get(Calendar.MINUTE)
    val min =
        if (m < 10) "0$m"
        else m.toString()

    val time = "$dd/$mm/$yy $h:$min"
    val disease by mainViewModel.getDiseaseById(diagnosis.diseaseId).observeAsState(null)


    Card(
        onClick = {
            setShowDialog(true)
        },
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            if (photo.exists()) {
                Image(
                    rememberAsyncImagePainter(model = photo),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0F to Color.Transparent,
                            .5f to Color.Black.copy(alpha = 0.2F),
                            1F to Color.Black.copy(alpha = 0.7F)
                        )
                    )
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = disease?.name ?: "Disease",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Text(
                    text = time,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                )
            }

        }
    }
    if (showDialog)
        PhotoPreviewDialog(setShowDialog, photo)
}

@Composable
fun PhotoPreviewDialog(setShowDialog: (Boolean) -> Unit, photo: File) {
    Dialog(
        onDismissRequest = { setShowDialog(false) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                    .clickable { setShowDialog(false) }
            ) {
                IconButton(
                    onClick = { setShowDialog(false) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = null)
                }
            }
            Image(
                rememberAsyncImagePainter(model = photo),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .clickable(enabled = false) { }
            )
        }
    }
}