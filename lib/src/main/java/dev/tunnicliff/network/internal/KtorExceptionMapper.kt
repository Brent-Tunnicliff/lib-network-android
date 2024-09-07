// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.network.internal

import dev.tunnicliff.network.HttpException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.utils.io.errors.IOException

internal class KtorExceptionMapper : ExceptionMapper {
    override fun map(exception: Exception): Exception =
        when (exception) {
            is RedirectResponseException ->
                HttpException.UnhandedRedirectException(
                    code = exception.response.status.value,
                    message = exception.message
                )

            is ClientRequestException ->
                HttpException.ClientException(
                    code = exception.response.status.value,
                    message = exception.message
                )

            is ServerResponseException ->
                HttpException.ServerException(
                    code = exception.response.status.value,
                    message = exception.message
                )

            is IOException ->
                HttpException.NetworkException(
                    message = exception.message ?: "Unknown"
                )

            else -> exception
        }
}