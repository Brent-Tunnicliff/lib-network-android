package dev.tunnicliff.network.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.GsonBuilder
import dev.tunnicliff.network.demo.ui.theme.DemoTheme
import kotlinx.coroutines.launch

private val gson = GsonBuilder().setPrettyPrinting().create()

@Composable
fun App(
    viewModel: AppViewModel = viewModel()
) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val latestComic by viewModel.latestComic.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    DemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.getComic()
                        }
                    }
                ) {
                    Text(text = "Get latest comic")
                }

                HorizontalDivider()

                if (isLoading) {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage?.let {
                    Text(text = it)
                }

                latestComic?.let {
                    Text(text = gson.toJson(latestComic))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    App()
}