package dev.tunnicliff.network

interface ExceptionMapper {
    /**
     * Maps common exceptions.
     */
    fun map(exception: Exception): Exception
}

