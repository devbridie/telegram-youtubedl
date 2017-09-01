
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.api.methods.send.SendAudio
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.Chat
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.User
import org.telegram.telegrambots.bots.*
import org.telegram.telegrambots.bots.commands.BotCommand

class Bot() : TelegramLongPollingCommandBot(DefaultBotOptions()) {

    init {
        register(AudioCommand())
        register(StartCommand())
    }

    override fun processNonCommandUpdate(update: Update?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBotToken(): String {
        return "389656511:AAF_kdApLFm0cRvD2n13Cg3Spl50dNbMhW8"
    }

    override fun getBotUsername(): String {
        return "youtubedlaudiobot"
    }

    override fun clearWebhook() {
        super.clearWebhook();
    }
}

class StartCommand : BotCommand("start", "Start information") {
    override fun execute(sender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        sender.sendMessage(SendMessage(chat.id, "Commands:\n/audio <url>: Sends an mp3 of a video on <url> to this chat."))
    }

}

class AudioCommand : BotCommand("audio", "Downloads audio from a youtube URL.") {
    override fun execute(sender: AbsSender, user: User, chat: Chat, arguments: Array<out String>) {
        if (sender !is DefaultAbsSender) return;
        if (arguments.isEmpty()) {
            sender.sendMessage(SendMessage(chat.id, "Try /audio <url>"))
            return;
        }

        println("sender = [${sender}], user = [${user}], chat = [${chat}], arguments = [${arguments.joinToString(" ")}]")
        val url = arguments.first()

        val statusMessage = sender.sendMessage(SendMessage(chat.id, "Getting info from $url...").disableWebPagePreview())

        info(InfoOptions(url), { info ->
            sender.editMessageText(EditMessageText().apply {
                messageId = statusMessage.messageId
                text = "Downloading video \"${info.fulltitle}\" (${info.length}s) from $url..."
                chatId = chat.id.toString()
                disableWebPagePreview()
            })
            download(DownloadOptions(url, String.format("%d", System.currentTimeMillis()), true), { file ->
                sender.deleteMessage(DeleteMessage(chat.id, statusMessage.messageId))
                sender.sendAudioCorrect(SendAudio().apply {
                    setNewAudio(file)
                    setChatId(chat.id)
                    val argTitle = arguments.drop(1)
                    if (argTitle.isEmpty()) {
                        setMetaFromString(info.fulltitle)
                    } else {
                        title = argTitle.joinToString(" ")
                    }
                })
            }, {
                sender.sendMessage(SendMessage(chat.id, "Download failed. Sorry :("))
            })
        }, {
            sender.sendMessage(SendMessage(chat.id, "Getting info failed. Sorry :("))
        })
    }
}

fun SendAudio.setMetaFromString(string: String) {
    val meta = TitleMeta.fromString(string);
    meta.artist?.let {
        this.performer = meta.artist;
    }
    this.title = meta.title;
}

data class TitleMeta(val title: String, val artist: String? = null) {
    companion object {
        fun fromString(string: String): TitleMeta {
            val split = string.split("-");
            if (split.size == 1) {
                return TitleMeta(title = string)
            } else {
                return TitleMeta(artist = split.first().trim(), title = split.drop(1).joinToString("-").trim())
            }
        }
    }
}


fun main(args: Array<String>) {
    ApiContextInitializer.init();
    TelegramBotsApi().registerBot(Bot())
}