@file:Suppress("PackageDirectoryMismatch")

package day12

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

val example1 = listOf(
    "start-A",
    "start-b",
    "A-c",
    "A-b",
    "b-d",
    "A-end",
    "b-end",
)

val example2 = listOf(
    "dc-end",
    "HN-start",
    "start-kj",
    "dc-start",
    "dc-HN",
    "LN-dc",
    "HN-end",
    "kj-sa",
    "kj-HN",
    "kj-dc",
)

val example3 = listOf(
    "fs-end",
    "he-DX",
    "fs-he",
    "start-DX",
    "pj-DX",
    "end-zg",
    "zg-sl",
    "zg-pj",
    "pj-he",
    "RW-he",
    "fs-DX",
    "pj-RW",
    "zg-RW",
    "start-pj",
    "he-WI",
    "zg-he",
    "pj-fs",
    "start-RW",
)

typealias CaveSystem = Map<String, Cave>
typealias Path = List<String>

fun parse(input: List<String>): CaveSystem = input.map { it.split('-') }
    .flatMap { listOf(Pair(it.first(), it.last()), Pair(it.last(), it.first())) }
    .groupBy({ it.first }, { it.second })
    .mapValues { Cave(it.key, it.key.first().isUpperCase(), it.value) }

data class Cave(val name: String, val isBig: Boolean, val paths: List<String>)

fun enumeratePaths(caveSystem: CaveSystem, path: Path): List<Path> =
    if (path.last() == "end") {
        listOf(path)
    } else {
        caveSystem[path.last()]!!.paths
            .filter { caveSystem[it]!!.isBig || it !in path }
            .flatMap { enumeratePaths(caveSystem, path.plus(it)) }
    }

class Day12 {
    @Test
    fun main() {
        val parsed = parse(readPuzzleInputLines("Day12"))
        val part1 = enumeratePaths(parsed, listOf("start")).count()
        println("Day 12, Part 1: $part1")
        assertEquals(3450, part1)
        val part2 = 2
        println("Day 12, Part 2: $part2")
        // assertEquals(, part2)
    }

    @Test
    fun `test parse`() {

    }

    @Test
    fun `test solution`() {
        assertEquals(10, enumeratePaths(parse(example1), listOf("start")).count())
        assertEquals(19, enumeratePaths(parse(example2), listOf("start")).count())
        assertEquals(226, enumeratePaths(parse(example3), listOf("start")).count())
    }
}