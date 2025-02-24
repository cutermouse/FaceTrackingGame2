package com.example.facetrackinggame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

object GameController : SensorEventListener {
    var characterX = 500f
    var characterY = 1200f
    private const val playerRadius = 50f
    var gameView: GameView? = null
    var playerSpeed = 10f // ✅ Initial player speed
    private val normalSpeed = 10f // ✅ Default speed, used for resetting

    private val obstacles = mutableListOf<Obstacle>()
    private val powerUpTimers = mutableMapOf<Obstacle.ShapeType, Long>() // Store the time for active power-ups

    private var screenWidth = 1080f
    private var screenHeight = 1920f

    var isGameOver = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private var frameCount = 0 // Frame counter for spawning obstacles
    var score = 0 // ✅ Score variable
    var useAccelerometer = false // ✅ Toggle between face tracking & accelerometer

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    var countdown = -1

    // ✅ Function to set screen size dynamically
    fun setScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
    }

    // ✅ Initialize sensors (Call this in Activity)
    fun initializeSensors(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            if (useAccelerometer) {
                updateCharacterWithAccelerometer(event.values[0], event.values[1])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startCountdown() {
        countdown = 4 // Start at 4 seconds
        val countdownHandler = Handler(Looper.getMainLooper())

        fun updateCountdown() {
            if (countdown > 0) {
                countdown-- // Reduce countdown
                gameView?.updateView() // Update UI
                countdownHandler.postDelayed({ updateCountdown() }, 1000)
            } else {
                useAccelerometer = !useAccelerometer
                gameView?.updateView()

                // Schedule next toggle after 6 seconds (adjust timing if needed)
                countdownHandler.postDelayed({ startCountdown() }, 6000)
            }
        }

        updateCountdown() // Start countdown loop
    }

    // ✅ Function to toggle movement method
    private fun toggleMovementMethod()
    {
        if (score % 200 == 0 && score > 0 && countdown == -1) { //start swapping movement when score reach 200
            startCountdown()
        }
    }

    // ✅ Face Tracking Movement
    fun updateCharacterMovement(headX: Float, headY: Float) {
        if (isGameOver) return
        if (useAccelerometer) return // Skip if accelerometer is active

        val movementSpeed = 20f

        if (headY > 10) characterX -= movementSpeed
        if (headY < -10) characterX += movementSpeed
        if (headX > 10) characterY -= movementSpeed
        if (headX < -10) characterY += movementSpeed

        characterX = characterX.coerceIn(playerRadius, screenWidth - playerRadius)
        characterY = characterY.coerceIn(playerRadius, screenHeight - playerRadius)

        updateGameView()
    }

    // ✅ Accelerometer Movement
    private fun updateCharacterWithAccelerometer(accelX: Float, accelY: Float) {
        if (isGameOver) return

        val sensitivity = 5f // Adjust movement speed
        characterX -= accelX * sensitivity
        characterY += accelY * sensitivity

        characterX = characterX.coerceIn(playerRadius, screenWidth - playerRadius)
        characterY = characterY.coerceIn(playerRadius, screenHeight - playerRadius)

        updateGameView()
    }

    private fun handlePowerUps() {
        val currentTime = System.currentTimeMillis()

        // Handle Speed Up power-up expiration
        if (powerUpTimers[Obstacle.ShapeType.SPEED_UP]?.let { currentTime - it > 5000 } == true) {
            // Reset speed to normal
            playerSpeed = normalSpeed
            powerUpTimers.remove(Obstacle.ShapeType.SPEED_UP)
        }
    }

    fun updateObstacles() {
        if (isGameOver) return

        frameCount++
        score++ // ✅ Increase score over time
        toggleMovementMethod() // ✅ Check if movement should toggle

        // Create new obstacles
        if (frameCount % 50 == 0) {
            val obstacleCount = Random.nextInt(2, 5)
            repeat(obstacleCount) {
                obstacles.add(Obstacle(screenWidth.toInt()))
            }
        }

        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obstacle = iterator.next()
            obstacle.update()

            if (obstacle.checkCollision(characterX, characterY, playerRadius)) {
                // Handle power-up effects
                when (obstacle.powerUpType) {
                    Obstacle.ShapeType.SPEED_UP -> {
                        // Increase player speed temporarily
                        playerSpeed *= 2.0f
                        powerUpTimers[Obstacle.ShapeType.SPEED_UP] = System.currentTimeMillis()
                    }
                    else -> {
                        isGameOver = true
                    }
                }
                iterator.remove()
            }

            // Remove obstacle if it goes off-screen
            if (obstacle.isOutOfScreen(screenHeight.toInt())) {
                iterator.remove()
            }
        }

        // Handle power-up expiration
        handlePowerUps()

        updateGameView()
    }


    fun drawObstacles(canvas: Canvas) {
        obstacles.forEach { it.draw(canvas) }
    }

    fun drawScore(canvas: Canvas, scorePaint: Paint) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 80f
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }

        val text = "Score: $score"
        val textWidth = paint.measureText(text)  // ✅ Ensure paint is initialized first
        val textX = (screenWidth - textWidth) / 2  // ✅ Center horizontally
        val textY = screenHeight / 10  // ✅ Position near the top

        canvas.drawText(text, textX, textY, paint)
    }

    private fun updateGameView() {
        uiHandler.post { gameView?.updateView() }
    }

    fun resetGame() {
        isGameOver = false
        characterX = screenWidth / 2
        characterY = screenHeight - 300
        obstacles.clear()
        frameCount = 0
        score = 0 // ✅ Reset score on restart
        useAccelerometer = false
        updateGameView()
    }
}
