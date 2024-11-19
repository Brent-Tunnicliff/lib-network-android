// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.network.internal

import dev.tunnicliff.logging.LOG
import dev.tunnicliff.network.HttpException
import dev.tunnicliff.network.RestService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.set
import io.ktor.util.reflect.TypeInfo
import java.net.URL
import java.util.UUID
import kotlin.reflect.KClass

internal class KtorRestService(
    private val baseUrl: URL,
    private val client: HttpClient,
    private val exceptionMapper: ExceptionMapper
) : RestService {
    private companion object {
        const val TAG = "KtorRestService"
    }

    // region RestService

    @Throws(HttpException::class)
    override suspend fun <Body> get(
        path: String,
        headers: Map<String, String>,
        parameters: Map<String, String>,
        ofType: KClass<*>,
        progressListener: (progressInBytes: Long, totalSizeInBytes: Long?) -> Unit
    ): Body {
        val requestId = generateRequestId()
        LOG.info(TAG, "$requestId: Starting get call, baseUrl: $baseUrl, path: $path")
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
                    LOG.info(
                        TAG,
                        "$requestId: Download progress $bytesSentTotal out of $contentLength"
                    )

                    progressListener(bytesSentTotal, contentLength)
                }
            }

            LOG.info(TAG, "$requestId: Mapping response body")

            val result: Body = response.body(ofType)

            LOG.info(TAG, "$requestId: Request success")

            return result
        } catch (exception: Exception) {
            LOG.error(TAG, "$requestId: Request failed", exception)
            throw exceptionMapper.map(exception)
        }
    }

    // endregion

    // region Private

    private fun generateRequestId(): String =
        UUID.randomUUID().toString()

    // endregion
}

private suspend fun <Body> HttpResponse.body(ofType: KClass<*>): Body =
    body(
        TypeInfo(
            type = ofType,
            reifiedType = ofType.java
        )
    )
