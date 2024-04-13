package dev.tunnicliff.network

import android.util.Log

/**
 * Placeholder Logger until the `lib-logging-android` library os ready.
 */
class Logger {
    fun debug(tag: String, message: String, throwable: Throwable? = null) {
        Log.d(tag, message, throwable)
    }

    fun info(tag: String, message: String, throwable: Throwable? = null) {
        Log.i(tag, message, throwable)
    }

    fun warning(tag: String, message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
    }

    fun error(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    fun critical(tag: String, message: String, throwable: Throwable? = null) {
        Log.wtf(tag, message, throwable)
    }

}