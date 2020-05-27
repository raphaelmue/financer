package org.financer.client.javafx.main

import javafx.application.Application

object ApplicationLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("javafx.preloader", "org.financer.client.javafx.main.SplashScreenLoader")
        Application.launch(FinancerApplication::class.java, *args)
    }
}