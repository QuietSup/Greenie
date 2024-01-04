package com.example.greenie.components.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.greenie.R
import com.example.greenie.alarm.AlarmScheduler
import com.example.greenie.data.models.Alarm
import com.example.greenie.data.models.Diagnosis
import com.example.greenie.data.viewmodels.MainViewModel
import com.example.greenie.domain.Classification
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import kotlin.math.absoluteValue


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BottomSheet(
    sheetState: SheetState,
    setShowSheet: (Boolean) -> Unit,
    classifications: List<Classification>,
    setShowTimePicker: (Boolean) -> Unit,
    mainViewModel: MainViewModel,
    photo: Bitmap?,
    scope: CoroutineScope
)
{

    val notificationsPermissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val hasPermission = notificationsPermissionState.status.isGranted
    val onRequestPermission = notificationsPermissionState::launchPermissionRequest

    val snackState = remember { SnackbarHostState() }

    val scheduler = AlarmScheduler(LocalContext.current)

    var isSaved by remember {
        mutableStateOf(false)
    }

    var (diagnosisId, setDiagnosisId) = remember {
        mutableStateOf<Long?>(null)
    }

//    LaunchedEffect(key1 = diagnosisId) {
//        if (diagnosisId != null) {
////            mainViewModel.getDiagnosisById(diagnosisId).observeForever {
////                diagnosis = it
////            }
//        }
//    }

    SnackbarHost(hostState = snackState, Modifier)

        ModalBottomSheet(
            onDismissRequest = {
                setShowSheet(false)
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                classifications.forEach { classification ->

                    println("Classified tfcode: ${classification.diseaseCode}")
////                    mainViewModel.getDiseaseByTfcode(classification.diseaseCode)
////                    mainViewModel.getDiseaseByTfcode(intArrayOf(32, 5, 25, 9, 26, 13).random())
////                    mainViewModel.getDiseaseByTfcode(classification.diseaseCode)

                    val disease by mainViewModel.getDiseaseByTfcode(classification.diseaseCode).observeAsState(null)
                    if (disease != null) {

                        val alarm by mainViewModel.getAlarmByPlantId(disease!!.plantId)
                            .observeAsState(null)
                        val plant by mainViewModel.getPlantById(disease!!.plantId)
                            .observeAsState(null)
                        val treatments by mainViewModel.getTreatmentsByDiseaseId(disease!!.id)
                            .observeAsState(
                                emptyList()
                            )

                        if (plant != null) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = disease?.name ?: "Not found",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val context = LocalContext.current
                                    var diagnosis: Diagnosis? by remember {
                                        mutableStateOf(null)
                                    }
                                    if (diagnosisId != null) {
                                        mainViewModel.getDiagnosisById(diagnosisId)
                                            .observeForever {
                                                diagnosis = it
                                            }
                                    }

                                    IconButton(
                                        onClick = {
                                            if (photo != null) {
                                                if (!isSaved) {
                                                    try {
                                                        mainViewModel.insertDiagnosis(
                                                            disease!!.id,
                                                            photo,
                                                            context,
                                                            setDiagnosisId
                                                        )
                                                        if (plant != null) {
                                                            mainViewModel.updatePlant(
                                                                id = plant!!.id,
                                                                name = plant!!.name,
                                                                photoFolder = plant!!.photoFolder,
                                                            )
                                                        }
                                                        println("Plant $plant")

                                                        scope.launch {
                                                            snackState.showSnackbar(
                                                                "Photo saved to your journal",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                        isSaved = true
                                                    } catch (_: Exception) {
                                                        scope.launch {
                                                            snackState.showSnackbar(
                                                                "Error occurred. Photo wasn't saved",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    try {
                                                        if (diagnosis != null) {
                                                            mainViewModel.deleteDiagnosis(
                                                                diagnosis!!,
                                                                context
                                                            )
                                                            isSaved = false
                                                            scope.launch {
                                                                snackState.showSnackbar(
                                                                    "Photo deleted from your journal",
                                                                    withDismissAction = true,
                                                                    duration = SnackbarDuration.Short
                                                                )
                                                            }
                                                        } else {
                                                            scope.launch {
                                                                snackState.showSnackbar(
                                                                    "Error occurred. Photo wasn't deleted",
                                                                    withDismissAction = true,
                                                                    duration = SnackbarDuration.Short
                                                                )
                                                            }
                                                        }
                                                    } catch (_: Exception) {
                                                        scope.launch {
                                                            snackState.showSnackbar(
                                                                "Error occurred. Photo wasn't deleted",
                                                                withDismissAction = true,
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter =
                                            if (isSaved)
                                                painterResource(id = R.drawable.bookmark_added)
                                            else
                                                painterResource(id = R.drawable.bookmark),
                                            contentDescription = ""
                                        )
                                    }
                                }
                            }
                            Divider()

                            if (treatments.isNotEmpty()) {
                                val context = LocalContext.current
                                var imgs = context.assets.list("diseases/${disease?.id}")?.toList()
                                var (showDialog, setShowDialog) = remember {
                                    mutableStateOf(false)
                                }
                                var index by remember {
                                    mutableIntStateOf(0)
                                }

                                if (showDialog && imgs != null) {
                                    PhotoPreviewDialog(
                                        setShowDialog = setShowDialog,
                                        imgs = imgs,
                                        index = index,
                                        imgsPath = "file:///android_asset/diseases/${disease?.id}/"
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                ) {

                                    imgs?.forEachIndexed { i, it ->
                                        AsyncImage(
                                            model = "file:///android_asset/diseases/${disease?.id}/$it",
                                            modifier = Modifier
                                                .size(120.dp, 160.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    index = i
                                                    setShowDialog(true)
                                                },
                                            contentDescription = "",

                                            contentScale = ContentScale.Crop,
                                        )
                                    }

                                }

                                ListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    headlineContent = { Text(text = "every ${disease?.reminderInterval} days") },
                                    leadingContent = {
                                        Icon(
                                            imageVector = Icons.Outlined.DateRange,
                                            contentDescription = ""
                                        )
                                    },
                                    supportingContent = {
                                        Text(text = disease?.reminderTooltip.toString())
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                if (hasPermission) {
                                                    if (alarm != null && alarm!!.isActive == 1 && alarm!!.tooltip == disease?.reminderTooltip) {
                                                        scheduler.cancel(alarm!!.plantId)
                                                        mainViewModel.updateAlarm(
                                                            Alarm(
                                                                hours = alarm!!.hours,
                                                                minutes = alarm!!.minutes,
                                                                plantId = alarm!!.plantId,
                                                                title = alarm!!.title,
                                                                tooltip = alarm!!.tooltip,
                                                                isActive = 0,
                                                                interval = alarm!!.interval
                                                            )
                                                        )
                                                    } else {
                                                        setShowTimePicker(true)
                                                    }
                                                } else {
                                                    onRequestPermission()
                                                }
                                                println("disease")
                                                println(disease)
                                            }
                                        ) {
                                            Icon(
                                                imageVector =
                                                if (alarm != null && alarm!!.isActive == 1 && alarm!!.tooltip == disease!!.reminderTooltip)
                                                    Icons.Filled.Notifications
                                                else
                                                    Icons.Outlined.Notifications,
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                )

                                ListItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    headlineContent = { Text(text = "Treatment") },
                                    supportingContent = {
                                        Column {
                                            treatments.forEach {
                                                Text(text = it.text)
                                            }
                                        }
                                    },
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.happy_cat),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .align(Alignment.Center)
                                            .padding(top=40.dp)
                                    )
                                }
                            }

                        } else {
                            Text(text = "No disease found")
                        }
                    }
                }
            }
        }

}

@Throws(IOException::class)
fun saveBitmap(
    context: Context,
    bitmap: Bitmap,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    mimeType: String = "image/jpeg",
    displayName: String,
    directory: String = "dir"
): Uri {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + directory)
    }

    val resolver = context.contentResolver
    var uri: Uri? = null

    try {
        uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("Failed to create new MediaStore record.")

        resolver.openOutputStream(uri)?.use {
            if (!bitmap.compress(format, 95, it))
                throw IOException("Failed to save bitmap.")
        } ?: throw IOException("Failed to open output stream.")

        return uri

    } catch (e: IOException) {

        uri?.let { orphanUri ->
            resolver.delete(orphanUri, null, null)
        }
        throw e
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoPreviewDialog(setShowDialog: (Boolean) -> Unit, imgs: List<String>, index: Int, imgsPath: String) {
//    "file:///android_asset/diseases/${disease?.id}/$img"
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
            val pagerState = rememberPagerState(
                pageCount = { imgs.size },
                initialPage = index
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .clickable(enabled = false) { },
                pageSpacing = 12.dp
            ) {
                AsyncImage(
                    model = imgsPath + imgs[it],
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) { }
                        .graphicsLayer {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = (
                                    (pagerState.currentPage - it) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                )
            }
//            AsyncImage(
//                model = photoPath,
//                contentDescription = "",
//                contentScale = ContentScale.Fit,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.Center)
//                    .clickable(enabled = false) { }
//            )
        }
    }
}