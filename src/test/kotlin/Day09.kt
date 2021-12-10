@file:Suppress("PackageDirectoryMismatch")

package day09

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

typealias Map = List<List<Int>>
typealias Adjacency = List<List<Set<Pair<Int, Int>>>>

operator fun Map.get(point: Pair<Int, Int>) = this[point.second][point.first]
operator fun Adjacency.get(point: Pair<Int, Int>) = this[point.second][point.first]

class Day09 {
    @Test
    fun main() {
        val parsed = parse(readPuzzleInputLines("Day09"))
        val adjacency = getAdjacency(parsed)
        val part1 = riskLevel(findLocalExtrema(parsed, adjacency))
        println("Day 9, Part 1: $part1")
        assertEquals(548, part1)
        val part2 = combinedLargestBasins(findBasinSizes(parsed, adjacency))
        println("Day 9, Part 2: $part2")
        assertEquals(786048, part2)
    }

    @Test
    fun `test solution part1`() {
        val lines = listOf(
            "2199943210",
            "3987894921",
            "9856789892",
            "8767896789",
            "9899965678",
        )
        assertEquals(listOf(0, 1, 5, 5), findLocalExtrema(parse(lines), getAdjacency(parse(lines))).toList().sorted())
        assertEquals(15, riskLevel(findLocalExtrema(parse(lines), getAdjacency(parse(lines)))))
    }

    fun parse(lines: List<String>): Map {
        val map = lines.map { it.toList().map { char -> char.digitToInt() } }
        return map
    }

    fun findLocalExtrema(map: Map, adjacency: Adjacency) = findLowPoints(map, adjacency).map { map[it] }

    fun findLowPoints(map: Map, adjacency: Adjacency) =
        map.indices.flatMap { y -> map[y].indices.map { Pair(it, y) } }
            .filter { point -> adjacency[point].all { map[it] > map[point] } }

    fun getAdjacency(map: Map): Adjacency =
        map.indices.map { y ->
            map[y].indices.map { Pair(it, y) }.map {
                buildSet {
                    if ((it.second - 1) in map.indices) this.add(Pair(it.first, it.second - 1))
                    if ((it.first - 1) in map[it.second].indices) this.add(Pair(it.first - 1, it.second))
                    if ((it.first + 1) in map[it.second].indices) this.add(Pair(it.first + 1, it.second))
                    if ((it.second + 1) in map.indices) this.add(Pair(it.first, it.second + 1))
                }
            }
        }

    fun findBasinSizes(map: Map, adjacency: Adjacency) = findLowPoints(map, adjacency).map {
        val known = mutableSetOf(it)
        val queue = mutableListOf(it)
        while (queue.isNotEmpty()) {
            for (ap in adjacency[queue.removeFirst()]) {
                if (map[ap] < 9 && ap !in known) {
                    known.add(ap)
                    queue.add(ap)
                }
            }
        }
        known.count()
    }

    fun riskLevel(extrema: List<Int>) = extrema.sumOf { it + 1 }

    fun combinedLargestBasins(basinSizes: List<Int>) = basinSizes.sorted().takeLast(3).reduce { x, y -> x * y }

    @Test
    fun `test solution`() {
        val lines = listOf(
            "2199943210",
            "3987894921",
            "9856789892",
            "8767896789",
            "9899965678",
        )
        assertEquals(listOf(3, 9, 9, 14), findBasinSizes(parse(lines), getAdjacency(parse(lines))).sorted())
        assertEquals(1134, combinedLargestBasins(findBasinSizes(parse(lines), getAdjacency(parse(lines)))))

    }
}