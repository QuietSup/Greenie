package com.example.greenie.components.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.greenie.R
import com.example.greenie.components.helper.clock.Clock
import com.example.greenie.data.viewmodels.MainViewModel
import com.example.greenie.domain.Classification
import com.example.greenie.domain.Classifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    val hasPermission = cameraPermissionState.status.isGranted
    val onRequestPermission = cameraPermissionState::launchPermissionRequest

    if (hasPermission) {
        Camera(mainViewModel, scope, snackbarHostState)
    } else {
        NoPermissionScreen(onRequestPermission)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Camera(
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    val (photo, updatePhoto) = remember { mutableStateOf<Bitmap?>(null) }
    var (showSheet, setShowSheet) = remember { mutableStateOf(false) }
    var (classifications, setClassifications) = remember {
        mutableStateOf(emptyList<Classification>())
    }
    var (showTimePicker, setShowTimePicker) = remember { mutableStateOf(false) }

    var classification by remember {
        mutableStateOf<Classification?>(null)
    }

    val classifier = remember {
        Classifier(
            context,
            onClassify = {
                setClassifications(it)
            }
        )
    }

    fun helper(x: Bitmap) {
        try {
            println("helper started")
//            updatePhoto(x)
//            classifier.classify(x).forEach {
////                val classification = classifications[0]
////                mainViewModel.getDiseaseByTfcode(classification.diseaseCode).observeForever {
////                mainViewModel.getDiseaseByTfcode(intArrayOf(32, 5, 25, 9, 26, 13).random()).observeForever {
//                mainViewModel.getDiseaseByTfcode(5).observeForever {
//                    setDisease(it)
//                    mainViewModel.getPlantById(it.plantId).observeForever {
//                        setPlant(it)
//                    }
//                }
//            }
            updatePhoto(x)
            println("photo updated")
            println("classifier $classifier")
            val cl = classifier.classify(x)
            println(cl)
            cl.forEach {
                classification = it
                println("made one classification")
            }
            if (classification == null) {
                classification = Classification(diseaseCode = 27)
                classifications = listOf(Classification(diseaseCode = 27))
            }

//            if (cl.isEmpty()) {
//                scope.launch {
//                    snackbarHostState.showSnackbar(
//                        "Couldn't make diagnosis. Please relaunch the camera",
//                        withDismissAction = true,
//                        duration = SnackbarDuration.Long
//                    )
//                }
//            }
            setShowSheet(true)

            println("classifications complete: ${classification}")

        } catch (e: Exception) {
            e.printStackTrace()
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Couldn't make diagnosis. Please relaunch the camera",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    Scaffold(
        Modifier
            .fillMaxSize(),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            IconButton(
                onClick = {
                    println("start")
                    capturePhoto(
                        context,
                        cameraController
                    ) {
                        x -> helper(x)
                    }
                }) {
                Icon(
                    modifier = Modifier.size(180.dp),
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Camera capture icon"
                )
            }
        }
    ) { paddingValues: PaddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(android.graphics.Color.BLACK)
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )


            if (classification != null) {
//                val disease = mainViewModel.getDiseaseByTfcode(5).observeAsState(null)
                val disease = mainViewModel.getDiseaseByTfcode(classification!!.diseaseCode).observeAsState(null)
                if (disease.value != null) {
                    val plant = mainViewModel.getPlantById(disease.value!!.plantId).observeAsState(null)
                    if (plant.value != null) {
                        if (showTimePicker && plant.value != null && disease.value != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                            ) {
                                Clock(
                                    setShowTimePicker = setShowTimePicker,
                                    plant.value!!,
                                    disease.value!!.reminderTooltip,
                                    mainViewModel,
                                    interval = disease.value?.reminderInterval ?: 1
                                )
                            }
                        }

                        Column {
                            if (photo != null)
                                PhotoPreview(
                                    modifier = Modifier
                                        .size(175.dp)
                                        .padding(16.dp),
                                    capturedPhoto = photo,
                                    onClick = { setShowSheet(true) }
                                )

                            val sheetState = rememberModalBottomSheetState()
                            if (showSheet) {
                                BottomSheet(
                                    sheetState = sheetState,
                                    setShowSheet = setShowSheet,
                                    classifications,
                                    setShowTimePicker,
                                    mainViewModel,
                                    photo,
                                    scope
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit,
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val correctedBitmap: Bitmap = image
                .toBitmap()
                .rotateBitmap(image.imageInfo.rotationDegrees)
            onPhotoCaptured(correctedBitmap)
            println("Success")
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraContent", "Error capturing image", exception)
        }
    })
}

fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(-rotationDegrees.toFloat())
        postScale(-1f, -1f)
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@Composable
fun NoPermissionScreen(onRequestPermission: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = onRequestPermission,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Camera")
                Text(text = "Camera permission", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
