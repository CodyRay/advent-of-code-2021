@file:Suppress("PackageDirectoryMismatch")

package day11

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

fun parse(input: List<String>): List<List<Int>> {
    return input.map { line -> line.toList().map { it.digitToInt() } }
}

typealias Map = List<List<Int>>
typealias MutMap = List<MutableList<Int>>
typealias Adjacency = List<List<Set<Pair<Int, Int>>>>

operator fun Map.get(point: Pair<Int, Int>) = this[point.second][point.first]
operator fun Adjacency.get(point: Pair<Int, Int>) = this[point.second][point.first]

operator fun MutMap.set(point: Pair<Int, Int>, value: Int) {
    this[point.second][point.first] = value
}

fun allPoints(map: Map): List<Pair<Int, Int>> {
    return map.indices.flatMap { y -> map[y].indices.map { Pair(it, y) } }
}

fun getAdjacency(map: Map): Adjacency =
    map.indices.map { y ->
        map[y].indices.map { Pair(it, y) }.map {
            buildSet {
                if ((it.second - 1) in map.indices)
                    add(Pair(it.first, it.second - 1))
                if ((it.first - 1) in map[it.second].indices)
                    add(Pair(it.first - 1, it.second))
                if ((it.first + 1) in map[it.second].indices)
                    add(Pair(it.first + 1, it.second))
                if ((it.second + 1) in map.indices)
                    add(Pair(it.first, it.second + 1))
                if ((it.second + 1) in map.indices && (it.first + 1) in map[it.second].indices)
                    add(Pair(it.first + 1, it.second + 1))
                if ((it.second + 1) in map.indices && (it.first - 1) in map[it.second].indices)
                    add(Pair(it.first - 1, it.second + 1))
                if ((it.second - 1) in map.indices && (it.first + 1) in map[it.second].indices)
                    add(Pair(it.first + 1, it.second - 1))
                if ((it.second - 1) in map.indices && (it.first - 1) in map[it.second].indices)
                    add(Pair(it.first - 1, it.second - 1))
            }
        }
    }

fun step(map: Map, adjacency: Adjacency, numSteps: Int): Pair<Int, MutMap> {
    val mMap: MutMap = map.map { it.toMutableList() }
    var flashes = 0
    repeat(numSteps) {
        allPoints(map).forEach { p -> run { mMap[p] += 1 } }
        while (allPoints(map).any { mMap[it] > 9 }) {
            val flashPoint = allPoints(map).first { mMap[it] > 9 }
            mMap[flashPoint] = 0
            flashes += 1
            adjacency[flashPoint].filter { mMap[it] > 0 }.forEach {
                mMap[it] += 1
            }
        }
    }
    return Pair(flashes, mMap)
}

fun stepTillZero(map: Map, adjacency: Adjacency): Pair<Int, MutMap> {
    val mMap: MutMap = map.map { it.toMutableList() }
    var cycles = 0
    while (allPoints(map).any { mMap[it] != 0 }) {
        cycles += 1
        allPoints(map).forEach { p -> run { mMap[p] += 1 } }
        while (allPoints(map).any { mMap[it] > 9 }) {
            val flashPoint = allPoints(map).first { mMap[it] > 9 }
            mMap[flashPoint] = 0
            adjacency[flashPoint].filter { mMap[it] > 0 }.forEach {
                mMap[it] += 1
            }
        }
    }
    return Pair(cycles, mMap)
}

class Day11 {
    @Test
    fun main() {
        val parsed = parse(readPuzzleInputLines("Day11"))
        val adjacency = getAdjacency(parsed)
        val part1 = step(parsed, adjacency, 100).first
        println("Day 11, Part 1: $part1")
        assertEquals(1603, part1)
        val part2 = stepTillZero(parsed, adjacency).first
        println("Day 11, Part 2: $part2")
        assertEquals(222, part2)
    }

    @Test
    fun `test solution`() {
        val example = parse(
            listOf(
                "5483143223",
                "2745854711",
                "5264556173",
                "6141336146",
                "6357385478",
                "4167524645",
                "2176841721",
                "6882881134",
                "4846848554",
                "5283751526",
            )
        )
        val adjacency = getAdjacency(example)
        assertEquals(
            parse(
                listOf(
                    "6594254334",
                    "3856965822",
                    "6375667284",
                    "7252447257",
                    "7468496589",
                    "5278635756",
                    "3287952832",
                    "7993992245",
                    "5957959665",
                    "6394862637",
                )
            ), step(example, adjacency, 1).second
        )
        assertEquals(
            parse(
                listOf(
                    "8807476555",
                    "5089087054",
                    "8597889608",
                    "8485769600",
                    "8700908800",
                    "6600088989",
                    "6800005943",
                    "0000007456",
                    "9000000876",
                    "8700006848",
                )
            ), step(example, adjacency, 2).second
        )
        assertEquals(
            parse(
                listOf(
                    "0050900866",
                    "8500800575",
                    "9900000039",
                    "9700000041",
                    "9935080063",
                    "7712300000",
                    "7911250009",
                    "2211130000",
                    "0421125000",
                    "0021119000",
                )
            ), step(example, adjacency, 3).second
        )
        assertEquals(
            parse(
                listOf(
                    "0397666866",
                    "0749766918",
                    "0053976933",
                    "0004297822",
                    "0004229892",
                    "0053222877",
                    "0532222966",
                    "9322228966",
                    "7922286866",
                    "6789998766",
                )
            ), step(example, adjacency, 100).second
        )
        assertEquals(1656, step(example, adjacency, 100).first)
        assertEquals(195, stepTillZero(example, adjacency).first)
    }
}