package com.devbridie.telegramyoutubedl.youtubedl

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

typealias Seconds = Int
data class VideoInformation(val duration: Seconds, val fulltitle: String, val uploader: String, val url: String)
data class InfoOptions(val input: String)

val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val entryAdapter = moshi.adapter(Entry::class.java)

fun info(options: InfoOptions): VideoInformation {
    val commandLine = commandLine("youtube-dl") {
        argument("--dump-json")
        argument("--skip-download")
        argument("--default-search")
        argument("ytsearch")
        argument(options.input)
    }

    val executor = YoutubeDlExecutor(commandLine)
    executor.execute()
    val consoleOutput = executor.getOutputText()

    val entry = entryAdapter.fromJson(consoleOutput) ?: throw RuntimeException("Unable to parse JSON data")
    return VideoInformation(
            duration = entry.duration,
            fulltitle = entry.title,
            uploader = entry.uploader,
            url = "https://www.youtube.com/watch?v=${entry.id}"
    )
}