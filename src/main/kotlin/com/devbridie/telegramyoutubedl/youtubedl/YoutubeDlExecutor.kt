package com.devbridie.telegramyoutubedl.youtubedl

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.PumpStreamHandler
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.Charset

data class YoutubeDlExecutorResult(
        val code: Int,
        val out: String,
        val err: String
)

class YoutubeDlExitCodeException(val out: String, val err: String, val result: YoutubeDlExecutorResult) : RuntimeException("youtube-dl exited with code ${result.code}")

class YoutubeDlExecutor {
    private val outStream = ByteArrayOutputStream()
    private val errorStream = ByteArrayOutputStream()

    val workingDirectory = File("downloads").apply {
        mkdirs()
    }

    private val executor = DefaultExecutor().apply {
        streamHandler = PumpStreamHandler(outStream, errorStream)
        workingDirectory = this@YoutubeDlExecutor.workingDirectory
        setExitValues(null)
    }

    private fun getOutputText() = String(outStream.toByteArray(), Charset.defaultCharset())
    private fun getErrorText() = String(errorStream.toByteArray(), Charset.defaultCharset())

    fun execute(f: CommandLine.() -> Unit): YoutubeDlExecutorResult {
        val commandLine = CommandLine("youtube-dl")
        f.invoke(commandLine)
        val code = executor.execute(commandLine)
        val result = YoutubeDlExecutorResult(code, getOutputText(), getErrorText())
        if (code == 0) {
            return result
        } else {
            throw YoutubeDlExitCodeException(result.out, result.err, result)
        }
    }
}

fun CommandLine.argument(argument: String) {
    this.addArgument(argument, false)
}