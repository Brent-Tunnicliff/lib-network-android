package dev.tunnicliff.network.internal

import com.google.gson.Gson
import dev.tunnicliff.network.HttpException
import dev.tunnicliff.network.RestService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.http.set
import java.net.URL
import java.util.UUID
import kotlin.reflect.KClass

internal class KtorRestService(
    private val baseUrl: URL,
    private val client: HttpClient,
    private val exceptionMapper: ExceptionMapper,
    private val logger: Logger
) : RestService {
    private companion object {
        const val TAG = "RestServiceImpl"
    }

    // region RestService

    @Throws(HttpException::class)
    override suspend fun <Body> get(
        path: String,
        ofType: KClass<*>,
        progressListener: (progressInBytes: Long, totalSizeInBytes: Long?) -> Unit
    ): Body {
        val requestId = generateRequestId()
        logger.info(TAG, "$requestId: Starting get call, baseUrl: $baseUrl, path: $path")
        try {
            val response = client.get(baseUrl) {
                url.set(path = path)
                onDownload { bytesSentTotal, contentLength ->
                    logger.info(
                        TAG,
                        "$requestId: Download progress $bytesSentTotal out of $contentLength"
                    )

                    progressListener(bytesSentTotal, contentLength)
                }
            }

            logger.info(TAG, "$requestId: Mapping response body")

            val result: Body = Gson().fromJson<Body>(response.body<String>(), ofType.java)

            logger.info(TAG, "$requestId: Request success")

            return result
        } catch (exception: Exception) {
            logger.error(TAG, "$requestId: Request failed", exception)
            throw exceptionMapper.map(exception)
        }
    }

    // endregion

    // region Private

    private fun generateRequestId(): String =
        UUID.randomUUID().toString()

    // endregion
}
