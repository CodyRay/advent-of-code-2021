fun String.splitWhitespace() = split("""\s+""".toRegex()).filter { it.isNotBlank() }


fun String.splitGroupedNewlines() = split("\n\n")

fun List<String>.toInts() = map { it.toInt() }
fun String.toIntsFromCommaSeparated() = split(',').toInts()