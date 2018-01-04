package com.devbridie.telegramyoutubedl.youtubedl

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

class YoutubeDlException(
        val errorCode: Int,
        val out: String,
        val err: String
) : RuntimeException("youtube-dl exited with code $errorCode")

class YoutubeDlExecutor(val commandLine: CommandLine) : DefaultExecutor() {
    val outStream = ByteArrayOutputStream()
    val errorStream = ByteArrayOutputStream()
    init {
        streamHandler = PumpStreamHandler(outStream, errorStream)
        workingDirectory = File("downloads").apply {
            mkdirs()
        }
        setExitValues(null)
    }

    fun getOutputText() = String(outStream.toByteArray(), Charset.defaultCharset())
    fun getErrorText() = String(errorStream.toByteArray(), Charset.defaultCharset())

    fun execute(): Int {
        val output = super.execute(commandLine)
        if (output == 0) {
            return output
        } else {
            throw YoutubeDlException(output, getOutputText(), getErrorText())
        }
    }
}