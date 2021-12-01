fun main() {
    val depths = parseDepths(readPuzzleInputLines("day01"))
    println(depths.windowedIncreasing(1))
    println(depths.windowedIncreasing(3))
}

fun parseDepths(lines: List<String>): List<Int> = lines.map { it.toInt() }

fun List<Int>.windowedIncreasing(size: Int): Int = windowed(size).map { it.sum() }.windowed(2).count { (x, y) -> x < y }
