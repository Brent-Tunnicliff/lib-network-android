// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.network.internal

internal interface ExceptionMapper {
    /**
     * Maps common exceptions.
     */
    fun map(exception: Exception): Exception
}

