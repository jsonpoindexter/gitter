import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.squareup.moshi.Moshi
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>) = runBlocking {
    fuelConfig()
    launch {
        while (true) {
            print("\u001b[H\u001b[2J")
            println("Enter git search term(s)")
            val input = readLine()
            input?.let { input ->
                print("\u001b[H\u001b[2J")
                println("Fetching repository results...")
                searchGitHub(input)
            }
        }
    }.join()
}

fun fuelConfig(){
    FuelManager.instance.basePath = "https://api.github.com"
    FuelManager.instance.baseHeaders = mapOf("Accept" to "application/vnd.github.v3+json")
}

class SearchResponse {
    val total_count: Int = 0
    val items: List<Repository>? = null
}

class Repository {
    val id: Int = 0
    val name: String = ""
    val full_name: String = ""
    val html_url: String = ""
}


suspend fun searchGitHub(searchTerm: String) {
    val moshi = Moshi.Builder().build()
    val jsonAdapter = moshi.adapter(SearchResponse::class.java)
    val (_, _, result) = Fuel.get("/search/repositories?q=$searchTerm&sort=stars&order=desc").awaitString()
    result.fold({ data ->
        print("\u001b[H\u001b[2J")
        println("===========================================================")
        val jsonResponse = jsonAdapter.fromJson(data)
        println("Total Repositories: ${jsonResponse?.total_count}")
        jsonResponse?.items?.take(10)?.forEachIndexed { index, item ->
            println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            println("[$index] ${item.full_name.split("/")[1]} ${item.full_name.split("/")[0]} ${item.html_url}")
        }
        println("===========================================================")
        println("Enter # to clone")
        readLine()?.let { input ->
            print("\u001b[H\u001b[2J")
            val url = jsonResponse?.items?.get(input.toInt())?.html_url
            println("url: $url")
            val proc = Runtime.getRuntime().exec("ghq get $url")
            println()
            var s = proc.inputStream.bufferedReader().readLine()
            while (s != null) {
                println(s)
                s = proc.inputStream.bufferedReader().readLine()
            }
            println()
            println("Press Enter to continue")
            readLine()
        }
    }, { error ->
        println("error: $error")
    })
}
