import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.api.methods.send.SendAudio
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot

class Bot : TelegramLongPollingBot(DefaultBotOptions()) {
    override fun onUpdateReceived(update: Update) {
        with(update) {
            if (update.message.hasText()) {
                if (message.text.containsLink()) {
                    val url = message.text.extractLink()
                    val statusMessage = sendApiMethod(SendMessage(message.chatId, "Getting info from $url...").disableWebPagePreview())

                    callYoutubeDl(statusMessage, url)
                } else {
                    val search = message.text
                    val statusMessage = sendApiMethod(SendMessage(message.chatId, "Using search for \"$search\"...").disableWebPagePreview())
                    callYoutubeDl(statusMessage, search)
                }
            }
        }
    }

    private fun Update.callYoutubeDl(statusMessage: Message, input: String) {
        info(InfoOptions(input), { (length, fulltitle) ->
            sendApiMethod(EditMessageText().apply {
                messageId = statusMessage.messageId
                text = "Downloading video \"$fulltitle\" (${length}s) from $input..."
                chatId = message.chatId.toString()
                disableWebPagePreview()
            })
            download(DownloadOptions(input), { file ->
                sendApiMethod(DeleteMessage(message.chatId, statusMessage.messageId))
                sendAudio(SendAudio().apply {
                    setNewAudio(file)
                    setChatId(message.chatId)
                    setMetaFromString(fulltitle)
                })
            }, {
                sendApiMethod(SendMessage(message.chatId, "Download failed. Sorry :("))
            })
        }, {
            sendApiMethod(SendMessage(message.chatId, "Getting info failed. Sorry :("))
        })
    }

    override fun getBotToken(): String {
        return "389656511:AAF_kdApLFm0cRvD2n13Cg3Spl50dNbMhW8"
    }

    override fun getBotUsername(): String {
        return "youtubedlaudiobot"
    }

    override fun clearWebhook() {
        super.clearWebhook()
    }
}

fun SendAudio.setMetaFromString(string: String) {
    val meta = TitleMeta.fromString(string)
    meta.artist?.let {
        this.performer = meta.artist
    }
    this.title = meta.title
}

data class TitleMeta(val title: String, val artist: String? = null) {
    companion object {
        fun fromString(string: String): TitleMeta {
            val split = string.split("-")
            if (split.size == 1) {
                return TitleMeta(title = string)
            } else {
                return TitleMeta(artist = split.first().trim(), title = split.drop(1).joinToString("-").trim())
            }
        }
    }
}


fun main(args: Array<String>) {
    ApiContextInitializer.init()
    TelegramBotsApi().registerBot(Bot())
}