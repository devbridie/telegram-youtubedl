package com.devbridie.telegramyoutubedl.youtubedl.wrappers

import com.devbridie.telegramyoutubedl.youtubedl.YoutubeDlExecutor
import com.devbridie.telegramyoutubedl.youtubedl.argument
import java.io.File

data class DownloadOptions(val url: String)

fun downloadFile(options: DownloadOptions): File {
    val executor = YoutubeDlExecutor()
    val (_, consoleOutput, _) = executor.execute {
        argument("--extract-audio")
        argument("--audio-format")
        argument("mp3")
        argument("--no-playlist")
        argument("--restrict-filenames")
        argument("--add-metadata")
        argument("--embed-thumbnail")
        argument(options.url)
    }
    val findDestination = Regex("\\[ffmpeg] Destination: (.+?)\n").find(consoleOutput)
    val fileLocation = findDestination?.groups?.get(1)?.value ?: throw RuntimeException("Could not find destination file in output")
    return File(executor.workingDirectory, fileLocation)
}