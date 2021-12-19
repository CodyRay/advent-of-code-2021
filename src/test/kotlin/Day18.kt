@file:Suppress("PackageDirectoryMismatch")

package day18

import org.junit.Test
import readPuzzleInputLines
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

sealed class Snailfish {
    operator fun plus(otherSnailfish: Snailfish) = SnailfishPair(this, otherSnailfish).reduce()

    fun reduce(): Snailfish {
        val snailfish = copy()
        while (true) {
            if (snailfish.shouldExplode()) {
                explodeOnce(snailfish)
                continue
            } else if (snailfish.shouldSplit()) {
                splitOnce(snailfish)
                continue
            } else {
                break
            }
        }
        return snailfish
    }

    private fun explodeOnce(snailfish: Snailfish) {
        var leftNumeric: SnailfishNumber? = null
        var parentExploding: SnailfishPair? = null
        var childExploding: SnailfishPair? = null
        var rightNumeric: SnailfishNumber? = null
        snailfish.visit { current, depth, parent ->
            when (current) {
                is SnailfishPair -> {
                    if (depth > 4 && childExploding == null) {
                        parentExploding = parent
                        childExploding = current
                    }
                }
                is SnailfishNumber -> {
                    if (childExploding == null) {
                        leftNumeric = current
                    } else if (parent != childExploding) {
                        rightNumeric = current
                        return@visit true
                    }
                }
            }
            return@visit false
        }
        assertNotNull(parentExploding)
        assertNotNull(childExploding)
        parentExploding!!.replaceChild(childExploding!!, SnailfishNumber(0))
        if (leftNumeric != null) {
            leftNumeric!!.number += (childExploding!!.left as SnailfishNumber).number
        }
        if (rightNumeric != null) {
            rightNumeric!!.number += (childExploding!!.right as SnailfishNumber).number
        }
    }

    private fun splitOnce(snailfish: Snailfish) {
        snailfish.visit { current, _, parent ->
            if (current is SnailfishNumber && current.number > 9) {
                parent.replaceChild(
                    current,
                    SnailfishPair(
                        SnailfishNumber(floor(current.number / 2.0).roundToInt()),
                        SnailfishNumber(ceil(current.number / 2.0).roundToInt()),
                    )
                )
                return@visit true
            }
            return@visit false
        }
    }

    private fun shouldExplode() = this.visit { snailfish, depth, _ -> snailfish is SnailfishPair && depth > 4 }

    private fun shouldSplit() = this.visit { snailfish, _, _ -> snailfish is SnailfishNumber && snailfish.number > 9 }

    abstract fun visit(depth: Int = 1, visitor: (Snailfish, Int, SnailfishPair) -> Boolean): Boolean

    private fun copy() = parseSnailfish(this.toString())

    abstract fun magnitude(): Int
}

fun parseSnailfish(input: String): Snailfish {
    val tokens = input
        .replace("[", " [ ")
        .replace(",", " , ")
        .replace("]", " ] ")
        .trim()
        .split("""\s+""".toRegex())
        .map { it }
        .iterator()
    return parseSnailfish(tokens)
}

fun parseSnailfish(tokens: Iterator<String>): Snailfish {
    val token = tokens.next()
    return when {
        token == "[" -> {
            val left = parseSnailfish(tokens)
            assertEquals(",", tokens.next())
            val right = parseSnailfish(tokens)
            assertEquals("]", tokens.next())
            SnailfishPair(left, right)
        }
        """^\d+$""".toRegex().matches(token) -> {
            SnailfishNumber(token.toInt())
        }
        else -> error("Unexpceted token, '$token'")
    }
}

class SnailfishPair(var left: Snailfish, var right: Snailfish) : Snailfish() {
    override fun visit(depth: Int, visitor: (Snailfish, Int, SnailfishPair) -> Boolean): Boolean {
        return visitor(left, depth + 1, this)
                || left.visit(depth + 1, visitor)
                || visitor(right, depth + 1, this)
                || right.visit(depth + 1, visitor)
    }

