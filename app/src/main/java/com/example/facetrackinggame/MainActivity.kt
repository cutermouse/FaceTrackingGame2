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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import android.os.Handler
import android.os.Looper
import android.graphics.Point
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    private var viewFinder: PreviewView? = null  // ✅ Nullable to prevent crash
    private lateinit var gameView: GameView
    private var isGameOverHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // ✅ Ensure layout is set

        resetGameState() // ✅ Ensures fresh game state

        getScreenSize()
        viewFinder = findViewById(R.id.viewFinder)
        gameView = findViewById(R.id.gameView)
        GameController.gameView = gameView
        startObstacleLoop()
        checkCameraPermission()
    }

    private fun resetGameState() {
        GameController.resetGame() // ✅ Ensures a fresh game start
        isGameOverHandled = false
    }

    private fun restartGame() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // ✅ Clears previous activities
        startActivity(intent)
        finish() // ✅ Ensures no old game state is kept
    }

    private fun onGameOver(score: Int) {
        runOnUiThread {
            setContent {
                GameOverScreen(score, ::restartGame) { playerName ->
                    saveHighScore(playerName, score)
                    startActivity(Intent(this, ScoreboardActivity::class.java))
                    finish()
                }
            }
        }
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
            != PackageManager.PERMISSION_GRANTED
        ) {
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
                viewFinder?.let { vf -> it.setSurfaceProvider(vf.surfaceProvider) }
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
            override fun run() {
                if (!GameController.isGameOver) {
                    GameController.updateObstacles()
                } else if (!isGameOverHandled) {
                    isGameOverHandled = true
                    onGameOver(GameController.score) // ✅ Ask for name input when game over
                }
                handler.postDelayed(this, 50)
            }
        }
        handler.post(obstacleRunnable)
    }
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit, onSave: (String) -> Unit) {
    var playerName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)), // Semi-transparent background
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Game Over", fontSize = 24.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Score: $score", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                BasicTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSave(playerName) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (playerName.isNotBlank()) {
                                onSave(playerName)
                            } else {
                                Toast.makeText(context, "Enter a name!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Save Score")
                    }

                    Button(
                        onClick = { onRestart() }  // ✅ Restart the game properly
                    ) {
                        Text("Restart")
                    }
                }
            }
        }
    }
}
