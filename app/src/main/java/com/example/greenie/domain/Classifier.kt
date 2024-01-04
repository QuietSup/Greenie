package com.example.greenie.domain

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class Classifier(
    private val context: Context,
    private val maxResults: Int = 1,
    private val onClassify: (List<Classification>) -> Unit
) {
    private var classifier: ImageClassifier? = null

    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder().
                setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(0.5f)
            .build()

        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "1_metadata.tflite",
                options
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun classify(bitmap: Bitmap): List<Classification> {
        println("classify")

        if (classifier == null) {
            setupClassifier()
        }
        println("classify")
        println("classifier in fun: $classifier")
        val croppedBitmap = bitmap.centerCrop(224, 224)
        val imageProcessor = ImageProcessor.Builder().build()
//        val tensorImage = imageProcessor.process((TensorImage.fromBitmap(croppedBitmap)))
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(croppedBitmap))
        val imageProcessingOptions = ImageProcessingOptions.builder().build()

        val results = classifier?.classify(tensorImage, imageProcessingOptions)
//        println(results)
        val output = results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    diseaseCode = category.index,
                )
            }
//        }?.distinctBy { it.diseaseCode } ?: emptyList()
        }?.distinctBy { it.diseaseCode } ?: listOf(Classification(diseaseCode = 27))
        onClassify(output)
        return output
    }

//    private fun geRotationInfo(rotation: Int): ImageProcessingOptions.Orientation {
//        return when (rotation) {
//            Surface.ROTATION_0 -> ImageProcessingOptions.Orientation.RIGHT_TOP
//            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
//            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
//            else -> ImageProcessingOptions.Orientation.BOTTOM_LEFT
//        }
//    }
}

fun Bitmap.centerCrop(desiredWidth: Int, desiredHeight: Int): Bitmap {
    val xStart = (width - desiredWidth) / 2
    val yStart = (height - desiredHeight) / 2

    if (xStart < 0 || yStart < 0 || desiredWidth > width || desiredHeight > height) {
        throw IllegalArgumentException("Invalid arguments for cropping")
    }

    return Bitmap.createBitmap(this, xStart, yStart, desiredWidth, desiredHeight)
}