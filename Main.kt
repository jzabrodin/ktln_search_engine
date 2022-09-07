package search

import java.io.File

val map = mutableMapOf<String, MutableList<Int>>()

class SearchEngine {
    fun search(input: List<String>, word: String): Unit {
        val result = mutableListOf<String>()
        val wordProcessed = word.trim().lowercase()

        map[wordProcessed]?.forEach {
            if (it < input.size) {
                result.add(input[it])
            }
        }

        if (result.isNotEmpty()) {
            println("People found:")
            result.forEach { s -> println(s) }
        } else {
            println("No matching people found.")
        }
    }
}

class DataCollector {
    fun collect(): MutableList<String> {
        println("Enter the number of people:")
        val linesNumber = InputHelper().getInt()
        var currentLine = 0
        val inputData = mutableListOf<String>()
        println("Enter all people:")
        while (currentLine < linesNumber) {
            inputData.add(readln().trim())
            currentLine++
        }

        return inputData
    }
}

class QueriesProcessor {
    fun process(inputData: List<String>): Unit {
        val engine = SearchEngine()
        println("Enter data to search people:")
        val word = readln().trim()
        engine.search(inputData, word)
    }
}

class InputHelper {
    fun getInt(): Int {
        var result: Int
        while (true) {
            try {
                result = readln().toInt()
                break
            } catch (_: NumberFormatException) {

            }
        }

        return result
    }
}

class MenuProcessor(private val inputData: List<String>) {
    enum class Options(val option: Int) {
        FIND(1), PRINT(2), EXIT(0);

        companion object {
            fun isCorrectValue(x: Int): Boolean {
                return Options.values().find { it.option == x } != null
            }
        }
    }

    private fun getOption(): Int {
        val option = InputHelper().getInt()

        return if (Options.isCorrectValue(option)) {
            option
        } else {
            println("Incorrect option! Try again.")
            this.getOption()
        }
    }

    fun process() {
        val queriesProcessor = QueriesProcessor()
        val dataPrinter = DataPrinter()
        showMenu()

        when (getOption()) {
            Options.FIND.option -> {
                queriesProcessor.process(this.inputData)
                this.process()
            }

            Options.PRINT.option -> {
                dataPrinter.print(this.inputData)
                this.process()
            }

            Options.EXIT.option -> {
                return
            }
        }
    }

    private fun showMenu() {
        println("=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")
    }
}

class DataPrinter() {
    fun print(x: List<String>) {
        for (a in x) {
            println(a)
        }
    }
}

class IndexCreator {
    fun create(x: List<String>) {
        var lineNumber = 0
        x.forEach { string ->
            // string loop
            string.trim().split(" ").forEach { word ->
                // word loop
                val s = word.lowercase()
                val list = map[s];
                if (list != null) {
                    list.add(lineNumber)
                } else {
                    val mutableListOf = mutableListOf<Int>()
                    mutableListOf.add(lineNumber)
                    map[s] = mutableListOf;
                }
            }
            lineNumber++
        }
    }
}

fun main(args: Array<String>) {
    var inputData: MutableList<String> = mutableListOf()

    if (args.isEmpty()) {
        val collector = DataCollector()
        inputData = collector.collect()
    } else {
        val path = args[1]
        val file = File(path)
        file.forEachLine { inputData.add(it.trim()) }
    }

    val indexCreator = IndexCreator()
    indexCreator.create(inputData)

    val menuProcessor = MenuProcessor(inputData)
    menuProcessor.process()

}
