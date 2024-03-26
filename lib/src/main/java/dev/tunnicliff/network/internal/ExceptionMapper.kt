package dev.tunnicliff.network.internal

internal interface ExceptionMapper {
    /**
     * Maps common exceptions.
     */
    fun map(exception: Exception): Exception
}

