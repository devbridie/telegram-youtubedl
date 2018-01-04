package com.devbridie.telegramyoutubedl.youtubedl

import java.io.File

data class DownloadOptions(val url: String)

fun download(options: DownloadOptions): File {
    val commandLine = commandLine("youtube-dl") {
        argument("--extract-audio")
        argument("--audio-format")
        argument("mp3")
        argument("--no-playlist")
        argument("--restrict-filenames")
        argument("--add-metadata")
        argument("--embed-thumbnail")
        argument(options.url)
    }

    val executor = YoutubeDlExecutor(commandLine)
    executor.execute()
    val consoleOutput = executor.getOutputText()

    val findDestination = Regex("\\[ffmpeg] Destination: (.+?)\n").find(consoleOutput)
    val fileLocation = findDestination?.groups?.get(1)?.value ?: throw RuntimeException("Could not find destination file in output")
    return File(executor.workingDirectory, fileLocation)
}