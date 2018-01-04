package com.devbridie.telegramyoutubedl.youtubedl.wrappers

import com.devbridie.telegramyoutubedl.youtubedl.YoutubeDlExecutor
import com.devbridie.telegramyoutubedl.youtubedl.argument

fun getYoutubeDlVersion(): String {
    val (_, output, _) = YoutubeDlExecutor().execute {
        argument("--version")
    }
    return output.trim()
}