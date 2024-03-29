package dev.tunnicliff.network.demo

import android.app.Application

class DemoApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}