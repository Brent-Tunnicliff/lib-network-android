package dev.tunnicliff.network.demo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.tunnicliff.network.RestService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URL

class AppViewModel(
    private val restService: RestService
) : ViewModel() {
    companion object {
        private const val TAG = "AppViewModel"

        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as DemoApplication).container
                AppViewModel(
                    restService = appContainer.networkContainer.restService(URL("https://xkcd.com"))
                )
            }
        }
    }

    private val errorMessageState = MutableStateFlow<String?>(null)
    val errorMessage = errorMessageState.asStateFlow()

    private val isLoadingState = MutableStateFlow(false)
    val isLoading = isLoadingState.asStateFlow()

    private val latestComicState = MutableStateFlow<Comic?>(null)
    val latestComic = latestComicState.asStateFlow()

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