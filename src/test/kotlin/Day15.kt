@file:Suppress("PackageDirectoryMismatch")

package day15

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

typealias RiskMap = List<List<Int>>

operator fun RiskMap.get(point: Point) = this[point.y][point.x]
fun RiskMap.start() = Point(this, 0, 0)
fun RiskMap.end() = Point(this, first().lastIndex, lastIndex)
data class Point(val map: RiskMap, val x: Int, val y: Int) {
    override fun toString(): String {
        return "(${x},${y})"
    }
}

fun Point.directions(): Set<Point> = buildSet {
    if ((y - 1) in map.indices) add(Point(map, x, y - 1))
    if ((x - 1) in map[y].indices) add(Point(map, x - 1, y))
    if ((y + 1) in map.indices) add(Point(map, x, y + 1))
    if ((x + 1) in map[y].indices) add(Point(map, x + 1, y))
}

fun <T> MutableList<Pair<Int, T>>.priorityEnqueue(value: Pair<Int, T>) = this.also {
    add(binarySearch { it.first - value.first }.let { if (it < 0) -(it + 1) else it }, value)
}

fun <T> MutableList<Pair<Int, T>>.priorityEnqueueAll(values: List<Pair<Int, T>>) = this.also {
    values.forEach { priorityEnqueue(it) }
}

fun dijkstras(map: RiskMap): Int {
    val queue = mutableListOf(Pair(0, map.start()))
    val visited = mutableSetOf<Point>()
    while (queue.first().second != map.end()) {
        val (riskLevel, point) = queue.removeFirst()
        if (point !in visited) {
            queue.priorityEnqueueAll(point.directions()
                .map { Pair(riskLevel + map[it], it) })
            visited.add(point)
        }
    }
    return queue.first().first
}

fun List<String>.parse(): RiskMap = map { line -> line.map { it.digitToInt() } }


class Day14 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day15").parse()
        val part1 = dijkstras(parsed)
        println("Day 15, Part 1: $part1")
        assertEquals(717, part1)
        val part2 = 2
        println("Day 15, Part 2: $part2")
        //assertEquals(, part2)
    }

    @Test
    fun `test priority queue`() {
        val queue = mutableListOf(Pair(5, ""))
        assertEquals(listOf(Pair(0, ""), Pair(5, "")), queue.priorityEnqueue(Pair(0, "")))
        assertEquals(listOf(Pair(0, ""), Pair(0, ""), Pair(5, "")), queue.priorityEnqueue(Pair(0, "")))
        assertEquals(listOf(Pair(0, ""), Pair(0, ""), Pair(3, ""), Pair(5, "")), queue.priorityEnqueue(Pair(3, "")))
        assertEquals(
            listOf(Pair(0, ""), Pair(0, ""), Pair(3, ""), Pair(4, ""), Pair(5, "")),
            queue.priorityEnqueue(Pair(4, ""))
        )
        assertEquals(
            listOf(Pair(0, ""), Pair(0, ""), Pair(3, ""), Pair(4, ""), Pair(5, ""), Pair(6, "")),
            queue.priorityEnqueue(Pair(6, ""))
        )
    }

    @Test
    fun `test solution`() {
        val example = listOf(
            "1163751742",
            "1381373672",
            "2136511328",
            "3694931569",
            "7463417111",
            "1319128137",
            "1359912421",
            "3125421639",
            "1293138521",
            "2311944581"
        ).parse()

        val example0 = listOf(
            "1"
        ).parse()
        val example1 = listOf(
            "21",
            "81"
        ).parse()
        val example2 = listOf(
            "639",
            "521",
            "581"
        ).parse()
        assertEquals(40, dijkstras(example))
    }
}
