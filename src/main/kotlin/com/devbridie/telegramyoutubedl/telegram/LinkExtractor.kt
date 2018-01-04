package com.devbridie.telegramyoutubedl.telegram
import java.util.regex.Pattern

val regex = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" +
        "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" +
        "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)" // ¯\_(ツ)_/¯
val youtubePattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)

fun containsYoutubeLink(text: String): Boolean {
    return youtubePattern.matcher(text).matches()
}

fun extractLink(text: String): String {
    if (!containsYoutubeLink(text)) throw RuntimeException("$text does not contain a link")
    val matcher = youtubePattern.matcher(text)
    matcher.find()
    return text.substring(matcher.start(0), matcher.end())
}