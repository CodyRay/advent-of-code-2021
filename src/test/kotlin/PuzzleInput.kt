import java.nio.file.Paths
import kotlin.io.path.readLines
import kotlin.io.path.readText

fun readPuzzleInputLines(name: String) = Paths.get("src", "test", "kotlin", "$name.txt").readLines()
fun readPuzzleInputText(name: String) = Paths.get("src", "test", "kotlin", "$name.txt").readText()