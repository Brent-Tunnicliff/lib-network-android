package dev.tunnicliff.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.set
import java.net.URL
import java.util.UUID

/**
 * Service for managing network calls.
 *
 * Maps responses and exceptions for the app to consume.
 * Each RestService represents one base url.
 */
class RestService internal constructor(
    val baseUrl: URL,
    val client: HttpClient,
    val exceptionMapper: ExceptionMapper,
    val logger: Logger
) {
    companion object {
        const val TAG = "RestService"
    }

    /**
     * Returns a unique identifier.
     *
     * Used when logging network calls.
     */
    fun generateRequestId(): String =
        UUID.randomUUID().toString()

    /**
     * Make a HTTP GET network request.
     *
     * @param Body the HTTP response body type. Expected to conform to `kotlinx.serialization.Serializable`.
     * @param path the path for the request.
     * @param headers the headers to be sent with the request.
     * @param parameters the query parameters to be send with the request.
     * @param progressListener (optional) listener for getting download progress updates.
     * "progressInBytes" is how many bytes has been downloaded so far.
     * "totalSizeInBytes" is the total body size, null means unknown.
     * @exception HttpException mapping of all known unhappy paths.
     */
    @Throws(HttpException::class)
    suspend inline fun <reified Body> get(
        path: String,
        headers: Map<String, String> = emptyMap(),
        parameters: Map<String, String> = emptyMap(),
        noinline progressListener: (progressInBytes: Long, totalSizeInBytes: Long?) -> Unit = { _, _ -> }
    ): Body {
        val requestId = generateRequestId()
        logger.info(TAG, "$requestId: Starting get call, baseUrl: $baseUrl, path: $path")
        try {
            val response = client.get(baseUrl) {
                url.set(path = path)
                parameters.forEach {
                    parameter(key = it.key, value = it.value)
                }

                headers.forEach {
                    header(key = it.key, value = it.value)
                }

                onDownload { bytesSentTotal, contentLength ->
                    logger.info(
                        TAG,
                        "$requestId: Download progress $bytesSentTotal out of $contentLength"
                    )

                    progressListener(bytesSentTotal, contentLength)
                }
            }

            logger.info(TAG, "$requestId: Mapping response body")

            val result = response.body<Body>()

            logger.info(TAG, "$requestId: Request success")

            return result
        } catch (exception: Exception) {
            logger.error(TAG, "$requestId: Request failed", exception)
            throw exceptionMapper.map(exception)
        }
    }
}