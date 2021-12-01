fun main() {
    println(depthIncreasing(parseDepths(readPuzzleInputLines("day01"))))
}

fun depthIncreasing(depths: List<Int>): Int = depths.windowed(2).count { (x, y) -> x < y }

fun parseDepths(lines: List<String>): List<Int> = lines.map { it.toInt() }