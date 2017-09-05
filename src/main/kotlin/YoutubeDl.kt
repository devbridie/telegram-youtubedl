import org.apache.commons.exec.*
import org.apache.commons.io.output.ByteArrayOutputStream
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val downloadsFolder = File("downloads").apply {
    mkdirs()
}

data class DownloadOptions(
        val input: String
)

fun download(options: DownloadOptions, complete: (file: File) -> Unit, failed: () -> Unit) {
    val stream = ByteArrayOutputStream()
    DefaultExecutor().setDownloadsCwd().pipeOutput(stream).execute(youtubedl {
        with(options) {
            argument("-x")
            argument("--default-search")
            argument("ytsearch")
            argument("--audio-format")
            argument("mp3")
            argument("--add-metadata")
            argument(options.input)
        }
    }, {
        val consoleOutput = stream.toString(Charset.forName("UTF-8"))
        val infoFilePattern = Pattern.compile("\\[ffmpeg] Destination: (.+?)\n")
        val infoFileMatcher = infoFilePattern.matcher(consoleOutput)
        infoFileMatcher.find()
        val fileLocation = infoFileMatcher.group(1)
        complete(File(downloadsFolder, fileLocation))
    }, {
        failed()
    })
}

typealias Seconds = Int
data class VideoInformation(val length: Seconds, val fulltitle: String)
data class InfoOptions(val input: String)

fun info(options: InfoOptions, callback: (VideoInformation) -> Unit, failed: () -> Unit) {
    val stream = ByteArrayOutputStream()
    DefaultExecutor().setDownloadsCwd().pipeOutput(stream).execute(
            youtubedl {
                argument("--write-info-json")
                argument("--skip-download")
                argument("--default-search")
                argument("ytsearch")
                argument(options.input)
            },
            {
                val consoleOutput = stream.toString(Charset.forName("UTF-8"))
                val infoFilePattern = Pattern.compile("Writing video description metadata as JSON to: (.*)\n")
                val infoFileMatcher = infoFilePattern.matcher(consoleOutput)
                infoFileMatcher.find()
                val fileLocation = infoFileMatcher.group(1)
                val information = File(downloadsFolder, fileLocation).readText()
                val json = JSONObject(information)
                val info = VideoInformation(length = json.getInt("duration"), fulltitle = json.getString("fulltitle"))
                callback(info)
            },
            { e ->
                val information = stream.toString(Charset.defaultCharset())
                println("information = $information")
                failed()
            }
    )
}

private fun CommandLine.argument(argument: String) {
    this.addArgument(argument, false)
}

private fun youtubedl(f: CommandLine.() -> Unit): CommandLine {
    val commandLine = CommandLine("youtube-dl")
    f.invoke(commandLine)
    return commandLine
}

fun DefaultExecutor.mute(): DefaultExecutor {
    return this.apply {
        streamHandler = PumpStreamHandler(null, null, null)
    }
}

fun DefaultExecutor.setDownloadsCwd(): DefaultExecutor {
    return this.apply {
        workingDirectory = downloadsFolder
    }
}

fun DefaultExecutor.pipeOutput(stream: ByteArrayOutputStream): DefaultExecutor {
    this.streamHandler = PumpStreamHandler(stream)
    return this
}


fun DefaultExecutor.execute(command: CommandLine, complete: (exitValue: Int) -> Unit, fail: (e: ExecuteException) -> Unit) {
    this.execute(command, object : ExecuteResultHandler {
        override fun onProcessFailed(e: ExecuteException) {
            e.printStackTrace()
            fail(e)
        }

        override fun onProcessComplete(exitValue: Int) {
            complete(exitValue)
        }
    })
}