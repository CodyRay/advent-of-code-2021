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
//data class RiskPath(val visited: List<Point>, val seen: List<Point>) {
//
//}

fun Point.directions(): Set<Point> = buildSet {
    if ((y - 1) in map.indices) add(Point(map, x, y - 1))
    if ((x - 1) in map[y].indices) add(Point(map, x - 1, y))
    if ((x + 1) in map[y].indices) add(Point(map, x + 1, y))
    if ((y + 1) in map.indices) add(Point(map, x, y + 1))
}

fun <T> MutableList<Pair<Int, T>>.priorityEnqueue(value: Pair<Int, T>) = this.also {
    add(binarySearch { it.first - value.first }.let { if (it < 0) -(it + 1) else it }, value)
}

fun <T> MutableList<Pair<Int, T>>.priorityEnqueueAll(values: List<Pair<Int, T>>) = this.also {
    values.forEach { priorityEnqueue(it) }
}

fun findLowestRiskPath(map: RiskMap, start: Point = map.start()): Int {
    val queue = mutableListOf(Pair(0, start))
    while (queue.first().second != map.end()) {
        val (riskLevel, point) = queue.removeFirst()
        queue.priorityEnqueueAll(point.directions()
            .map {
                Pair(riskLevel + map[it], it)
            })
    }
    return queue.removeFirst().first
}

fun List<String>.parse(): RiskMap = map { line -> line.map { it.digitToInt() } }


class Day14 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day15").parse()
        val part1 = findLowestRiskPath(parsed)
        println("Day 15, Part 1: $part1")
        //assertEquals(, part1)
        val part2 = 2
        println("Day 15, Part 2: $part2")
        //assertEquals(, part2)
    }

    @Test
    fun `test solution`() {
        val exampleX = listOf(
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
        val example3 = listOf(
            "2421",
            "1639",
            "8521",
            "4581"
        ).parse()
        val example4 = listOf(
            "28137",
            "12421",
            "21639",
            "38521",
            "44581"
        ).parse()
        val example5 = listOf(
            "417111",
            "128137",
            "912421",
            "421639",
            "138521",
            "944581"
        ).parse()
        val example6 = listOf(
            "4931569",
            "3417111",
            "9128137",
            "9912421",
            "5421639",
            "3138521",
            "1944581"
        ).parse()
        val example7 = listOf(
            "36511328",
            "94931569",
            "63417111",
            "19128137",
            "59912421",
            "25421639",
            "93138521",
            "11944581"
        ).parse()
        val example8 = listOf(
            "381373672",
            "136511328",
            "694931569",
            "463417111",
            "319128137",
            "359912421",
            "125421639",
            "293138521",
            "311944581"
        ).parse()
        val example9 = listOf(
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
        assertEquals(40, findLowestRiskPath(exampleX))
    }
}
