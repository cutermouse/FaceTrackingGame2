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

        paint.color = Color.BLUE
        canvas.drawCircle(GameController.characterX, GameController.characterY, 50f, paint)

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
            val title = if (GameController.useAccelerometer) {
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
