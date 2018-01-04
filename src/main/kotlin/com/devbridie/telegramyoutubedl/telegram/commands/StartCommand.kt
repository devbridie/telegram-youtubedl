package com.devbridie.telegramyoutubedl.telegram.commands

import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Chat
import org.telegram.telegrambots.api.objects.User
import org.telegram.telegrambots.bots.AbsSender
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand


object StartCommand : BotCommand("start", "Shows the initial help text.") {
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        absSender.execute(SendMessage(chat.id, "Simply send this bot a YouTube URL (e.g. https://www.youtube.com/watch?v=B9v8jLBrvug) or a search term (e.g. ship to wreck)").disableWebPagePreview())
    }
}