package com.devbridie.telegramyoutubedl.telegram.bot

import com.devbridie.telegramyoutubedl.telegram.commands.SearchCommand
import com.devbridie.telegramyoutubedl.telegram.commands.StartCommand
import com.devbridie.telegramyoutubedl.telegram.commands.URLCommand
import com.natpryce.konfig.Configuration
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.commandbot.TelegramLongPollingCommandBot

class YoutubeDlCommandBot(val configuration: Configuration) : TelegramLongPollingCommandBot(DefaultBotOptions(), configuration[BotConfiguration.name]) {
    init {
        register(StartCommand)
        register(SearchCommand)
        register(URLCommand)
    }

    override fun processNonCommandUpdate(update: Update) {
        if (!update.message.hasText()) return

        if (containsLink(update.message.text)) {
            val url = extractLink(update.message.text)
            URLCommand.execute(this, update.message.from, update.message.chat, arrayOf(url))
        } else {
            SearchCommand.execute(this, update.message.from, update.message.chat, arrayOf(update.message.text))
        }
    }

    override fun getBotToken() = configuration[BotConfiguration.token]
}