package com.devbridie.telegramyoutubedl.telegram.commands

import com.devbridie.telegramyoutubedl.telegram.*
import com.devbridie.telegramyoutubedl.telegram.bot.EditMessageResultHandler
import com.devbridie.telegramyoutubedl.youtubedl.wrappers.DownloadOptions
import com.devbridie.telegramyoutubedl.youtubedl.wrappers.InfoOptions
import com.devbridie.telegramyoutubedl.youtubedl.wrappers.downloadFile
import com.devbridie.telegramyoutubedl.youtubedl.wrappers.getInfo
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.AbsSender
import org.telegram.telegrambots.bots.commandbot.commands.BotCommand

abstract class YoutubeDlCommand(command: String, description: String) : BotCommand(command, description) {

    fun download(absSender: AbsSender, statusMessage: Message, input: String) {
        val from = statusMessage.chat.title ?: statusMessage.chat.userName ?: (statusMessage.chat.firstName ?: "") + " " + (statusMessage.chat.lastName ?: "")
        println("Servicing request by $from: $input")
        val handler = EditMessageResultHandler(absSender, statusMessage)
        getResult(input, handler::updateMessage)
    }

    fun AbsSender.sendSimpleMessage(chatId: Long, message: String): Message {
        return execute(SendMessage(chatId, message).disableWebPagePreview())
    }

    private fun getResult(input: String, statusCallback: (DownloadStatus) -> Unit = {}) {
        statusCallback(GettingInfoDownloadStatus)
        val info = try {
            getInfo(InfoOptions(input))
        } catch (e: Exception) {
            statusCallback(FailedGettingInfoStatus(input))
            return
        }

        val maximumDuration = 500
        if (info.duration > maximumDuration) {
            statusCallback(DownloadTooLongFailure(info, maximumDuration))
            return
        }

        statusCallback(DownloadingDownloadStatus(info))

        try {
            val file = downloadFile(DownloadOptions(info.url))
            statusCallback(CompletedDownloadStatus(info, file))
        } catch (e: Exception) {
            statusCallback(FailedDownloadStatus(info))
        }
    }
}