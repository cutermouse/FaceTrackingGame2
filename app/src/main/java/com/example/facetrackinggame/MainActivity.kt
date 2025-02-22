package com.example.facetrackinggame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

import android.os.Handler
import android.os.Looper

import android.graphics.Point
import android.view.WindowManager


class MainActivity : ComponentActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var gameView: GameView  // ✅ Add GameView reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Set the correct layout (with both views)
        setContentView(R.layout.activity_main)

        getScreenSize() // ✅ Get actual screen size dynamically

        // ✅ Initialize both views
        viewFinder = findViewById(R.id.viewFinder)
        gameView = findViewById(R.id.gameView)

        GameController.gameView = gameView

        // ✅ Start the obstacle update loop
        startObstacleLoop()

        checkCameraPermission()
    }

    private fun getScreenSize() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        GameController.setScreenSize(size.x.toFloat(), size.y.toFloat())
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)  // ✅ ViewFinder now initialized
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), FaceAnalyzer())
                }
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    // ✅ Added function to continuously update falling obstacles
    private fun startObstacleLoop() {
        val handler = Handler(Looper.getMainLooper())
        val obstacleRunnable = object : Runnable {
            override fun run() {
                if (!GameController.isGameOver) { // ✅ Stop updating when game over
                    GameController.updateObstacles()
                }
                handler.postDelayed(this, 50) // ✅ Run every 50ms
            }
        }
        handler.post(obstacleRunnable)
    }
}
