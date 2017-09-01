import java.util.regex.Pattern

val youtubePattern = Pattern.compile(
        "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
        Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL)

fun String.containsLink(): Boolean {
    return youtubePattern.matcher(this).matches()
}

fun String.extractLink(): String {
    if (!this.containsLink()) throw RuntimeException("${this} does not contain a youtube link")
    val matcher = youtubePattern.matcher(this)
    matcher.find()
    return this.substring(matcher.start(1), matcher.end())
}