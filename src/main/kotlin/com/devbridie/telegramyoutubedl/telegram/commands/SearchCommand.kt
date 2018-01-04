package com.devbridie.telegramyoutubedl.telegram.commands

import org.telegram.telegrambots.api.objects.Chat
import org.telegram.telegrambots.api.objects.User
import org.telegram.telegrambots.bots.AbsSender

object SearchCommand : YoutubeDlCommand("search", "Searches YouTube for the given term and sends the first result as an audio file.") {
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        if (arguments.isEmpty())  {
            absSender.sendSimpleMessage(chat.id, "$COMMAND_INIT_CHARACTER$commandIdentifier expects at least 1 argument.")
        } else {
            val text = arguments.joinToString(separator = " ")
            val statusMessage = absSender.sendSimpleMessage(chat.id, "Using search for \"$text\"...")
            download(absSender, statusMessage, text)
        }
    }
}