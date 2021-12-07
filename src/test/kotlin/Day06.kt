@file:Suppress("PackageDirectoryMismatch")

package day06

import org.junit.Test
import readPuzzleInputLines
import toInts
import java.math.BigInteger
import kotlin.test.assertEquals

fun parsed(input: String) = input.split(',').toInts()

fun depthFirstGenerationalCounter(generations: Int, startState: List<Int>): BigInteger {
    val memoization = mutableMapOf<Pair<Int, Int>, BigInteger>()
    return startState.sumOf { countGenerations(memoization, generations, it) }
}

fun countGenerations(memoization: MutableMap<Pair<Int, Int>, BigInteger>, generations: Int, parent: Int): BigInteger {
    val mKey = Pair(generations, parent)
    if (mKey !in memoization) {
        val result = if (generations == 0) {
            1.toBigInteger()
        } else if (parent == 0) {
            countGenerations(memoization, generations - 1, 6) + countGenerations(memoization, generations - 1, 8)
        } else {
            countGenerations(memoization, generations - 1, parent - 1)
        }
        memoization[mKey] = result
    }
    return memoization[mKey]!!
}

fun depthFirstGenerationalLister(generations: Int, startState: List<Int>) =
    startState.flatMap { listGenerations(generations, it) }

fun listGenerations(generations: Int, parent: Int): List<Int> {
    return if (generations == 0) {
        listOf(parent)
    } else if (parent == 0) {
        listOf(listGenerations(generations - 1, 6), listGenerations(generations - 1, 8)).flatten()
    } else {
        listGenerations(generations - 1, parent - 1)
    }
}

class Day06 {
    @Test
    fun main() {
        val input = parsed(readPuzzleInputLines("Day06").single())
        val part1 = depthFirstGenerationalCounter(80, input)
        println("Day 6, Part 1: $part1")
        assertEquals(390011.toBigInteger(), part1)
        val part2 = depthFirstGenerationalCounter(256, input)
        println("Day 6, Part 2: $part2")
        assertEquals(1746710169834.toBigInteger(), part2)
    }

    @Test
    fun `test parsing`() {
        assertEquals(listOf(3, 4, 3, 1, 2), parsed("3,4,3,1,2"))
    }

    @Test
    fun `test solution`() {
        val example = listOf(3, 4, 3, 1, 2)
        assertEquals(listOf(2, 3, 2, 0, 1).sorted(), depthFirstGenerationalLister(1, example).sorted())
        assertEquals(listOf(1, 2, 1, 6, 0, 8).sorted(), depthFirstGenerationalLister(2, example).sorted())
        assertEquals(listOf(0, 1, 0, 5, 6, 7, 8).sorted(), depthFirstGenerationalLister(3, example).sorted())
        assertEquals(listOf(6, 0, 6, 4, 5, 6, 7, 8, 8).sorted(), depthFirstGenerationalLister(4, example).sorted())
        assertEquals(listOf(5, 6, 5, 3, 4, 5, 6, 7, 7, 8).sorted(), depthFirstGenerationalLister(5, example).sorted())
        assertEquals(26, depthFirstGenerationalLister(18, example).count())
        assertEquals(26.toBigInteger(), depthFirstGenerationalCounter(18, example))
        assertEquals(5934, depthFirstGenerationalLister(80, example).count())
        assertEquals(5934.toBigInteger(), depthFirstGenerationalCounter(80, example))
        assertEquals(26984457539.toBigInteger(), depthFirstGenerationalCounter(256, example))
    }
}