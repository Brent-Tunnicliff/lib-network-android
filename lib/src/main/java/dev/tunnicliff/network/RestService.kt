package dev.tunnicliff.network

import dev.tunnicliff.network.internal.KtorExceptionMapper
import dev.tunnicliff.network.internal.KtorRestService
import dev.tunnicliff.network.internal.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import java.net.URL
import kotlin.reflect.KClass

/**
 * Service for managing network calls.
 *
 * Maps responses and exceptions for the app to consume.
 * Each RestService represents one base url.
 */
interface RestService {
    /**
     * Make a HTTP GET network request.
     *
     * @param Body the HTTP response body type.
     * @param path the path for the request.
     * @param ofType the HTTP response body type for mapping. Should be "Body::class".
     * @param progressListener (optional) listener for getting download progress updates.
     * "progressInBytes" is how many bytes has been downloaded so far.
     * "totalSizeInBytes" is the total body size, null means unknown.
     * @exception HttpException mapping of all known unhappy paths.
     */
    @Throws(HttpException::class)
    suspend fun <Body> get(
        path: String,
        ofType: KClass<*>,
        progressListener: (progressInBytes: Long, totalSizeInBytes: Long?) -> Unit = { _, _ -> }
    ): Body

    // region Builder

    /**
     * Builder for generating an instance of RestService
     *
     * @param baseUrl the base url for all network calls using this instance.
     */
    class Builder(private val baseUrl: URL) {
        private companion object {
            // lets start with 15 seconds and tweak later if needed.
            const val TIMEOUT_IN_MILLISECONDS: Long = 15_000
        }

        private var timeout = TIMEOUT_IN_MILLISECONDS

        /**
         * Builds an instance of RestService with all configured parameters.
         */
        fun build(): RestService {
            val client = HttpClient(CIO) {
                expectSuccess = true
                install(HttpTimeout) {
                    requestTimeoutMillis = timeout
                    connectTimeoutMillis = timeout
                    socketTimeoutMillis = timeout
                }
//                install(ContentNegotiation) {
//                    json()
//                }
            }

            return KtorRestService(
                baseUrl = baseUrl,
                client = client,
                exceptionMapper = KtorExceptionMapper(),
                logger = Logger()
            )
        }

        /**
         * Sets the timeout for requests made with this service.
         *
         * @param timeoutInMilliseconds number of milliseconds allowed before timeout error is thrown.
         * @return modified reference to current builder.
         */
        fun setTimeout(timeoutInMilliseconds: Long): Builder = this.apply {
            timeout = timeoutInMilliseconds
        }
    }

    // endregion
}