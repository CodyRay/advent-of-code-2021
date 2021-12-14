@file:Suppress("PackageDirectoryMismatch")

package day13

import org.junit.Test
import readPuzzleInputText
import splitGroupedNewlines
import kotlin.test.assertEquals

val example1 = """
    6,10
    0,14
    9,10
    0,3
    10,4
    4,11
    6,0
    6,12
    4,1
    0,13
    10,12
    3,4
    3,0
    8,4
    1,10
    2,14
    8,10
    9,0

    fold along y=7
    fold along x=5
""".trimIndent()

// 6 -> 4
// 7 -> 3
// 8 -> 2
// 9 -> 1


data class Fold(val direction: String, val point: Int) {
    fun point(p: Point): Point = when (direction) {
        "x" -> Pair(if (p.first > point) 2 * point - p.first else p.first, p.second)
        "y" -> Pair(p.first, if (p.second > point) 2 * point - p.second else p.second)
        else -> error("bad direction")
    }
}

typealias Point = Pair<Int, Int>

fun parse(input: String): Pair<List<Point>, List<Fold>> {
    val (ps, fs) = input.splitGroupedNewlines().map { it.trim().lines() }.let { Pair(it.first(), it.last()) }
    val points = ps.map { Pair(it.substringBefore(',').toInt(), it.substringAfter(',').toInt()) }
    val folds = fs.map {
        val (dir, pointStr) = """fold along ([x|y])=(\d+)""".toRegex().find(it.trim())!!.destructured
        Fold(dir, pointStr.toInt())
    }
    return Pair(points, folds)
}

fun List<Fold>.points(ps: List<Point>) = ps.map { fold(it) { point, fold -> fold.point(point) } }

fun printPoints(points: Set<Point>) {
    val maxX = points.maxOf { it.first }
    val maxY = points.maxOf { it.second }
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            if (Point(x, y) in points)
                print('#')
            else
                print(' ')
        }
        println()
    }
}

class Day13 {
    @Test
    fun main() {
        val (points, folds) = parse(readPuzzleInputText("Day13"))
        val part1 = points.map { folds.first().point(it) }.toSet().count()
        println("Day 13, Part 1: $part1")
        //assertEquals(, part1)
        val part2 = folds.points(points).toSet()
        println("Day 13, Part 2:")
        printPoints(part2)
    }

    @Test
    fun `test parse`() {
        parse(example1)
    }

    @Test
    fun `test solution`() {
        val fold1 = Fold("y", 7)
        val fold2 = Fold("x", 5)
        assertEquals(Pair(4, 4), fold2.point(fold1.point(Pair(6, 10))))
        assertEquals(Pair(0, 0), fold2.point(fold1.point(Pair(0, 14))))
        assertEquals(Pair(1, 4), fold2.point(fold1.point(Pair(9, 10))))
        assertEquals(Pair(0, 3), fold2.point(fold1.point(Pair(0, 3))))
        assertEquals(Pair(0, 4), fold2.point(fold1.point(Pair(10, 4))))
        assertEquals(Pair(4, 3), fold2.point(fold1.point(Pair(4, 11))))
        assertEquals(Pair(4, 0), fold2.point(fold1.point(Pair(6, 0))))
        assertEquals(Pair(4, 2), fold2.point(fold1.point(Pair(6, 12))))
        assertEquals(Pair(4, 1), fold2.point(fold1.point(Pair(4, 1))))
        assertEquals(Pair(0, 1), fold2.point(fold1.point(Pair(0, 13))))
        assertEquals(Pair(0, 2), fold2.point(fold1.point(Pair(10, 12))))
        assertEquals(Pair(3, 4), fold2.point(fold1.point(Pair(3, 4))))
        assertEquals(Pair(3, 0), fold2.point(fold1.point(Pair(3, 0))))
        assertEquals(Pair(2, 4), fold2.point(fold1.point(Pair(8, 4))))
        assertEquals(Pair(1, 4), fold2.point(fold1.point(Pair(1, 10))))
        assertEquals(Pair(2, 0), fold2.point(fold1.point(Pair(2, 14))))
        assertEquals(Pair(2, 4), fold2.point(fold1.point(Pair(8, 10))))
        assertEquals(Pair(1, 0), fold2.point(fold1.point(Pair(9, 0))))

        assertEquals(
            16,
            listOf(
                Pair(4, 4),
                Pair(0, 0),
                Pair(1, 4),
                Pair(0, 3),
                Pair(0, 4),
                Pair(4, 3),
                Pair(4, 0),
                Pair(4, 2),
                Pair(4, 1),
                Pair(0, 1),
                Pair(0, 2),
                Pair(3, 4),
                Pair(3, 0),
                Pair(2, 4),
                Pair(1, 4),
                Pair(2, 0),
                Pair(2, 4),
                Pair(1, 0),
            ).toSet().count()
        )
        val (points, folds) = parse(example1)
        assertEquals(17, points.map { folds.first().point(it) }.toSet().count())
        assertEquals(16, folds.points(points).toSet().count())
    }
}