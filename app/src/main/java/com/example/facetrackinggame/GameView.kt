package com.example.facetrackinggame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View

class GameView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), Runnable {
    private val thread = Thread(this)
    private val paint = Paint()
    private val facePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    init {
        setWillNotDraw(false) // ✅ Ensure the view can draw itself
        visibility = View.VISIBLE // ✅ Make sure GameView is visible
    }

    override fun run() {
        while (true) {
            val canvas = holder.lockCanvas() ?: continue
            drawGame(canvas)
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGame(canvas)
    }

    private fun drawGame(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT) // ✅ Make background transparent
        paint.color = Color.BLUE

        // ✅ Ensure the character moves
        println("Drawing Character at X=${GameController.characterX}, Y=${GameController.characterY}")

        // ✅ Draw the blue circle at the updated position
        canvas.drawCircle(GameController.characterX, GameController.characterY, 50f, paint)
    }

    // ✅ Add a function to refresh the GameView
    fun updateView() {
        invalidate()  // ✅ Forces a redraw
    }
}
