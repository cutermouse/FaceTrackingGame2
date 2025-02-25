package com.example.facetrackinggame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View

class GameView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), Runnable {
    private val paint = Paint()
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 100f
        typeface = Typeface.DEFAULT_BOLD
    }

    // Load your character image (replace R.drawable.character with your actual resource)
    private val originalCharacterBitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.character)

    // Desired size for the character image (e.g., 50f)
    private val scaledCharacterBitmap: Bitmap = Bitmap.createScaledBitmap(originalCharacterBitmap, 100, 100, false)

    init {
        setWillNotDraw(false)
        visibility = View.VISIBLE
        background = null

        setOnClickListener {
            if (GameController.isGameOver) {
                GameController.resetGame()
            }
        }
    }

    override fun run() {
        while (true) {
            val canvas = holder.lockCanvas() ?: continue
            try {
                drawGame(canvas)
            } finally {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGame(canvas)
    }

    private fun drawGame(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Draw the character using the scaled image (centered at GameController.characterX, GameController.characterY)
        val characterLeft = GameController.characterX - scaledCharacterBitmap.width / 2f
        val characterTop = GameController.characterY - scaledCharacterBitmap.height / 2f
        canvas.drawBitmap(scaledCharacterBitmap, characterLeft, characterTop, paint)

        GameController.drawObstacles(canvas)
        GameController.drawScore(canvas, textPaint)

        if (GameController.isGameOver) {
            canvas.drawText("Game Over", width / 2f - 200, height / 2f, textPaint)
        }

        // ✅ Draw countdown if active
        if (GameController.countdown > 0) {
            val countdownText = GameController.countdown.toString()
            val countdownPaint = Paint(textPaint).apply {
                textSize = 200f  // Increased text size
                color = Color.RED  // Optional: Make it more visible
            }
            val textWidth = countdownPaint.measureText(countdownText)
            val textX = (width - textWidth) / 2f
            val textY = height / 2f
            canvas.drawText(countdownText, textX, textY, countdownPaint)
        } else {
            val title = if (GameController.useAccelerometerCountdown) {
                "Tilt your phone!!"
            } else {
                "Face Tracking!!"
            }
            val titleWidth = textPaint.measureText(title)
            val titleX = (width - titleWidth) / 2f
            val titleY = height / 5f
            canvas.drawText(title, titleX, titleY, textPaint)
        }
    }

    fun updateView() {
        postInvalidate()
    }
}


