package com.example.facetrackinggame

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

import androidx.annotation.OptIn
import com.example.facetrackinggame.GameController

class FaceAnalyzer : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isEmpty()) {
                    println("No face detected!")
                }

                for (face in faces) {
                    val headX = face.headEulerAngleX  // Up/Down
                    val headY = face.headEulerAngleY  // Left/Right
                    val boundingBox = face.boundingBox  // ✅ Get face bounding box

                    println("Face Detected: HeadX = $headX, HeadY = $headY, Box: $boundingBox")

                    GameController.updateCharacterMovement(headX, headY, boundingBox)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
