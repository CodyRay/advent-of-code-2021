import org.junit.Test
import kotlin.test.assertEquals

val EXAMPLE_DATA = listOf(
    Command("forward", 5),
    Command("down", 5),
    Command("forward", 8),
    Command("up", 3),
    Command("down", 8),
    Command("forward", 2),
)

data class Submarine(val aim: Int, val depth: Int, val horizontal: Int, val handlesAim: Boolean) {
    companion object {
        val part1 = Submarine(0, 0, 0, false)
        val part2 = Submarine(0, 0, 0, true)
    }

    fun move(changeAim: Int = 0, changeDepth: Int = 0, changeHorizontal: Int = 0): Submarine =
        Submarine(aim + changeAim, depth + changeDepth, horizontal + changeHorizontal, handlesAim)

    fun simulate(command: Command): Submarine = when (command.type) {
        "forward" -> move(changeDepth = aim * command.movement, changeHorizontal = command.movement)
        "up" -> if (handlesAim) move(changeAim = -command.movement) else move(changeDepth = -command.movement)
        "down" -> if (handlesAim) move(changeAim = command.movement) else move(changeDepth = command.movement)
        else -> error("bad command")
    }

    fun simulate(commands: List<Command>): Submarine =
        commands.fold(this) { currentCoords, command -> currentCoords.simulate(command) }
}

data class Command(val type: String, val movement: Int) {
    companion object {
        fun parseAll(commandStrings: List<String>): List<Command> = commandStrings.map {
            it.split(' ', limit = 2).let { (type, distanceStr) -> Command(type, distanceStr.toInt()) }
        }
    }
}

class Day02 {
    @Test
    fun main() {
        val commands = Command.parseAll(readPuzzleInputLines("Day02"))
        val part1 = Submarine.part1.simulate(commands).let { it.depth * it.horizontal }
        println("Day 2, Part 1: $part1")
        assertEquals(2150351, part1)
        val part2 = Submarine.part2.simulate(commands).let { it.depth * it.horizontal }
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
        assertEquals(1, Submarine.part2.move(1).aim)
        assertEquals(1, Submarine.part2.simulate(Command("down", 1)).aim)
        val exampleCoords = Submarine.part1.simulate(EXAMPLE_DATA)
        assertEquals(15, exampleCoords.horizontal)
        assertEquals(10, exampleCoords.depth)
        assertEquals(150, exampleCoords.depth * exampleCoords.horizontal)
        val exampleCoords2 = Submarine.part2.simulate(EXAMPLE_DATA)
        assertEquals(15, exampleCoords2.horizontal)
        assertEquals(60, exampleCoords2.depth)
        assertEquals(900, exampleCoords2.depth * exampleCoords2.horizontal)
    }
}