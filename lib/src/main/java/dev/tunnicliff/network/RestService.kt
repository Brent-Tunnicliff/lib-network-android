// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.network

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
     * @param Body the HTTP response body type. Must conform to `kotlinx.serialization.Serializable`.
     * @param path the path for the request.
     * @param headers the headers to be sent with the request.
     * @param parameters the query parameters to be send with the request.
     * @param ofType the HTTP response body type for mapping. Should be "Body::class".
     * @param progressListener (optional) listener for getting download progress updates.
     * "progressInBytes" is how many bytes has been downloaded so far.
     * "totalSizeInBytes" is the total body size, null means unknown.
     * @exception HttpException mapping of all known unhappy paths.
     */
    @Throws(HttpException::class)
    suspend fun <Body> get(
        path: String,
        headers: Map<String, String> = emptyMap(),
        parameters: Map<String, String> = emptyMap(),
        ofType: KClass<*>,
        progressListener: (progressInBytes: Long, totalSizeInBytes: Long?) -> Unit = { _, _ -> }
    ): Body
}