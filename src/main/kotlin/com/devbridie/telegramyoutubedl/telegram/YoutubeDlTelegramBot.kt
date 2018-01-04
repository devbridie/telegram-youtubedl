package com.devbridie.telegramyoutubedl.telegram

import com.devbridie.telegramyoutubedl.youtubedl.DownloadOptions
import com.devbridie.telegramyoutubedl.youtubedl.InfoOptions
import com.devbridie.telegramyoutubedl.youtubedl.download
import com.devbridie.telegramyoutubedl.youtubedl.info
import com.natpryce.konfig.Configuration
import org.telegram.telegrambots.api.methods.send.SendAudio
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.io.EOFException

class YoutubeDlTelegramBotException : Exception {
    constructor (display: String) : super(display)
    constructor (display: String, cause: Exception) : super(display, cause)
}

class YoutubeDlTelegramBot(val configuration: Configuration) : TelegramLongPollingBot(DefaultBotOptions()) {
    override fun onUpdateReceived(update: Update) {
        with(update.message) {
            if (!hasText()) return
            if (text == "/start") {
                sendSimpleMessage(chatId, "Simply send this bot a YouTube URL (e.g. https://www.youtube.com/watch?v=B9v8jLBrvug) or a search term (e.g. ship to wreck)")
                return
            }

            try {
                if (containsYoutubeLink(text)) {
                    val url = extractLink(text)
                    val statusMessage = sendSimpleMessage(chatId, "Getting info from $url...")
                    callYoutubeDl(statusMessage, url)
                } else {
                    val statusMessage = sendSimpleMessage(chatId, "Using search for \"$text\"...")
                    callYoutubeDl(statusMessage, text)
                }
            } catch (e: YoutubeDlTelegramBotException) {
                sendSimpleMessage(chatId, e.message!!)
            }
        }
    }

    private fun sendSimpleMessage(chatId: Long, message: String): Message {
        return sendApiMethod(SendMessage(chatId, message).disableWebPagePreview())
    }

    private fun editSimpleMessage(statusMessage: Message, newMessage: String) {
        sendApiMethod(EditMessageText().apply {
            messageId = statusMessage.messageId
            text = newMessage
            chatId = statusMessage.chatId.toString()
            disableWebPagePreview()
        })
    }

    private fun callYoutubeDl(statusMessage: Message, input: String) {
        val info = try {
            info(InfoOptions(input))
        } catch (e: Exception) {
            if (e is EOFException) {
                throw YoutubeDlTelegramBotException("No results for \"$input\".")
            }
            throw YoutubeDlTelegramBotException("Getting info failed.", e)
        }

        val (length, fullTitle, _, url) = info
        if (length > 500) throw YoutubeDlTelegramBotException("$url Length longer than 500s (${length}s)")
        editSimpleMessage(statusMessage, "Downloading video \"$fullTitle\" (${length}s) from \"$url\"...")

        val outputFile = try {
            download(DownloadOptions(url))
        } catch (e: Exception) {
            throw YoutubeDlTelegramBotException("Download failed for \"$url\".", e)
        }

        sendApiMethod(DeleteMessage(statusMessage.chatId, statusMessage.messageId))
        sendAudio(SendAudio().apply {
            setNewAudio(outputFile)
            setChatId(statusMessage.chatId)
            setMetaFromString(fullTitle)
        })
    }

    override fun getBotToken() = configuration[BotConfiguration.token]

    override fun getBotUsername() = configuration[BotConfiguration.name]
}