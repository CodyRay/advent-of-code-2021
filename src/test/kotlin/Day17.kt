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

fun findTrajectories(range: Range): List<Pair<Int, Int>> {
    val world = range.world()
    val xOptions = world.x.filter { x -> xRange(x).overlaps(range.x) }
    val yOptions = world.y.flatMap { listOf(-it, it) }.distinct().sortedDescending()
    return yOptions.flatMap { y -> xOptions.map { x -> Pair(x, y) } }.filter { trajectory ->
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
        val trajectories = findTrajectories(parsed)
        val part1 = yMax(trajectories.first().second)
        println("Day 17, Part 1: $part1")
        assertEquals(11175, part1)
        val part2 = trajectories.count()
        println("Day 17, Part 2: $part2")
        assertEquals(3540, part2)
    }

    @Test
    fun `test solution`() {
        assertEquals(Range(0..30, -10..0), Range(20..30, -10..-5).world())
        val trajectories = findTrajectories(parseRange("target area: x=20..30, y=-10..-5"))
        assertEquals(Pair(6, 9), trajectories.first())
        assertEquals(45, yMax(trajectories.first().second))

        assertEquals(
            listOf(
                Pair(23, -10), Pair(25, -9), Pair(27, -5), Pair(29, -6), Pair(22, -6), Pair(21, -7), Pair(9, 0),
                Pair(27, -7), Pair(24, -5), Pair(25, -7), Pair(26, -6), Pair(25, -5), Pair(6, 8), Pair(11, -2),
                Pair(20, -5), Pair(29, -10), Pair(6, 3), Pair(28, -7), Pair(8, 0), Pair(30, -6), Pair(29, -8),
                Pair(20, -10), Pair(6, 7), Pair(6, 4), Pair(6, 1), Pair(14, -4), Pair(21, -6), Pair(26, -10),
                Pair(7, -1), Pair(7, 7), Pair(8, -1), Pair(21, -9), Pair(6, 2), Pair(20, -7), Pair(30, -10),
                Pair(14, -3), Pair(20, -8), Pair(13, -2), Pair(7, 3), Pair(28, -8), Pair(29, -9), Pair(15, -3),
                Pair(22, -5), Pair(26, -8), Pair(25, -8), Pair(25, -6), Pair(15, -4), Pair(9, -2), Pair(15, -2),
                Pair(12, -2), Pair(28, -9), Pair(12, -3), Pair(24, -6), Pair(23, -7), Pair(25, -10), Pair(7, 8),
                Pair(11, -3), Pair(26, -7), Pair(7, 1), Pair(23, -9), Pair(6, 0), Pair(22, -10), Pair(27, -6),
                Pair(8, 1), Pair(22, -8), Pair(13, -4), Pair(7, 6), Pair(28, -6), Pair(11, -4), Pair(12, -4),
                Pair(26, -9), Pair(7, 4), Pair(24, -10), Pair(23, -8), Pair(30, -8), Pair(7, 0), Pair(9, -1),
                Pair(10, -1), Pair(26, -5), Pair(22, -9), Pair(6, 5), Pair(7, 5), Pair(23, -6), Pair(28, -10),
                Pair(10, -2), Pair(11, -1), Pair(20, -9), Pair(14, -2), Pair(29, -7), Pair(13, -3), Pair(23, -5),
                Pair(24, -8), Pair(27, -9), Pair(30, -7), Pair(28, -5), Pair(21, -10), Pair(7, 9), Pair(6, 6),
                Pair(21, -5), Pair(27, -10), Pair(7, 2), Pair(30, -9), Pair(21, -8), Pair(22, -7), Pair(24, -9),
                Pair(20, -6), Pair(6, 9), Pair(29, -5), Pair(8, -2), Pair(27, -8), Pair(30, -5), Pair(24, -7),
            ).toSet(), trajectories.toSet()
        )
        assertEquals(112, trajectories.count())
    }
}
