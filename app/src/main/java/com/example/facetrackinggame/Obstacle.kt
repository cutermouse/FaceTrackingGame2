package com.example.facetrackinggame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.random.Random

class Obstacle(screenWidth: Int) {
    enum class ShapeType { CIRCLE, SQUARE, TRIANGLE }

    var x: Float = Random.nextInt(50, screenWidth - 50).toFloat() // ✅ Random X position
    var y: Float = 0f // ✅ Start at the top
    var size: Float = Random.nextInt(30, 100).toFloat() // ✅ Random size
    var speed: Float = Random.nextInt(10, 25).toFloat() // ✅ Increased falling speed (Default was 5-15)
    var shape: ShapeType = ShapeType.values().random() // ✅ Random shape

    private val paint = Paint().apply {
        color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)) // ✅ Random color
    }

    fun update() {
        y += speed // ✅ Increased speed makes objects fall faster
    }

    fun draw(canvas: Canvas) {
        when (shape) {
            ShapeType.CIRCLE -> canvas.drawCircle(x, y, size, paint)
            ShapeType.SQUARE -> canvas.drawRect(x - size, y - size, x + size, y + size, paint)
            ShapeType.TRIANGLE -> drawTriangle(canvas)
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
