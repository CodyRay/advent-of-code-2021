fun main() {
    println(depthIncreasing(parseDepths(readPuzzleInputLines("day01"))))
    println(windowedDepthIncreasing(parseDepths(readPuzzleInputLines("day01"))))
}

fun depthIncreasing(depths: List<Int>): Int = depths.windowed(2).count { (x, y) -> x < y }

fun windowedDepthIncreasing(depths: List<Int>): Int = depthIncreasing(depths.windowed(3).map { it.sum() })

fun parseDepths(lines: List<String>): List<Int> = lines.map { it.toInt() }