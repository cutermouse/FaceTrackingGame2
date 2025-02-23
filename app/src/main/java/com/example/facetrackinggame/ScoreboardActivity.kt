package com.example.facetrackinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
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
            scores.value = db.scoreDao().getTopScores().sortedByDescending { it.value } // ✅ Sort scores highest first
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("High Scores", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(scores.value) { index, score -> // ✅ Use `itemsIndexed` to get the rank number
                ScoreRow(index + 1, score.playerName, score.value)
            }
        }
    }
}

@Composable
fun ScoreRow(rank: Int, playerName: String, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$rank.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(end = 8.dp)) // ✅ Rank on the left
        Text(playerName, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text("$score", style = MaterialTheme.typography.bodyLarge)
    }
}
