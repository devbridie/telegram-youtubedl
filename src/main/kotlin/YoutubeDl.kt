import org.apache.commons.exec.*
import org.apache.commons.io.output.ByteArrayOutputStream
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset

data class DownloadOptions(
        val url: String,
        val id: String,
        val audioOnly: Boolean = false
)

fun download(options: DownloadOptions, complete: (file: File) -> Unit, failed: () -> Unit) {

    DefaultExecutor().mute().execute(youtubedl {
        with(options) {
            if (audioOnly) argument("-x")
            argument("-o")
            argument("download/${id}.%(ext)s")
            argument("--audio-format")
            argument("mp3")
            argument(options.url)
        }
    }, {
        complete(File("download", options.id + ".mp3"))
    }, {
        failed();
    });
}

typealias Seconds = Int;
data class VideoInformation(val length: Seconds, val fulltitle: String)
data class InfoOptions(val url: String)

fun info(options: InfoOptions, callback: (VideoInformation) -> Unit, failed: () -> Unit) {
    val stream = ByteArrayOutputStream()
    DefaultExecutor().pipeOutput(stream).execute(
            youtubedl {
                argument("--print-json")
                argument(options.url)
            },
            {
                val information = stream.toString(Charset.forName("UTF-8"));
                val json = JSONObject(information);
                val info = VideoInformation(length = json.getInt("duration"), fulltitle = json.getString("fulltitle"))
                callback(info)
            },
            { e ->
                val information = stream.toString(Charset.defaultCharset());
                println("information = ${information}")
                failed()
            }
    )
}

private fun CommandLine.argument(argument: String) {
    this.addArgument(argument)
}

private fun youtubedl(f: CommandLine.() -> Unit): CommandLine {
    val commandLine = CommandLine("youtube-dl")
    f.invoke(commandLine)
    return commandLine;
}

fun DefaultExecutor.mute(): DefaultExecutor {
    //this.streamHandler = PumpStreamHandler(null, null, null)
    return this
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