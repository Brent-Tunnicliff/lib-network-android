package dev.tunnicliff.network.demo

import dev.tunnicliff.container.Container
import dev.tunnicliff.network.NetworkContainer

class AppContainer : Container() {
    val networkContainer = NetworkContainer()
}