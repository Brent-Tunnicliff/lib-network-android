// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.network.demo
import kotlinx.serialization.Serializable

@Serializable
data class Comic(
    val alt: String,
    val day: String,
    val img: String,
    val month: String,
    val num: Int,
    val title: String,
    val year: String
)
