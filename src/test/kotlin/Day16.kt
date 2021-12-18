@file:Suppress("PackageDirectoryMismatch")

package day16

import org.junit.Test
import readPuzzleInputLines
import kotlin.test.assertEquals

class BitStream(var stream: List<Char>) {
    fun pop(bits: Int) = stream.take(bits).toCharArray().concatToString().toInt(2).also {
        stream = stream.drop(bits)
    }

    fun subStream(bits: Int): BitStream {
        return BitStream(stream.take(bits)).also {
            stream = stream.drop(bits)
        }
    }

    fun isNotEmpty() = stream.isNotEmpty()

    companion object {
        fun fromHex(hex: String): BitStream =
            BitStream(hex.flatMap { it.toString().toInt(16).toString(2).padStart(4, '0').toList() })
    }
}

fun parsePacket(bitStream: BitStream): Packet {
    val version = bitStream.pop(3)
    return when (bitStream.pop(3)) {
        0 -> {
            val subPackets = parseSubPackets(bitStream)
            Sum(version, subPackets)
        }
        1 -> {
            val subPackets = parseSubPackets(bitStream)
            Product(version, subPackets)
        }
        2 -> {
            val subPackets = parseSubPackets(bitStream)
            Minimum(version, subPackets)
        }
        3 -> {
            val subPackets = parseSubPackets(bitStream)
            Maximum(version, subPackets)
        }
        4 -> {
            val number = parseNumber(bitStream)
            LiteralValue(version, number)
        }
        5 -> {
            val subPackets = parseSubPackets(bitStream)
            assertEquals(2, subPackets.size)
            GreaterThan(version, subPackets.first(), subPackets.last())
        }
        6 -> {
            val subPackets = parseSubPackets(bitStream)
            assertEquals(2, subPackets.size)
            LessThan(version, subPackets.first(), subPackets.last())
        }
        7 -> {
            val subPackets = parseSubPackets(bitStream)
            assertEquals(2, subPackets.size)
            EqualTo(version, subPackets.first(), subPackets.last())
        }
        else -> error("Invalid Type")
    }
}

fun parseNumber(bitStream: BitStream) = buildList {
    do {
        val hasNext = bitStream.pop(1)
        add(bitStream.pop(4).toString(16))
    } while (hasNext > 0)
}.joinToString("").toLong(16)

fun parseSubPackets(bitStream: BitStream): List<Packet> {
    return buildList {
        if (bitStream.pop(1) > 0) {
            repeat(bitStream.pop(11)) {
                add(parsePacket(bitStream))
            }
        } else {
            val subStream = bitStream.subStream(bitStream.pop(15))
            while (subStream.isNotEmpty()) {
                add(parsePacket(subStream))
            }
        }
    }
}

sealed class Packet {
    abstract fun versionSum(): Int
    abstract fun evaluate(): Long
}

data class Sum(val version: Int, val subPackets: List<Packet>) : Packet() {
    override fun versionSum() = version + subPackets.sumOf { it.versionSum() }
    override fun evaluate() = subPackets.sumOf { it.evaluate() }
}

data class Product(val version: Int, val subPackets: List<Packet>) : Packet() {
    override fun versionSum() = version + subPackets.sumOf { it.versionSum() }
    override fun evaluate() = subPackets.map { it.evaluate() }.reduce { p1, p2 -> p1 * p2 }
}

data class Minimum(val version: Int, val subPackets: List<Packet>) : Packet() {
    override fun versionSum() = version + subPackets.sumOf { it.versionSum() }
    override fun evaluate() = subPackets.minOf { it.evaluate() }
}

data class Maximum(val version: Int, val subPackets: List<Packet>) : Packet() {
    override fun versionSum() = version + subPackets.sumOf { it.versionSum() }
    override fun evaluate() = subPackets.maxOf { it.evaluate() }
}

data class LiteralValue(val version: Int, val number: Long) : Packet() {
    override fun versionSum() = version
    override fun evaluate() = number
}

data class GreaterThan(val version: Int, val leftSubPacket: Packet, val rightSubPacket: Packet) : Packet() {
    override fun versionSum() = version + leftSubPacket.versionSum() + rightSubPacket.versionSum()
    override fun evaluate() = if (leftSubPacket.evaluate() > rightSubPacket.evaluate()) 1L else 0L
}

data class LessThan(val version: Int, val leftSubPacket: Packet, val rightSubPacket: Packet) : Packet() {
    override fun versionSum() = version + leftSubPacket.versionSum() + rightSubPacket.versionSum()
    override fun evaluate() = if (leftSubPacket.evaluate() < rightSubPacket.evaluate()) 1L else 0L
}

