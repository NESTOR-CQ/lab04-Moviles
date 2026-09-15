package com.example.moviecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.moviecounter.ui.theme.MovieCounterTheme

// --- MODELOS Y VIEWMODELS ---

data class Movie(val id: Int, val name: String)

class MovieViewModel : ViewModel() {
    var movieName by mutableStateOf("")
        private set

    val movies = mutableStateListOf<Movie>()

    fun updateMovieName(newName: String) {
        movieName = newName
    }

    fun addMovie() {
        if (movieName.isNotBlank()) {
            movies.add(Movie(id = movies.size + 1, name = movieName))
            movieName = ""
        }
    }
}

data class WellnessTask(val id: Int, val label: String, var initialChecked: Boolean = false) {
    var checked by mutableStateOf(initialChecked)
}

class WellnessViewModel : ViewModel() {
    private val _tasks = getWellnessTasks().toMutableStateList()
    val tasks: List<WellnessTask> get() = _tasks

    fun remove(item: WellnessTask) {
        _tasks.remove(item)
    }

    fun changeTaskChecked(item: WellnessTask, checked: Boolean) {
        _tasks.find { it.id == item.id }?.let { task ->
            task.checked = checked
        }
    }
}

private fun getWellnessTasks() = List(16) { i -> WellnessTask(i, "Task # $i") }
// --- MAIN ACTIVITY ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieCounterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WellnessScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// --- COMPONENTES WELLNESS (CODELAB OFICIAL) ---

@Composable
fun WaterCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        var count by rememberSaveable { mutableStateOf(0) }

        if (count > 0) {
            Text("You've had $count glasses.")
        }

        Button(
            onClick = { count++ },
            modifier = Modifier.padding(top = 8.dp),
            enabled = count < 10
        ) {
            Text("Add one")
        }
    }
}

@Composable
fun WellnessTaskItem(
    taskName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = taskName,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        IconButton(onClick = onClose) {
            Text("✕", modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun WellnessScreen(
    modifier: Modifier = Modifier,
    wellnessViewModel: WellnessViewModel = remember { WellnessViewModel() }
) {
    Column(modifier = modifier) {
        WaterCounter()

        LazyColumn {
            items(
                items = wellnessViewModel.tasks,
                key = { task -> task.id }
            ) { task ->
                WellnessTaskItem(
                    taskName = task.label,
                    checked = task.checked,
                    onCheckedChange = { checked ->
                        wellnessViewModel.changeTaskChecked(task, checked)
                    },
                    onClose = { wellnessViewModel.remove(task) }
                )
            }
        }
    }
}

// --- COMPONENTES ANTERIORES ---

@Composable
fun MovieStatelessCounter(
    count: Int,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "You have added $count movies.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onIncrement) {
            Text("Add Movie")
        }
    }
}

@Composable
fun MovieStatefulCounter(
    modifier: Modifier = Modifier,
    viewModel: MovieViewModel = remember { MovieViewModel() }
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MovieStatelessCounter(
            count = viewModel.movies.size,
            onIncrement = { viewModel.addMovie() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.movieName,
            onValueChange = { viewModel.updateMovieName(it) },
            label = { Text("Movie Name") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.movies) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "${movie.id}. ${movie.name}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WellnessPreview() {
    MovieCounterTheme {
        WellnessScreen()
    }
}