import org.junit.Test
import kotlin.test.assertEquals

val EXAMPLE_DATA = listOf(
    Forward(5),
    Down(5),
    Forward(8),
    Up(3),
    Down(8),
    Forward(2),
)

data class SubmarineState(val aim: Int, val depth: Int, val horizontal: Int)

sealed class Command {
    companion object {
        fun parseAll(commandStrings: List<String>): List<Command> = commandStrings.map {
            val (type, distanceStr) = it.split(' ', limit = 2)
            val distance = distanceStr.toInt()
            when (type) {
                "forward" -> Forward(distance)
                "down" -> Down(distance)
                "up" -> Up(distance)
                else -> error("Bad command type")
            }
        }
    }

    abstract fun simulate(coords: SubmarineState, part2: Boolean): SubmarineState
}

fun List<Command>.simulate(coords: SubmarineState, part2: Boolean): SubmarineState =
    fold(coords) { currentCoords, command -> command.simulate(currentCoords, part2) }

data class Forward(val distance: Int) : Command() {
    override fun simulate(coords: SubmarineState, part2: Boolean): SubmarineState =
        SubmarineState(
            aim = coords.aim,
            depth = coords.depth + coords.aim * distance,
            horizontal = coords.horizontal + distance
        )
}

data class Down(val distance: Int) : Command() {
    override fun simulate(coords: SubmarineState, part2: Boolean): SubmarineState = if (part2)
        SubmarineState(aim = coords.aim + distance, depth = coords.depth, horizontal = coords.horizontal)
    else
        SubmarineState(aim = coords.aim, depth = coords.depth + distance, horizontal = coords.horizontal)

}

data class Up(val distance: Int) : Command() {
    override fun simulate(coords: SubmarineState, part2: Boolean): SubmarineState = if (part2)
        SubmarineState(aim = coords.aim - distance, depth = coords.depth, horizontal = coords.horizontal)
    else
        SubmarineState(aim = coords.aim, depth = coords.depth - distance, coords.horizontal)
}

class Day02 {
    @Test
    fun main() {
        val commands = Command.parseAll(readPuzzleInputLines("Day02"))
        val part1 = commands.simulate(SubmarineState(0, 0, 0), part2 = false)
            .let { it.depth * it.horizontal }
        println("Day 2, Part 1: $part1")
        assertEquals(2150351, part1)
        val part2 = commands.simulate(SubmarineState(0, 0, 0), part2 = true)
            .let { it.depth * it.horizontal }
        println("Day 2, Part 2: $part2")
        assertEquals(1842742223, part2)
    }

    @Test
    fun `test parsing`() {
        assertEquals(
            EXAMPLE_DATA, Command.parseAll(
                listOf(
                    "forward 5",
                    "down 5",
                    "forward 8",
                    "up 3",
                    "down 8",
                    "forward 2",
                )
            )
        )
    }

    @Test
    fun `test solution`() {
        val exampleCoords = EXAMPLE_DATA.simulate(SubmarineState(0, 0, 0), part2 = false)
        assertEquals(15, exampleCoords.horizontal)
        assertEquals(10, exampleCoords.depth)
        assertEquals(150, exampleCoords.depth * exampleCoords.horizontal)
        val exampleCoords2 = EXAMPLE_DATA.simulate(SubmarineState(0, 0, 0), part2 = true)
        assertEquals(15, exampleCoords2.horizontal)
        assertEquals(60, exampleCoords2.depth)
        assertEquals(900, exampleCoords2.depth * exampleCoords2.horizontal)
    }
}