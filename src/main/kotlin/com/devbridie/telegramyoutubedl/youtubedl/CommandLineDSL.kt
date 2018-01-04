package com.devbridie.telegramyoutubedl.youtubedl

import org.apache.commons.exec.CommandLine

fun CommandLine.argument(argument: String) {
    this.addArgument(argument, false)
}

fun commandLine(executable: String, f: CommandLine.() -> Unit): CommandLine {
    val commandLine = CommandLine(executable)
    f.invoke(commandLine)
    return commandLine
}