    override fun toString() = "[$left,$right]"

    override fun magnitude() = 3 * left.magnitude() + 2 * right.magnitude()

    internal fun replaceChild(child: Snailfish, newChild: Snailfish) {
        if (left == child) {
            left = newChild
        } else if (right == child) {
            right = newChild
        } else {
            error("parent has missing child")
        }
    }
}

class SnailfishNumber(var number: Int) : Snailfish() {
    override fun visit(depth: Int, visitor: (Snailfish, Int, SnailfishPair) -> Boolean): Boolean {
        // handled by parent
        return false
    }

    override fun magnitude() = number
    override fun toString() = number.toString()
}

fun List<Snailfish>.sumOf() = reduce { s1, s2 -> s1 + s2 }

fun List<Snailfish>.pairings() = flatMap { l -> minus(l).map { r -> l + r } }

class Day18 {
    @Test
    fun main() {
        val parsed = readPuzzleInputLines("Day18").map { parseSnailfish(it) }
        val part1 = parsed.sumOf().magnitude()
        println("Day 18, Part 1: $part1")
        assertEquals(3892, part1)
        val part2 = parsed.pairings().maxOf { it.magnitude() }
        println("Day 18, Part 2: $part2")
        assertEquals(4909, part2)
    }

    @Test
    fun `test parse`() {
        assertEquals(
            "[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]",
            parseSnailfish("[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]").toString()
        )
        assertEquals(
            parseSnailfish("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").toString(),
            (parseSnailfish("[[[[4,3],4],4],[7,[[8,4],9]]]") + parseSnailfish("[1,1]")).toString()
        )
        assertEquals(
            parseSnailfish("[[[[5,0],[7,4]],[5,5]],[6,6]]").toString(),
            listOf(
                "[1,1]",
                "[2,2]",
                "[3,3]",
                "[4,4]",
                "[5,5]",
                "[6,6]"
            ).map {
                parseSnailfish(it)
            }.sumOf().toString()
        )
        assertEquals(
            parseSnailfish("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").toString(),
            listOf(
                "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
                "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
                "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
                "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
                "[7,[5,[[3,8],[1,4]]]]",
                "[[2,[2,2]],[8,[8,1]]]",
                "[2,9]",
                "[1,[[[9,3],9],[[9,0],[0,7]]]]",
                "[[[5,[7,4]],7],1]",
                "[[[[4,2],2],6],[8,7]]",
            ).map {
                parseSnailfish(it)
            }.sumOf().toString()
        )
        assertEquals(129, parseSnailfish("[[9,1],[1,9]]").magnitude())

        assertEquals(143, parseSnailfish("[[1,2],[[3,4],5]]").magnitude())
        assertEquals(1384, parseSnailfish("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude())
        assertEquals(445, parseSnailfish("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude())
        assertEquals(791, parseSnailfish("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude())
        assertEquals(1137, parseSnailfish("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude())
        assertEquals(3488, parseSnailfish("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude())

        assertEquals(
            4140,
            listOf(
                "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
                "[[[5,[2,8]],4],[5,[[9,9],0]]]",
                "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
                "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
                "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
                "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
                "[[[[5,4],[7,7]],8],[[8,3],8]]",
                "[[9,3],[[9,9],[6,[4,9]]]]",
                "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
                "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]",
            ).map {
                parseSnailfish(it)
            }.sumOf().magnitude()
        )
        assertEquals(
            3993,
            listOf(
                "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
                "[[[5,[2,8]],4],[5,[[9,9],0]]]",
                "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
                "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
                "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
                "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
                "[[[[5,4],[7,7]],8],[[8,3],8]]",
                "[[9,3],[[9,9],[6,[4,9]]]]",
                "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
                "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]",
            ).map {
                parseSnailfish(it)
            }.pairings().maxOf { it.magnitude() }
        )
    }

    @Test
    fun `test solution`() {
    }
}
