package com.devbridie.telegramyoutubedl.telegram.commands

import org.telegram.telegrambots.api.objects.Chat
import org.telegram.telegrambots.api.objects.User
import org.telegram.telegrambots.bots.AbsSender

object URLCommand : YoutubeDlCommand("url", "Sends the video located at a given YouTube URL as an audio file.") {
    override fun execute(absSender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        if (arguments.isEmpty())  {
            absSender.sendSimpleMessage(chat.id, "$COMMAND_INIT_CHARACTER$commandIdentifier expects 1 argument.")
        } else {
            val url = arguments[0]
            val statusMessage = absSender.sendSimpleMessage(chat.id, "Getting info from $url...")
            download(absSender, statusMessage, url)
        }
    }
}