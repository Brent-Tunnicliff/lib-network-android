// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.network

/**
 * Represents HTTP error status codes.
 */
sealed class HttpException(
    open val code: Int?,
    message: String
) : Exception("HTTP request failed with code $code, $message") {
    /**
     * Network errors like timeout or connection issue.
     */
    class NetworkException(message: String) : HttpException(null, message)

    /**
     * HTTP error code 300 - 399
     */
    class UnhandedRedirectException(code: Int, message: String) : HttpException(code, message)

    /**
     * HTTP error code 400 - 499
     */
    class ClientException(code: Int, message: String) : HttpException(code, message)

    /**
     * HTTP error code 500 - 599
     */
    class ServerException(code: Int, message: String) : HttpException(code, message)
}


