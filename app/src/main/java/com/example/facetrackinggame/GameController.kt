package com.example.facetrackinggame

import android.graphics.Rect

object GameController {
    var characterX = 500f
    var characterY = 500f

    var faceBoundingBox: Rect? = null  // ✅ Store detected face position
    var gameView: GameView? = null  // ✅ Reference to GameView

    fun updateCharacterMovement(headX: Float, headY: Float, boundingBox: Rect) {
        println("Updating character movement: HeadX = $headX, HeadY = $headY")
        println("Bounding Box Received: $boundingBox")

        // ✅ Flip the horizontal movement direction
        if (headY > 10) characterX -= 10f  // Look right (mirrored) → Move LEFT
        if (headY < -10) characterX += 10f // Look left (mirrored) → Move RIGHT

        if (headX > 10) characterY -= 10f  // Look up → Move up
        if (headX < -10) characterY += 10f // Look down → Move down

        faceBoundingBox = boundingBox  // ✅ Save bounding box for drawing

        // ✅ Force GameView to update
        gameView?.updateView()
    }
}
