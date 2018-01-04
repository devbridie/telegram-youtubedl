package com.devbridie.telegramyoutubedl

import com.devbridie.telegramyoutubedl.telegram.bot.YoutubeDlCommandBot
import com.devbridie.telegramyoutubedl.youtubedl.wrappers.getYoutubeDlVersion
import com.natpryce.konfig.*
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import java.io.File

fun getConfiguration(): Configuration {
    val file = File("configuration.properties")
    return ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            (if (file.exists()) ConfigurationProperties.fromFile(file) else EmptyConfiguration) overriding
            ConfigurationProperties.fromResource("configuration.properties")
}

fun main(args: Array<String>) {
    println("Using youtube-dl version ${getYoutubeDlVersion()}.")
    ApiContextInitializer.init()
    TelegramBotsApi().registerBot(YoutubeDlCommandBot(getConfiguration()))
}