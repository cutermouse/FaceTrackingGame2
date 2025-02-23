package com.example.facetrackinggame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.sin
import kotlin.random.Random

class Obstacle(screenWidth: Int) {
    enum class ShapeType { CIRCLE, SQUARE, TRIANGLE, SPEED_UP }

    var x: Float = Random.nextInt(50, screenWidth - 50).toFloat() // ✅ Random X position
    var y: Float = 0f // ✅ Start at the top
    var size: Float = Random.nextInt(30, 100).toFloat() // ✅ Random size
    var speed: Float = Random.nextInt(10, 25).toFloat() // ✅ Falling speed
    var shape: ShapeType = ShapeType.values().random() // ✅ Random shape
    var powerUpType: ShapeType = ShapeType.CIRCLE // ✅ Default is no power-up

    private val paint = Paint().apply {
        color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)) // ✅ Random color
    }

    private var colorCycleTime = 0L // Track the color change cycle time
    private val colorCycleDuration = 1000L // Duration of one full color cycle (in milliseconds)

    init {
        // Randomly assign a power-up type (optional)
        if (Random.nextInt(100) < 30) {  // 50% chance to be a power-up obstacle
            powerUpType = ShapeType.SPEED_UP // Always make sure the power-up is a SPEED_UP
        }

        // Set a fixed size for SPEED_UP obstacles
        if (powerUpType == ShapeType.SPEED_UP) {
            size = 100f // Example fixed size for SPEED_UP
        }
    }

    fun update() {
        y += speed // ✅ Objects fall faster
        if (powerUpType == ShapeType.SPEED_UP) {
            updateColorCycle() // Update the color cycle for SPEED_UP
        }
    }

    fun draw(canvas: Canvas) {
        when (shape) {
            ShapeType.CIRCLE -> canvas.drawCircle(x, y, size, paint)
            ShapeType.SQUARE -> canvas.drawRect(x - size, y - size, x + size, y + size, paint)
            ShapeType.TRIANGLE -> drawTriangle(canvas)
            ShapeType.SPEED_UP -> drawSpeedUp(canvas) // ✅ Draw Speed Up Power-up
        }
    }

    private fun drawTriangle(canvas: Canvas) {
        val path = Path().apply {
            moveTo(x, y - size)  // Top point
            lineTo(x - size, y + size) // Bottom left
            lineTo(x + size, y + size) // Bottom right
            close()
        }
        canvas.drawPath(path, paint)
    }

    private fun drawSpeedUp(canvas: Canvas) {
        val radius = size / 2
        val speedUpPaint = Paint().apply {
            color = getColorForSpeedUp() // Get the dynamically changing color
        }
        canvas.drawCircle(x, y, radius, speedUpPaint) // Draw a circle for the speed-up power-up
    }

    private fun updateColorCycle() {
        colorCycleTime += 16 // Increment color cycle time (roughly per frame, assuming 60 FPS)
        if (colorCycleTime > colorCycleDuration) colorCycleTime = 0 // Reset after full cycle
    }

    private fun getColorForSpeedUp(): Int {
        // Calculate the cycle fraction (from 0 to 1) based on the color cycle time
        val fraction = colorCycleTime / colorCycleDuration.toFloat()

        // Cycle through colors using sine waves for RGB components
        val r = (sin(fraction * Math.PI * 2) * 127 + 128).toInt() // Red cycles through
        val g = (sin(fraction * Math.PI * 2 + Math.PI / 3) * 127 + 128).toInt() // Green cycles with phase shift
        val b = (sin(fraction * Math.PI * 2 + Math.PI / 1.5) * 127 + 128).toInt() // Blue cycles with another phase shift

        return Color.rgb(r, g, b)
    }

    fun isOutOfScreen(screenHeight: Int): Boolean {
        return y > screenHeight // ✅ Remove when it falls off the screen
    }

    fun checkCollision(playerX: Float, playerY: Float, playerRadius: Float): Boolean {
        val dx = x - playerX
        val dy = y - playerY
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        return distance < (size + playerRadius) // ✅ Collision detection
    }
}
