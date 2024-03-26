package dev.tunnicliff.network.demo

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.tunnicliff.network.RestService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URL

class AppViewModel : ViewModel() {
    private companion object {
        const val TAG = "AppViewModel"
    }

    private val errorMessageState = MutableStateFlow<String?>(null)
    val errorMessage = errorMessageState.asStateFlow()

    private val isLoadingState = MutableStateFlow(false)
    val isLoading = isLoadingState.asStateFlow()

    private val latestComicState = MutableStateFlow<Comic?>(null)
    val latestComic = latestComicState.asStateFlow()

    private val restService = RestService
        .Builder(URL("https://xkcd.com"))
        .build()

    suspend fun getComic() {
        errorMessageState.emit(null)
        latestComicState.emit(null)

        try {
            latestComicState.emit(
                restService.get<Comic>(
                    path = "info.0.json",
                    ofType = Comic::class
                )
            )
        } catch (exception: Throwable) {
            Log.e(TAG, "Getting comic threw error", exception)
            errorMessageState.emit("Error: ${exception.message ?: "Unknown"}")
        }
    }
}