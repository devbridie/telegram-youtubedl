package com.devbridie.telegramyoutubedl.telegram

import org.telegram.telegrambots.api.methods.send.SendAudio


fun SendAudio.setMetaFromString(string: String) {
    val meta = TitleMetadata.fromString(string)
    meta.artist?.let {
        this.performer = meta.artist
    }
    this.title = meta.title
}

data class TitleMetadata(val title: String, val artist: String? = null) {
    companion object {
        fun fromString(string: String): TitleMetadata {
            val split = string.split("-")
            return if (split.size == 1) {
                TitleMetadata(title = string)
            } else {
                TitleMetadata(artist = split.first().trim(), title = split.drop(1).joinToString("-").trim())
            }
        }
    }
}