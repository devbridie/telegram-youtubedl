package com.devbridie.telegramyoutubedl.youtubedl.wrappers

import com.devbridie.telegramyoutubedl.youtubedl.YoutubeDlExecutor
import com.devbridie.telegramyoutubedl.youtubedl.argument
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.io.EOFException

typealias Seconds = Int
data class VideoInformation(val duration: Seconds, val fulltitle: String, val uploader: String, val url: String)
data class InfoOptions(val input: String)

data class Entry(
        val duration: Int,
        val title: String,
        val uploader: String,
        val id: String
)

val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val entryAdapter = moshi.adapter(Entry::class.java)

fun getInfo(options: InfoOptions): VideoInformation {
    val (_, consoleOutput, _) = YoutubeDlExecutor().execute {
        argument("--dump-json")
        argument("--skip-download")
        argument("--default-search")
        argument("ytsearch")
        argument(options.input)
    }

    val entry = try {
        entryAdapter.fromJson(consoleOutput) ?: throw RuntimeException("Unable to parse JSON data from data $consoleOutput")
    } catch (e: EOFException) {
        throw RuntimeException("No results.", e)
    }
    return VideoInformation(
            duration = entry.duration,
            fulltitle = entry.title,
            uploader = entry.uploader,
            url = "https://www.youtube.com/watch?v=${entry.id}"
    )
}