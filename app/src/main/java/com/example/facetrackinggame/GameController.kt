package com.example.facetrackinggame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

object GameController {
    var characterX = 500f
    var characterY = 1200f
    private const val playerRadius = 50f
    var gameView: GameView? = null

    private val obstacles = mutableListOf<Obstacle>()

    private var screenWidth = 1080f
    private var screenHeight = 1920f

    var isGameOver = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private var frameCount = 0 // Frame counter for spawning obstacles
    var score = 0 // ✅ Added score variable

    // ✅ Function to set screen size dynamically
    fun setScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
    }

    fun updateCharacterMovement(headX: Float, headY: Float) {
        if (isGameOver) return

        val movementSpeed = 20f

        if (headY > 10) characterX -= movementSpeed
        if (headY < -10) characterX += movementSpeed
        if (headX > 10) characterY -= movementSpeed
        if (headX < -10) characterY += movementSpeed

        characterX = characterX.coerceIn(playerRadius, screenWidth - playerRadius)
        characterY = characterY.coerceIn(playerRadius, screenHeight - playerRadius)

        updateGameView()
    }

    fun updateObstacles() {
        if (isGameOver) return

        frameCount++
        score++ // ✅ Increase score over time

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
                println("💥 Collision detected! Game Over")
                isGameOver = true
                updateGameView()
                return
            }

            if (obstacle.isOutOfScreen(screenHeight.toInt())) {
                iterator.remove()
            }
        }

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
        println("Game Restarted!")
        updateGameView()
    }
}
