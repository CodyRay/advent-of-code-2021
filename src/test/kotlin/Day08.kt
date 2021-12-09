@file:Suppress("PackageDirectoryMismatch")

package day08

import org.junit.Test
import readPuzzleInputLines
import splitWhitespace
import kotlin.test.assertEquals


class Day08 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day08")
        val part1 =
            parse1(parsed).count { it.length == 7 /*8*/ || it.length == 2 /*1*/ || it.length == 3 /*7*/ || it.length == 4 /*4*/ }
        println("Day 8, Part 1: $part1")
        assertEquals(470, part1)
        val part2 = parse2(parsed).sumOf { decode(it.first, it.second) }
        println("Day 8, Part 2: $part2")
        assertEquals(989396, part2)
    }

    /*
     *  A
     * B C
     *  D
     * E F
     *  G
     * 1: C F
     * 7: A C F
     * 4: B D C F
     * 8: A B C D E F G
     * 9: A B C D F G
     * 3: A C D F G
     * 0: A B C E F G
     * 6: A B D F E G
     * 5: A B D F G
     * 2: A C D E G
     *
     * if length 2, then 1
     * if length 3, then 7
     * if length 4, then 4
     * if length 7, then 8
     * if length 6 and contains all the digits of length 4 (4), then 9
     * if length 5 and contains all the digits of length 2 (1), then 3
     * if length 6 and contains all the digits of length 2 (1), then 0
     * if length 6 and not selected yet, then 6
     * if length 5 and union of digits for 6 has length 5, then 5
     * last remaining is 2
     */
    fun decode(patterns: List<String>, digits: List<String>): Int {
        val one = patterns.single { it.length == 2 }
        val seven = patterns.single { it.length == 3 }
        val four = patterns.single { it.length == 4 }
        val eight = patterns.single { it.length == 7 }

        val nineZeroOrSix = patterns.filter { it.length == 6 }
        assertEquals(3, nineZeroOrSix.count())
        val nine = nineZeroOrSix.single { it.toSet().containsAll(four.toSet()) }
        val zero = nineZeroOrSix.subtract(setOf(nine)).single { it.toSet().containsAll(one.toSet()) }
        val six = nineZeroOrSix.subtract(setOf(nine, zero)).single()

        val threeFiveOrTwo = patterns.filter { it.length == 5 }
        assertEquals(3, threeFiveOrTwo.count())
        val three = threeFiveOrTwo.single { it.toSet().containsAll(one.toSet()) }
        val five = threeFiveOrTwo.single { six.toSet().containsAll(it.toSet()) }
        val two = threeFiveOrTwo.subtract(setOf(three, five)).single()
        val mapping = mapOf(
            zero to '0',
            one to '1',
            two to '2',
            three to '3',
            four to '4',
            five to '5',
            six to '6',
            seven to '7',
            eight to '8',
            nine to '9',
        ).mapKeys { normalize(it.key) }
        return digits.map {
            mapping[normalize(it)]!!
        }.toCharArray().concatToString().toInt()

    }

    fun normalize(segments: String): String {
        return segments.toList().sorted().toCharArray().concatToString()
    }

    @Test
    fun `test parsing`() {
    }

    @Test
    fun `test solution`() {
        val example = parse1(
            listOf(
                "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe",
                "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc",
                "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg",
                "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb",
                "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea",
                "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb",
                "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe",
                "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef",
                "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb",
                "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce",
            )
        )
        val part1 =
            example.count { it.length == 7 /*8*/ || it.length == 2 /*1*/ || it.length == 3 /*7*/ || it.length == 4 /*4*/ }
        assertEquals(26, part1)
        val parsedExample = parse2(
            listOf(
                "be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe",
                "edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc",
                "fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg",
                "fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb",
                "aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea",
                "fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb",
                "dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe",
                "bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef",
                "egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb",
                "gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce",
            )
        )
        assertEquals(8394, decode(parsedExample.first().first, parsedExample.first().second))
        assertEquals(61229, parsedExample.sumOf { decode(it.first, it.second) })
    }

    fun parse1(inputLines: List<String>) = inputLines.map { it.split('|').last() }.flatMap { it.splitWhitespace() }
    fun parse2(inputLines: List<String>) =
        inputLines.map { Pair(it.split('|').first().trim().split(' '), it.split('|').last().trim().split(' ')) }
}
