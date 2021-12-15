@file:Suppress("PackageDirectoryMismatch")

package day14

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

fun parse(input: List<String>) =
    Pair(input.first().asSequence(), input.drop(2).associate { Pair(Pair(it[0], it[1]), it[6]) })

fun polymerizeOnce(template: Sequence<Char>, insertionRules: Map<Pair<Char, Char>, Char>): Sequence<Char> {
    return template.plus(' ').zipWithNext().flatMap {
        sequence {
            yield(it.first)
            insertionRules[it]?.let { i -> yield(i) }
        }
    }
}

fun polymerize(count: Int, template: Sequence<Char>, insertionRules: Map<Pair<Char, Char>, Char>): Sequence<Char> {
    return (1..count).fold(template) { t, _ -> polymerizeOnce(t, insertionRules) }
}

fun Sequence<Char>.concatToString() = toList().toCharArray().concatToString()
fun Sequence<Char>.countChars() = groupingBy { it }.fold(0L) { acc, _ -> acc + 1L }
fun Map<Char, Long>.mostCommonMinusLeastCommon() = values.sorted().let { it.last() - it.first() }
fun <K, V> MutableMap<K, V>.memoized(args: K, operation: () -> V): V {
    if (!contains(args)) {
        set(args, operation())
    }
    return get(args)!!
}

fun mPolymerize(
    iterations: Int,
    template: Sequence<Char>,
    insertionRules: Map<Pair<Char, Char>, Char>
): Map<Char, Long> {
    val memory = mutableMapOf<Triple<Char, Int, Pair<Char, Char>>, Long>()
    val letters = insertionRules.values.union(insertionRules.keys.flatMap { listOf(it.first, it.second) }).toSet()

    fun countByLetter(letter: Char, iterations: Int, pair: Pair<Char, Char>): Long {
        return memory.memoized(Triple(letter, iterations, pair)) {
            if (iterations == 0 || pair.second == ' ') {
                if (pair.first == letter) 1 else 0
            } else {
                countByLetter(
                    letter,
                    iterations - 1,
                    Pair(pair.first, insertionRules[pair]!!)
                ) + countByLetter(
                    letter,
                    iterations - 1,
                    Pair(insertionRules[pair]!!, pair.second)
                )
            }
        }
    }
    return letters.associateWith {
        template.plus(' ').zipWithNext().fold(0L) { acc, pair -> acc + countByLetter(it, iterations, pair) }
    }
}

class Day14 {
    @Test
    fun main() {
        val (template, insertionRules) = parse(readPuzzleInputLines("Day14"))
        val part1 = polymerize(10, template, insertionRules).countChars().mostCommonMinusLeastCommon()
        println("Day 14, Part 1: $part1")
        //assertEquals(, part1)
        val part2 = mPolymerize(40, template, insertionRules).mostCommonMinusLeastCommon()
        println("Day 14, Part 2: $part2")
        //assertEquals(, part2)
    }

    @Test
    fun `test solution`() {
        val (template, insertionRules) = parse(
            listOf(
                "NNCB",
                "",
                "CH -> B",
                "HH -> N",
                "CB -> H",
                "NH -> C",
                "HB -> C",
                "HC -> B",
                "HN -> C",
                "NN -> C",
                "BH -> H",
                "NC -> B",
                "NB -> B",
                "BN -> B",
                "BB -> N",
                "BC -> B",
                "CC -> N",
                "CN -> C",
            )
        )
        assertEquals("NCNBCHB", polymerize(1, template, insertionRules).concatToString())
        assertEquals("NCNBCHB".asSequence().countChars(), mPolymerize(1, template, insertionRules))
        assertEquals("NBCCNBBBCBHCB", polymerize(2, template, insertionRules).concatToString())
        assertEquals("NBCCNBBBCBHCB".asSequence().countChars(), mPolymerize(2, template, insertionRules))
        assertEquals("NBBBCNCCNBBNBNBBCHBHHBCHB", polymerize(3, template, insertionRules).concatToString())
        assertEquals("NBBBCNCCNBBNBNBBCHBHHBCHB".asSequence().countChars(), mPolymerize(3, template, insertionRules))
        assertEquals(
            "NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB",
            polymerize(4, template, insertionRules).concatToString()
        )
        assertEquals(
            "NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB".asSequence().countChars(),
            mPolymerize(4, template, insertionRules)
        )
        assertEquals(97, polymerize(5, template, insertionRules).count())
        assertEquals(97, mPolymerize(5, template, insertionRules).values.sum())
        assertEquals(3073, polymerize(10, template, insertionRules).count())
        assertEquals(3073, mPolymerize(10, template, insertionRules).values.sum())
        val counts = polymerize(10, template, insertionRules).countChars()
        val mCounts = mPolymerize(10, template, insertionRules)
        assertEquals(1749, counts['B'])
        assertEquals(1749, mCounts['B'])
        assertEquals(298, counts['C'])
        assertEquals(298, mCounts['C'])
        assertEquals(161, counts['H'])
        assertEquals(161, mCounts['H'])
        assertEquals(865, counts['N'])
        assertEquals(865, mCounts['N'])
        assertEquals(1588, counts.mostCommonMinusLeastCommon())
        assertEquals(1588, mCounts.mostCommonMinusLeastCommon())
        val mCounts40 = mPolymerize(40, template, insertionRules)
        assertEquals(2192039569602, mCounts40['B'])
        assertEquals(3849876073, mCounts40['H'])
    }
}