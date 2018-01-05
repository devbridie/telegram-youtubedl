package com.devbridie.telegramyoutubedl.telegram.bot

import com.devbridie.telegramyoutubedl.telegram.*
import com.devbridie.telegramyoutubedl.youtubedl.wrappers.VideoInformation
import org.apache.commons.io.FileUtils
import org.telegram.telegrambots.api.methods.send.SendAudio
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.bots.AbsSender
import java.io.File

class EditMessageResultHandler(val sender: AbsSender, val message: Message) {
    fun updateMessage(downloadStatus: DownloadStatus) {
        when (downloadStatus) {
            GettingInfoDownloadStatus -> {
            }
            is FailedGettingInfoStatus ->
                editSimpleMessage("Failed getting info from (${downloadStatus.input}).")
            is DownloadTooLongFailure ->
                editSimpleMessage("Video (${downloadStatus.info.url}) duration (${downloadStatus.info.duration}s) longer than maximum (${downloadStatus.maximum}s)")
            is DownloadingDownloadStatus ->
                editSimpleMessage("Downloading video (${downloadStatus.info.url}, ${downloadStatus.info.duration}s)...")
            is CompletedDownloadStatus -> {
                val size = FileUtils.byteCountToDisplaySize(downloadStatus.file.length())
                editSimpleMessage("Sending audio ($size)...")
                sendAudio(downloadStatus.file, downloadStatus.info)
                deleteStatusMessage()
            }
            is FailedDownloadStatus ->
                editSimpleMessage("Failed to download video (${downloadStatus.info.url})")
        }
    }

    private fun deleteStatusMessage() {
        sender.execute(DeleteMessage(message.chatId, message.messageId))
    }

    private fun sendAudio(file: File, info: VideoInformation) {
        sender.sendAudio(SendAudio().apply {
            setNewAudio(file)
            setChatId(message.chatId)

            if (info.fulltitle.contains(" - ")) {
                val split = info.fulltitle.split(" - ")
                performer = split[0].trim()
                title = split.drop(1).joinToString(separator = " - ").trim()
            } else {
                title = info.fulltitle
            }
        })
    }

    private fun editSimpleMessage(newMessage: String) {
        sender.execute(EditMessageText().apply {
            messageId = message.messageId
            text = newMessage
            chatId = message.chatId.toString()
            disableWebPagePreview()
        })
    }
}