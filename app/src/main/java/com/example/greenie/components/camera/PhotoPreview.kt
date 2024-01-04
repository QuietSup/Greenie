package com.example.greenie.components.camera

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPreview(
    modifier: Modifier,
    capturedPhoto: Bitmap,
    onClick: () -> Unit
) {
    val capturedPhoto: ImageBitmap = remember(capturedPhoto.hashCode()) { capturedPhoto.asImageBitmap() }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
        border = BorderStroke(
            2.dp, Color.White
        )
    ) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }
}