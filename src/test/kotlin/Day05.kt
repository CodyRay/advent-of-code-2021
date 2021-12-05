@file:Suppress("PackageDirectoryMismatch")

package day05

import org.junit.Test
import readPuzzleInputLines
import kotlin.math.abs
import kotlin.test.assertEquals

fun pointCount(p1: Point, p2: Point) = maxOf(abs(p1.x - p2.x), abs(p1.y - p2.y)) + 1
fun oneDirectionalRange(start: Int, end: Int, count: Int) = if (start == end) {
    Array(count) { start }.toList()
} else if (start < end) {
    start.rangeTo(end).toList()
} else {
    start.downTo(end).toList()
}

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    infix fun rangeTo(end: Point): List<Point> {
        val count = pointCount(this, end)
        return oneDirectionalRange(x, end.x, count)
            .zip(oneDirectionalRange(y, end.y, count))
            .map { (x, y) -> Point(x, y) }
    }

    override fun compareTo(other: Point): Int {
        return (y - other.y) * 10 + (x - other.x)
    }
}

data class Line(val start: Point, val end: Point) {
    fun allPoints() = start rangeTo end
    fun straight() = start.x == end.x || start.y == end.y
}

fun parseLine(lineString: String): Line {
    val (x1, y1, x2, y2) = """^(\d+),(\d+) -> (\d+),(\d+)""".toRegex().find(lineString)!!.destructured
    return Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
}

fun List<Line>.countDangerousAreas() = flatMap { it.allPoints() }
    .groupBy { it }
    .values
    .count { it.size >= 2 }

fun part1(lines: List<Line>) = lines
    .filter { it.straight() }
    .countDangerousAreas()

fun part2(lines: List<Line>) = lines
    .countDangerousAreas()

class Day05 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day05").map { parseLine(it) }
        val part1 = part1(parsed)
        println("Day 5, Part 1: $part1")
        assertEquals(5169, part1)
        val part2 = part2(parsed)
        println("Day 5, Part 2: $part2")
        assertEquals(22083, part2)
    }

    @Test
    fun `test parsing`() {
        assertEquals(
            listOf(
                Line(Point(0, 9), Point(5, 9)),
                Line(Point(8, 0), Point(0, 8)),
                Line(Point(9, 4), Point(3, 4)),
                Line(Point(2, 2), Point(2, 1)),
                Line(Point(7, 0), Point(7, 4)),
                Line(Point(6, 4), Point(2, 0)),
                Line(Point(0, 9), Point(2, 9)),
                Line(Point(3, 4), Point(1, 4)),
                Line(Point(0, 0), Point(8, 8)),
                Line(Point(5, 5), Point(8, 2)),
            ), listOf(
                "0,9 -> 5,9",
                "8,0 -> 0,8",
                "9,4 -> 3,4",
                "2,2 -> 2,1",
                "7,0 -> 7,4",
                "6,4 -> 2,0",
                "0,9 -> 2,9",
                "3,4 -> 1,4",
                "0,0 -> 8,8",
                "5,5 -> 8,2",
            ).map { parseLine(it) }
        )
    }

    @Test
    fun `test solution`() {
        val exampledata =
            listOf(
                Line(Point(0, 9), Point(5, 9)),
                Line(Point(8, 0), Point(0, 8)),
                Line(Point(9, 4), Point(3, 4)),
                Line(Point(2, 2), Point(2, 1)),
                Line(Point(7, 0), Point(7, 4)),
                Line(Point(6, 4), Point(2, 0)),
                Line(Point(0, 9), Point(2, 9)),
                Line(Point(3, 4), Point(1, 4)),
                Line(Point(0, 0), Point(8, 8)),
                Line(Point(5, 5), Point(8, 2)),
            )
        assertEquals(
            listOf(Point(0, 9), Point(1, 9), Point(2, 9), Point(3, 9), Point(4, 9), Point(5, 9)),
            exampledata.first().allPoints()
        )
        assertEquals(6, exampledata.count { it.straight() })
        assertEquals(
            listOf(Point(9, 4), Point(8, 4), Point(7, 4), Point(6, 4), Point(5, 4), Point(4, 4), Point(3, 4)),
            Line(Point(9, 4), Point(3, 4)).allPoints()
        )
        assertEquals(26, exampledata.filter { it.straight() }.flatMap { it.allPoints() }.count())
        assertEquals(5, part1(exampledata))
        assertEquals(12, part2(exampledata))
    }

    private fun printMap(exampledata: List<Line>) {
        exampledata
            .flatMap { it.allPoints() }
            .groupBy { it.y }
            .mapValues { it.value.groupBy { p -> p.x }.mapValues { entry -> entry.value.size }.toSortedMap() }
            .toSortedMap()
            .forEach { (y, xs) ->
                println("$y: ${xs.map { (x, c) -> "[$x]=$c" }}")
            }
    }
}