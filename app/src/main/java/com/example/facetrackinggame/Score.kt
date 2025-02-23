package com.example.facetrackinggame

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores") // ✅ Define this as a Room entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ✅ Add this primary key
    val playerName: String,
    val value: Int
)
