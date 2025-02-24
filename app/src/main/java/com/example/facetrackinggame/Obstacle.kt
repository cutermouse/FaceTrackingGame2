package com.example.facetrackinggame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.random.Random

class Obstacle(context: Context, screenWidth: Int) {
    enum class ShapeType { CIRCLE, SQUARE, TRIANGLE, SPEED_UP }

    var x: Float = Random.nextInt(50, screenWidth - 50).toFloat() // Random X position
    var y: Float = 0f // Start at the top
    var size: Float = 0f // Initialize size to 0 (will be set in init)
    var speed: Float = Random.nextInt(10, 25).toFloat() // Falling speed
    var shape: ShapeType = ShapeType.values().random() // Random shape

    private val paint = Paint().apply {
        color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)) // Random color
    }

    private var bitmap: Bitmap? = null

    init {
        // 30% chance for the SPEED_UP to spawn, but only after a normal obstacle has been created
        if (Random.nextInt(100) < 30) {
            shape = ShapeType.SPEED_UP
            size = 80f // Set a fixed size for SPEED_UP
            speed *= 1.5f // Increase speed by 50% (you can adjust the multiplier)
        } else {
            // Randomize size and shape for other obstacles
            shape = ShapeType.values().filter { it != ShapeType.SPEED_UP }.random() // Ensure it's not SPEED_UP
            size = Random.nextInt(100, 200).toFloat()
        }

        // Load a unique image based on the shape type
        bitmap = when (shape) {
            ShapeType.CIRCLE -> loadScaledBitmap(context, R.drawable.bomb)
            ShapeType.SQUARE -> loadScaledBitmap(context, R.drawable.crate)
            ShapeType.TRIANGLE -> loadScaledBitmap(context, R.drawable.danger)
            ShapeType.SPEED_UP -> loadScaledBitmap(context, R.drawable.speed_boost)
        }
    }


    fun update() {
        y += speed // Objects fall faster
    }

    fun draw(canvas: Canvas) {
        // Draw the image centered on the obstacle
        bitmap?.let {
            val left = x - size / 2
            val top = y - size / 2
            canvas.drawBitmap(it, left, top, null)
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
        return y > screenHeight // Remove when it falls off the screen
    }

    fun checkCollision(playerX: Float, playerY: Float, playerRadius: Float): Boolean {
        val dx = x - playerX
        val dy = y - playerY
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        return distance < (size / 2 + playerRadius) // Collision detection
    }

    // Function to load and scale bitmap
    private fun loadScaledBitmap(context: Context, resId: Int): Bitmap {
        val originalBitmap = BitmapFactory.decodeResource(context.resources, resId)
        return Bitmap.createScaledBitmap(originalBitmap, size.toInt(), size.toInt(), true)
    }
}