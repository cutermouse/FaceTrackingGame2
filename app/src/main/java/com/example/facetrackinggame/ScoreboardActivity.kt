package com.example.facetrackinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star

class ScoreboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScoreboardScreen()
        }
    }
}

@Composable
fun ScoreboardScreen() {
    val db = ScoreDatabase.getDatabase(LocalContext.current)
    val scores = remember { mutableStateOf<List<Score>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            scores.value = db.scoreDao().getTopScores().sortedByDescending { it.value }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1F44)) // ✅ Background color
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Icon(
                imageVector = Icons.Default.EmojiEvents, // ✅ Trophy icon
                contentDescription = "Trophy",
                tint = Color.Yellow, // ✅ Yellow trophy
                modifier = Modifier.size(64.dp) // ✅ Adjust trophy size
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "HIGH SCORES",
                style = TextStyle(
                    fontSize = 36.sp, // ✅ Bigger text
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = Shadow( // ✅ Black border effect
                        color = Color.Black,
                        offset = Offset(4f, 4f),
                        blurRadius = 4f
                    )
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(scores.value) { index, score ->
                    ScoreRow(index + 1, score.playerName, score.value)
                }
            }
        }
    }
}


@Composable
fun ScoreRow(rank: Int, playerName: String, score: Int) {
    val trophyIcon = when (rank) {
        1 -> Icons.Default.Star // You can replace with a gold trophy icon
        2 -> Icons.Default.Star // You can replace with a silver trophy icon
        3 -> Icons.Default.Star // You can replace with a bronze trophy icon
        else -> null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray) // ✅ Background for contrast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add trophy icon for 1st, 2nd, and 3rd place
            trophyIcon?.let {
                Icon(
                    imageVector = it, // Trophy icon based on rank
                    contentDescription = "$rank place trophy",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp), // Padding between icon and text
                    tint = when (rank) {
                        1 -> Color.Yellow // Gold for 1st place
                        2 -> Color.Gray // Silver for 2nd place
                        3 -> Color(0xFFCD7F32) // Bronze for 3rd place
                        else -> Color.White
                    }
                )
            }

            // Rank
            Text(
                "$rank.",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, color = Color.White),
                modifier = Modifier.padding(end = 8.dp)
            )

            // Player name
            Text(
                playerName,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, color = Color.White),
            )

            // Divider to separate player name and score
            Divider(
                color = Color.White, thickness = 2.dp, // ✅ Thin white line
                modifier = Modifier
                    .weight(1f) // ✅ Makes the line expand between name and score
                    .padding(horizontal = 8.dp)
            )

            // Score
            Text(
                "$score",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, color = Color.White),
            )
        }
    }
}






