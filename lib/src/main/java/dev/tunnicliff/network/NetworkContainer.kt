package dev.tunnicliff.network

import dev.tunnicliff.container.Container
import dev.tunnicliff.network.internal.ExceptionMapper
import dev.tunnicliff.network.internal.KtorExceptionMapper
import dev.tunnicliff.network.internal.KtorRestService
import dev.tunnicliff.network.internal.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import java.net.URL

/**
 * Container for resolving network library objects.
 */
class NetworkContainer : Container() {
    private companion object {
        const val CLIENT_TIMEOUT_IN_MILLISECONDS: Long = 15_000
    }

    // region Public resolvers

    /**
     * Resolve the RestService object.
     *
     * @param baseURL base URL for every network request by this service.
     */
    fun restService(baseURL: URL): RestService = resolveWeak(name = baseURL.toString()) {
        KtorRestService(
            baseUrl = baseURL,
            client = httpClient(),
            exceptionMapper = exceptionMapper(),
            logger = logger()
        )
    }

    // endregion

    // region Private resolvers

    private fun httpClient(): HttpClient = resolveWeak {
        HttpClient(CIO) {
            expectSuccess = true
            install(HttpTimeout) {
                requestTimeoutMillis = CLIENT_TIMEOUT_IN_MILLISECONDS
                connectTimeoutMillis = CLIENT_TIMEOUT_IN_MILLISECONDS
                socketTimeoutMillis = CLIENT_TIMEOUT_IN_MILLISECONDS
            }
        }
    }

    private fun exceptionMapper(): ExceptionMapper = resolveWeak {
        KtorExceptionMapper()
    }

    private fun logger(): Logger = resolveWeak {
        Logger()
    }

    // endregion
}