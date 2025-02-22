package com.example.facetrackinggame

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import kotlin.random.Random

object GameController {
    var characterX = 500f
    var characterY = 1200f
    private const val playerRadius = 50f
    var gameView: GameView? = null

    private val obstacles = mutableListOf<Obstacle>()

    // ✅ Screen size will be set dynamically
    private var screenWidth = 1080f
    private var screenHeight = 1920f

    var isGameOver = false
    private val uiHandler = Handler(Looper.getMainLooper())

    private var frameCount = 0 // Frame counter for spawning obstacles

    // ✅ Function to set screen size dynamically
    fun setScreenSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
    }

    fun updateCharacterMovement(headX: Float, headY: Float) {
        if (isGameOver) return

        val movementSpeed = 20f // ✅ Faster movement

        if (headY > 10) characterX -= movementSpeed // Look right → Move left
        if (headY < -10) characterX += movementSpeed // Look left → Move right
        if (headX > 10) characterY -= movementSpeed // Look up → Move up
        if (headX < -10) characterY += movementSpeed // Look down → Move down

        // ✅ Prevent character from going off-screen
        characterX = characterX.coerceIn(playerRadius, screenWidth - playerRadius)
        characterY = characterY.coerceIn(playerRadius, screenHeight - playerRadius)

        updateGameView()
    }

    fun updateObstacles() {
        if (isGameOver) return

        frameCount++

        // ✅ Spawn multiple obstacles at once (2 to 4 every 50 frames)
        if (frameCount % 50 == 0) {
            val obstacleCount = Random.nextInt(2, 5) // ✅ Randomly spawn 2 to 4 obstacles at once
            repeat(obstacleCount) {
                obstacles.add(Obstacle(screenWidth.toInt()))
            }
        }

        val iterator = obstacles.iterator()
        while (iterator.hasNext()) {
            val obstacle = iterator.next()

            // ✅ Increase falling speed over time
            obstacle.speed += 0.05f

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

    private fun updateGameView() {
        uiHandler.post { gameView?.updateView() }
    }

    fun resetGame() {
        isGameOver = false
        characterX = screenWidth / 2 // ✅ Center character based on real width
        characterY = screenHeight - 300 // ✅ Adjust position based on real height
        obstacles.clear()
        frameCount = 0
        println("Game Restarted!")
        updateGameView()
    }
}
