import java.nio.file.Paths
import kotlin.io.path.readLines

fun readPuzzleInputLines(name: String) = Paths.get("src", "test", "kotlin", "$name.txt").readLines()