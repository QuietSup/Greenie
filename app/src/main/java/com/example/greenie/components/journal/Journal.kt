package com.example.greenie.components.journal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import com.example.greenie.R
import com.example.greenie.data.models.Plant
import com.example.greenie.data.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import java.io.File


@Composable
fun Journal(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

//    var plants by remember { mutableStateOf(emptyList<Plant>()) }
//    LaunchedEffect(Unit) {
//        mainViewModel.getPlantsSortedByModifiedAt().observeForever {
//            plants = it
//        }
//    }
    val plants: List<Plant> by mainViewModel.getPlantsSortedByModifiedAt().observeAsState(emptyList())

    if (plants.isNotEmpty()) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(140.dp),
            content = {
                items(plants) { plant ->
                    RecordCard(plant = plant, navController, mainViewModel)
                }
            },
            contentPadding = PaddingValues.Absolute(12.dp, 12.dp, 12.dp, 12.dp,),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = true,
        )
    }
    else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(R.drawable.nothing_image, imageLoader),
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Text(
                    text = "No plants found\n\uD83D\uDC48 Save photo to see diagnosis",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordCard(plant: Plant, navController: NavHostController, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    var photo: File? by remember {
        mutableStateOf(null)
    }

//    var diagnosis: Diagnosis? by remember { mutableStateOf(null) }
//    LaunchedEffect(Unit) {
//        mainViewModel.getLastDiagnosisByPlantId(plant.id).observeForever {
//            diagnosis = it
//        }
//    }
    val diagnosis by mainViewModel.getLastDiagnosisByPlantId(plant.id).observeAsState(null)

    LaunchedEffect(key1 = diagnosis) {
        if (diagnosis != null) {
            photo = File(context.filesDir, "${diagnosis!!.id}.png")
        }
    }

    Card(
        onClick = {
            navController.navigate("journal_item/${plant.id}")
        },
        modifier = Modifier.height(200.dp),
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            if (photo != null) {
                Image(
                    rememberAsyncImagePainter(model = photo),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
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

            Text(
                text = plant.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                color = Color.White
            )
        }
    }
}