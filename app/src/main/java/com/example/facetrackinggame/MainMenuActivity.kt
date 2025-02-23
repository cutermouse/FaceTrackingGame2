package com.example.facetrackinggame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainMenuScreen(
                onPlayClick = { startActivity(Intent(this, MainActivity::class.java)) },
                onScoreboardClick = { startActivity(Intent(this, ScoreboardActivity::class.java)) },
                onQuitClick = { finish() }
            )
        }
    }
}

@Composable
fun MainMenuScreen(
    onPlayClick: () -> Unit,
    onScoreboardClick: () -> Unit,
    onQuitClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1F44))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.face_dodge_logo),
                contentDescription = "Game Logo",
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onPlayClick,
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Play", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onScoreboardClick,
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Scoreboard", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onQuitClick,
                modifier = Modifier.width(250.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Quit", fontSize = 20.sp)
            }
        }
    }
}
