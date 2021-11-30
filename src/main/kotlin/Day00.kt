fun main() {
    println(readyToAdventOfCode(readPuzzleInputLines("day00").first()))
}

fun readyToAdventOfCode(input: String): Boolean {
    return input.toBooleanStrict()
}