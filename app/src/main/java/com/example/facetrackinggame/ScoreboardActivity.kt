package com.example.facetrackinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.facetrackinggame.Score
import com.example.facetrackinggame.ScoreDatabase
import kotlinx.coroutines.launch

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
            scores.value = db.scoreDao().getTopScores()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("High Scores", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(scores.value) { score ->
                ScoreRow(score.playerName, score.value)
            }
        }
    }
}

@Composable
fun ScoreRow(playerName: String, score: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text("$playerName: $score", style = MaterialTheme.typography.bodyLarge)
    }
}