data class EqualTo(val version: Int, val leftSubPacket: Packet, val rightSubPacket: Packet) : Packet() {
    override fun versionSum() = version + leftSubPacket.versionSum() + rightSubPacket.versionSum()
    override fun evaluate() = if (leftSubPacket.evaluate() == rightSubPacket.evaluate()) 1L else 0L
}

class Day16 {
    @Test
    fun main() {
        val parsed = parsePacket(BitStream.fromHex(readPuzzleInputLines("Day16").first()))
        val part1 = parsed.versionSum()
        println("Day 16, Part 1: $part1")
        // assertEquals(877, part1)
        val part2 = parsed.evaluate()
        println("Day 16, Part 2: $part2")
        //assertEquals(194435634456, part2)
    }

    @Test
    fun `test bitstreamFromHex`() {
        assertEquals("110100101111111000101000".toList(), BitStream.fromHex("D2FE28").stream)
        assertEquals(
            "00111000000000000110111101000101001010010001001000000000".toList(),
            BitStream.fromHex("38006F45291200").stream
        )
        assertEquals(
            "11101110000000001101010000001100100000100011000001100000".toList(),
            BitStream.fromHex("EE00D40C823060").stream
        )
    }

    @Test
    fun `test LiteralValue`() {
        assertEquals(2021, (parsePacket(BitStream.fromHex("D2FE28")) as LiteralValue).number)
    }

    @Test
    fun `test Operator`() {
        assertEquals(
            LessThan(
                1,
                LiteralValue(6, 10),
                LiteralValue(2, 20)

            ), parsePacket(BitStream.fromHex("38006F45291200"))
        )
        assertEquals(
            Maximum(
                7, listOf(
                    LiteralValue(2, 1),
                    LiteralValue(4, 2),
                    LiteralValue(1, 3),
                )
            ), parsePacket(BitStream.fromHex("EE00D40C823060"))
        )
        assertEquals(
            Minimum(
                4, listOf(
                    Minimum(
                        1, listOf(
                            Minimum(
                                5, listOf(
                                    LiteralValue(6, 15),
                                )
                            ),
                        )
                    ),
                )
            ), parsePacket(BitStream.fromHex("8A004A801A8002F478"))
        )
        assertEquals(16, parsePacket(BitStream.fromHex("8A004A801A8002F478")).versionSum())
        assertEquals(
            Sum(
                3, listOf(
                    Sum(
                        0, listOf(
                            LiteralValue(0, 10),
                            LiteralValue(5, 11),
                        )
                    ),
                    Sum(
                        1, listOf(
                            LiteralValue(0, 12),
                            LiteralValue(3, 13),
                        )
                    ),
                )
            ), parsePacket(BitStream.fromHex("620080001611562C8802118E34"))
        )
        assertEquals(12, parsePacket(BitStream.fromHex("620080001611562C8802118E34")).versionSum())
        assertEquals(
            Sum(
                6, listOf(
                    Sum(
                        0, listOf(
                            LiteralValue(0, 10),
                            LiteralValue(6, 11),
                        )
                    ),
                    Sum(
                        4, listOf(
                            LiteralValue(7, 12),
                            LiteralValue(0, 13),
                        )
                    ),
                )
            ), parsePacket(BitStream.fromHex("C0015000016115A2E0802F182340"))
        )
        assertEquals(23, parsePacket(BitStream.fromHex("C0015000016115A2E0802F182340")).versionSum())
        assertEquals(
            Sum(
                5, listOf(
                    Sum(
                        1, listOf(
                            Sum(
                                3, listOf(
                                    LiteralValue(7, 6),
                                    LiteralValue(6, 6),
                                    LiteralValue(5, 12),
                                    LiteralValue(2, 15),
                                    LiteralValue(2, 15),
                                )
                            ),
                        )
                    ),
                )
            ), parsePacket(BitStream.fromHex("A0016C880162017C3686B18A3D4780"))
        )
        assertEquals(31, parsePacket(BitStream.fromHex("A0016C880162017C3686B18A3D4780")).versionSum())
        assertEquals(3, parsePacket(BitStream.fromHex("C200B40A82")).evaluate())
        assertEquals(54, parsePacket(BitStream.fromHex("04005AC33890")).evaluate())
        assertEquals(7, parsePacket(BitStream.fromHex("880086C3E88112")).evaluate())
        assertEquals(9, parsePacket(BitStream.fromHex("CE00C43D881120")).evaluate())
        assertEquals(1, parsePacket(BitStream.fromHex("D8005AC2A8F0")).evaluate())
        assertEquals(0, parsePacket(BitStream.fromHex("F600BC2D8F")).evaluate())
        assertEquals(0, parsePacket(BitStream.fromHex("9C005AC2F8F0")).evaluate())
        assertEquals(1, parsePacket(BitStream.fromHex("9C0141080250320F1802104A08")).evaluate())
    }

    @Test
    fun `test solution`() {
    }
}
