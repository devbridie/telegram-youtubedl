package com.devbridie.telegramyoutubedl.telegram

import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EmptyConfiguration
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.overriding
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import java.io.File


fun main(args: Array<String>) {
    ApiContextInitializer.init()
    val file = File("configuration.properties")
    val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            (if (file.exists()) ConfigurationProperties.fromFile(file) else EmptyConfiguration) overriding
            ConfigurationProperties.fromResource("configuration.properties")
    val botsApi =TelegramBotsApi()
    botsApi.registerBot(YoutubeDlTelegramBot(config))
}