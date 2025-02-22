package com.example.facetrackinggame

import android.Manifest
import android.content.Intent
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
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainMenuScreen {
                // Navigate to MainActivity when Play button is clicked
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}

@Composable
fun MainMenuScreen(onPlayClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1F44))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.face_dodge_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onPlayClick,
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Play", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { /* Show Scoreboard */ },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Scoreboard", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { exitApp() },
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Quit", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun exitApp() {
    android.os.Process.killProcess(android.os.Process.myPid())
}

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
