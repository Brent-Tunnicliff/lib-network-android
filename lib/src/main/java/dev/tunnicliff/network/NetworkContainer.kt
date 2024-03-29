package dev.tunnicliff.network

import dev.tunnicliff.network.internal.ExceptionMapper
import dev.tunnicliff.network.internal.KtorExceptionMapper
import dev.tunnicliff.network.internal.KtorRestService
import dev.tunnicliff.network.internal.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import java.lang.ref.WeakReference
import java.net.URL

/**
 * Container for resolving network library objects.
 */
class NetworkContainer {
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

    // region helpers

    private val weakObjectReferences: MutableMap<String, WeakReference<Any>> = mutableMapOf()
    private inline fun <reified Object : Any> resolveWeak(
        name: String = "",
        createObject: () -> Object
    ): Object {
        val key = "${Object::class}-$name"
        val cachedObject = weakObjectReferences[key]?.get()

        if (cachedObject != null && cachedObject is Object) {
            return cachedObject
        }

        return createObject().also {
            weakObjectReferences[key] = WeakReference(it)
        }
    }

    // endregion
}