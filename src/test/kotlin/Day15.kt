@file:Suppress("PackageDirectoryMismatch")

package day15

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

typealias RiskMap = List<List<Int>>

operator fun RiskMap.get(point: Point) = this[point.y][point.x]
fun RiskMap.start() = Point(this, 0, 0)
fun RiskMap.end() = Point(this, first().lastIndex, lastIndex)
data class Point(val map: RiskMap, val x: Int, val y: Int) {
    override fun toString(): String {
        return "(${x},${y})"
    }
}

data class RiskPath(val seen: Set<Point>, val lastPoint: Point)

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

fun findLowestRiskPath(map: RiskMap, start: Point = map.start()): Int {
    val queue = mutableListOf(Pair(0, RiskPath(setOf(start), start)))
    while (queue.first().second.lastPoint != map.end()) {
        val (riskLevel, point) = queue.removeFirst()
        val visiblePoints = point.lastPoint.directions()
        queue.priorityEnqueueAll(visiblePoints
            .filter { it !in point.seen }
            .map {
                Pair(riskLevel + map[it], RiskPath(point.seen.union(visiblePoints), it))
            })
    }
    return queue.removeFirst().first
}

fun memoizedFindLowestRiskPath(map: RiskMap, start: Point = map.start()): Int {
    val memory = mutableMapOf(map.end() to 0)

    fun lowestRisk(start: Point): Pair<Int, Int> {
        val queue = mutableListOf(Pair(0, RiskPath(setOf(start), start)))
        var iterations = 0
        while (queue.first().second.lastPoint !in memory) {
            iterations += 1
            val (riskLevel, point) = queue.removeFirst()
            val adj = point.lastPoint.directions().filter { it !in point.seen }.sortedByDescending { it.map[it] }

            queue.priorityEnqueueAll(adj
                .map {
                    Pair(riskLevel + map[it] + (memory[it] ?: 0), RiskPath(point.seen.union(adj), it))
                })
        }
        return Pair(queue.removeFirst().first, iterations)
    }

    val populateMemoryQueue = map.end().directions().sortedByDescending { it.map[it] }.toMutableList()
    while (populateMemoryQueue.isNotEmpty()) {
        val point = populateMemoryQueue.removeLast()
        if (point !in memory) {
            val (riskLevel, iterations) = lowestRisk(point)
            memory[point] = riskLevel
            assertNotEquals(0, memory[point])
            println("$point to ${memory[point]} in $iterations iterations")
            populateMemoryQueue.addAll(point.directions().sortedByDescending { it.map[it] })
        }
    }
    return memory[map.start()]!!
}

fun altMemoizedFindLowestRiskPath(map: RiskMap, start: Point = map.start()): Int {
    val memory = mutableMapOf(map.end() to 0)

    fun lowestRisk(start: Point): Triple<Int, List<Point>, Int> {
        val queue = mutableListOf(Pair(0, listOf(start)))
        var iterations = 0
        while (queue.first().second.last() !in memory) {
            iterations += 1
            val (riskLevel, point) = queue.removeFirst()
            val adj = point.last().directions().filter { it !in point }

            queue.priorityEnqueueAll(adj
                .map {
                    Pair(riskLevel + map[it] + (memory[it] ?: 0), point.plus(it))
                })
        }
        val (riskLevel, path) = queue.removeFirst()
        return Triple(riskLevel, path, iterations)
    }

    val populateMemoryQueue = map.end().directions().toMutableList()

    fun record(point: Point, riskLevel: Int, iterations: Int) {
        memory[point] = riskLevel
        assertNotEquals(0, memory[point])
        println("$point to ${memory[point]} in $iterations iterations")
        populateMemoryQueue.addAll(point.directions())
    }

    while (populateMemoryQueue.isNotEmpty()) {
        val point = populateMemoryQueue.removeFirst()
        if (point !in memory) {
            var (riskLevel, path, iterations) = lowestRisk(point)
            do {
                record(path.first(), riskLevel, iterations)
                iterations = 0
                riskLevel -= map[path.get(1)]
                path = path.drop(1)
            } while (path.size > 1)
        }
    }
    return memory[map.start()]!!
}

fun finalAttempt(map: RiskMap): Int {
    val memory = mutableMapOf(map.end() to 0)

    fun lowestRisk(start: Point): Triple<Int, List<Point>, Int> {
        val queue = mutableListOf(Triple(0, 0, listOf(start)))
        var minimum = Triple(Int.MAX_VALUE, Int.MAX_VALUE - 10, listOf<Point>())
        var iterations = 0
        while (queue.isNotEmpty()) {
            iterations += 1
            val (riskLevel, pointCount, points) = queue.removeLast()
            if (riskLevel < minimum.first && pointCount < (minimum.second + 10)) {
                if (points.last() in memory) {
                    minimum = Triple(riskLevel, pointCount, points)
                } else {
                    queue.addAll(points.last()
                        .directions()
                        .filter { it !in points }
                        // .sortedByDescending { it.map[it] }
                        .map { Triple(riskLevel + map[it] + (memory[it] ?: 0), pointCount + 1, points.plus(it)) })
                }
            }
        }
        return Triple(minimum.first, minimum.third, iterations)
    }

    val populateMemoryQueue = map.end().directions().toMutableList()

    fun record(point: Point, riskLevel: Int, iterations: Int) {
        memory[point] = riskLevel
        assertNotEquals(0, memory[point])
        println("$point to ${memory[point]} in $iterations iterations")
        populateMemoryQueue.addAll(point.directions())
    }

    while (populateMemoryQueue.isNotEmpty()) {
        val point = populateMemoryQueue.removeFirst()
        if (point !in memory) {
            var (riskLevel, path, iterations) = lowestRisk(point)
            do {
                record(path.first(), riskLevel, iterations)
                iterations = 0
                riskLevel -= map[path[1]]
                path = path.drop(1)
            } while (path.size > 1)
        }
    }
    return memory[map.start()]!!
}

fun npMemoization(map: RiskMap): Int {
    val success = mutableMapOf(map.end() to 0)
    val deadEnds = mutableSetOf<Pair<Point, Point>>()

    val populateMemoryQueue = map.end().directions().toMutableList()
    while (populateMemoryQueue.isNotEmpty()) {
        val start = populateMemoryQueue.removeFirst()
        if (start in success) {
            continue
        }
        val queue = mutableListOf(Pair(0, listOf(start)))
        while (queue.first().second.last() !in success) {
            val (riskLevel, point) = queue.removeFirst()
            val visiblePoints = point.last().directions()
            queue.priorityEnqueueAll(visiblePoints
                .filter { it !in point }
                .filter { Pair(point.last(), it) !in deadEnds }
                .map {
                    Pair(riskLevel + map[it] + (success[it] ?: 0), point.plus(it))
                })
        }
        success[start] = queue.first().first
        queue.drop(1).forEach { (_, deadPath) ->
            deadPath.zipWithNext().forEach {
                deadEnds.add(it)
            }
        }
        populateMemoryQueue.addAll(start.directions())
    }
    return success[map.start()]!!
}

fun RiskMap.allPoints() = indices.flatMap { y -> first().map { x -> Point(this, x, y) } }

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
        assertEquals(40, dijkstras(exampleX))
    }
}
