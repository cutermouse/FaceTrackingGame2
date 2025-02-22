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
        background = null // ✅ Ensure background is transparent

        setOnClickListener {
            if (GameController.isGameOver) {
                GameController.resetGame() // ✅ Restart when tapping after game over
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
        // ✅ Do not clear the entire canvas to avoid hiding the camera
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        // Draw player
        paint.color = Color.BLUE
        canvas.drawCircle(GameController.characterX, GameController.characterY, 50f, paint)

        // Draw obstacles
        GameController.drawObstacles(canvas)

        // ✅ Show "Game Over" text
        if (GameController.isGameOver) {
            canvas.drawText("Game Over", width / 2f - 200, height / 2f, textPaint)
        }
    }

    fun updateView() {
        postInvalidate()
    }
}
