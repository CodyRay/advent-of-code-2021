@file:Suppress("PackageDirectoryMismatch")

package day17

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

data class Point(val x: Int, val y: Int)

data class Range(val x: IntRange, val y: IntRange) {
    operator fun contains(point: Point) = point.x in x && point.y in y
    fun world() = Range(if (x.first >= 0) 0..x.last else x.first..0, if (y.first >= 0) 0..y.last else y.first..0)
}

fun xSteps(xVelocity: Int) = sequence<Int> {
    var xV = xVelocity
    var x = 0
    while (true) {
        x += xV
        yield(x)
        xV += when {
            xV > 0 -> -1
            xV < 0 -> 1
            else -> 0
        }
    }
}

fun ySteps(yVelocity: Int) = sequence<Int> {
    var yV = yVelocity
    var y = 0
    while (true) {
        y += yV
        yield(y)
        yV -= 1
    }
}

fun xRange(xVelocity: Int) = when {
    xVelocity > 0 -> 0..(xVelocity downTo 1).sumOf { it }
    xVelocity < (-1 downTo xVelocity).sumOf { it } -> -1..0
    else -> 0..0
}

fun yMax(yVelocity: Int) = (yVelocity downTo 1).sumOf { it }

fun steps(xVelocity: Int, yVelocity: Int) = xSteps(xVelocity).zip(ySteps(yVelocity)) { x, y -> Point(x, y) }

fun findTrajectories(range: Range): Pair<Int, Int> {
    val world = range.world()
    val xOptions = world.x.filter { x -> xRange(x).overlaps(range.x) }
    val yOptions = world.y.flatMap { listOf(-it, it) }.sortedDescending()
    return yOptions.flatMap { y -> xOptions.map { x -> Pair(x, y) } }.first { trajectory ->
        steps(trajectory.first, trajectory.second)
            .takeWhile { it.y >= world.y.first }
            .any { it in range }
    }
}

fun IntRange.overlaps(otherRange: IntRange) =
    first in otherRange || last in otherRange || otherRange.first in this || otherRange.last in this

fun parseRange(x: String): Range {
    val (xMin, xMax, yMin, yMax) =
        """^target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)$""".toRegex().find(x)!!.destructured
    return Range(xMin.toInt()..xMax.toInt(), yMin.toInt()..yMax.toInt())
}

class Day17 {
    @Test
    fun main() {
        val parsed = parseRange(readPuzzleInputLines("Day17").first())
        val part1 = yMax(findTrajectories(parsed).second)
        println("Day 17, Part 1: $part1")
        assertEquals(11175, part1)
        val part2 = 2
        println("Day 17, Part 2: $part2")
        // assertEquals(, part2)
    }

    @Test
    fun `test solution`() {
        assertEquals(Range(0..30, -10..0), Range(20..30, -10..-5).world())
        assertEquals(Pair(6, 9), findTrajectories(parseRange("target area: x=20..30, y=-10..-5")))
        assertEquals(45, yMax(findTrajectories(parseRange("target area: x=20..30, y=-10..-5")).second))
    }
}
