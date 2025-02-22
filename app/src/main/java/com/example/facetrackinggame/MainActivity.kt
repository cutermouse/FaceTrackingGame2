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
    import android.os.Process
    import android.view.WindowManager
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.facetrackinggame.ScoreboardActivity
    import com.example.facetrackinggame.Score
    import com.example.facetrackinggame.ScoreDatabase
    import kotlinx.coroutines.GlobalScope
    import kotlinx.coroutines.launch

    class MainActivity : ComponentActivity() {
        private lateinit var viewFinder: PreviewView
        private lateinit var gameView: GameView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            getScreenSize()
            viewFinder = findViewById(R.id.viewFinder)
            gameView = findViewById(R.id.gameView)
            GameController.gameView = gameView
            startObstacleLoop()
            checkCameraPermission()
        }

        private fun onGameOver(playerName: String, score: Int) {  // 🔹 Call this when the game ends
            saveHighScore(playerName, score) // ✅ Save the player's score
            startActivity(Intent(this, ScoreboardActivity::class.java)) // ✅ Open scoreboard
            finish()
        }

        private fun saveHighScore(playerName: String, score: Int) {
            if (playerName.isBlank()) return
            val db = ScoreDatabase.getDatabase(this)
            GlobalScope.launch {
                db.scoreDao().insertScore(Score(playerName = playerName, value = score))
            }
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
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
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

        private fun startObstacleLoop() {
            val handler = Handler(Looper.getMainLooper())
            val obstacleRunnable = object : Runnable {
                var gameOverHandled = false  // ✅ Prevents multiple calls

                override fun run() {
                    if (!GameController.isGameOver) {
                        GameController.updateObstacles()
                    } else if (!gameOverHandled) { // ✅ Ensure it runs only once
                        gameOverHandled = true
                        onGameOver("Player2", 1200) // ✅ Call when game over
                    }
                    handler.postDelayed(this, 50)
                }
            }
            handler.post(obstacleRunnable)
        }

    }

