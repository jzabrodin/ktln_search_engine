package search

import java.io.File

val map = mutableMapOf<String, MutableList<Int>>()

enum class SearchStrategies(val mode: String) {
    ALL("ALL"),
    ANY("ANY"),
    NONE("NONE");

    companion object {
        fun isCorrectValue(x: String): Boolean {
            return SearchStrategies.values().find { it.mode == x } !== null
        }

        fun getEnumByString(x: String): SearchStrategies? {
            return SearchStrategies.values().find { it.mode == x }
        }

        fun strategiesAsString(): String {
            val data = mutableListOf<String>()
            SearchStrategies.values().forEach { data.add(it.mode) }

            return data.joinToString( ", " )
        }
    }

    override fun toString(): String {
        return mode;
    }
}

class SearchStrategyAny{
    fun search(
        word: String,
        input: List<String>
    ): MutableList<String> {
        val result = mutableListOf<String>()
        val searchWords = word.split(" ")

        searchWords.forEach { searchWord ->
            map[searchWord.lowercase()]?.forEach {
                val element = input[it]
                if(!result.contains(element)){
                    result.add(element)
                }
            }
        }
        return result
    }
}

class SearchStrategyNone{
    fun search(
        word: String,
        input: List<String>
    ): MutableList<String> {
        val result = mutableListOf<String>()
        val searchWords = word.split(" ")

        val stringNumbersWithStopWords = mutableListOf<Int>()

        searchWords.forEach { searchWord ->
            map[searchWord.lowercase()]?.forEach {
                if (!stringNumbersWithStopWords.contains(it)){
                    stringNumbersWithStopWords.add(it)
                }
            }
        }

        for( i in 0..input.lastIndex){
            if (stringNumbersWithStopWords.contains(i)){
                continue
            }

            result.add(input[i])
        }
        return result
    }
}

class SearchStrategyAll {
    fun search(
        searchWords: String,
        input: List<String>
    ): MutableList<String> {
        val words = searchWords.split(" ")
        val localCache = mutableMapOf<String, MutableList<String>>()

        // fill local cache
        words.forEach { word ->
            map[word.lowercase()]?.forEach {
                if (it < input.size) {
                    if (localCache.containsKey(word)) {
                        localCache[word]?.add(input[it])
                    } else {
                        val list = mutableListOf<String>()
                        list.add(input[it])
                        localCache[word] = list
                    }
                }
            }
        }

        val result = mutableListOf<String>()
        //check local cache
        localCache.forEach { mapEntry ->
            mapEntry.value.forEach { mapEntryValue ->

                var isAllWordsPresentedInResult = true
                for (word: String in words){
                    val isContainWord = mapEntryValue.contains(word, true)
                    if(!isContainWord){
                        isAllWordsPresentedInResult = false
                        break
                    }
                }
                if (isAllWordsPresentedInResult && !result.contains(mapEntryValue)) {
                    result.add(mapEntryValue)
                }
            }
        }

        return result
    }
}

class SearchEngine {
    fun search(input: List<String>, word: String, strategy: SearchStrategies): Unit {

        val wordProcessed = word.trim().lowercase()

        val result = when (strategy) {
            SearchStrategies.ANY -> {
                SearchStrategyAny().search(wordProcessed, input)
            }

            SearchStrategies.NONE -> {
                SearchStrategyNone().search(wordProcessed, input)
            }

            SearchStrategies.ALL -> {
                SearchStrategyAll().search(wordProcessed, input)
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
    fun process(inputData: List<String>, searchStrategy: SearchStrategies) {
        val engine = SearchEngine()
        println("Enter data to search people:")
        val word = readln().trim().lowercase()
        engine.search(inputData, word, searchStrategy)
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
                val searchStrategy: SearchStrategies = this.getSearchStrategy()

                queriesProcessor.process(this.inputData, searchStrategy)
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

    private fun getSearchStrategy(): SearchStrategies {
        println("Please select one of this strategy: " + SearchStrategies.strategiesAsString())

        val strategy = readln()
        var searchStrategy: SearchStrategies? = null

        searchStrategy = SearchStrategies.getEnumByString(strategy)

        if (searchStrategy == null) {
            println("Incorrect value")
            return this.getSearchStrategy()
        }

        return searchStrategy
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
