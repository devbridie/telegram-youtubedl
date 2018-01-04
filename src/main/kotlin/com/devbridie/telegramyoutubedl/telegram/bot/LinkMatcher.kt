package com.devbridie.telegramyoutubedl.telegram.bot

import java.util.regex.Pattern

val regex = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" +
        "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" +
        "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)" // ¯\_(ツ)_/¯
val linkPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)

fun containsLink(text: String): Boolean {
    return linkPattern.matcher(text).matches()
}

fun extractLink(text: String): String {
    if (!containsLink(text)) throw RuntimeException("$text does not contain a link")
    val matcher = linkPattern.matcher(text)
    matcher.find()
    return text.substring(matcher.start(0), matcher.end())
}