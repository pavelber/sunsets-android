package org.bernshtam.sunset

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONObject

object ServerWrapper {

    val emptyData: List<String> = listOf("No data", "No data", "No data")
    private const val serverUrl = "https://bernshtam.name/sunset"
    private const val locations = "$serverUrl/locations"
    private const val sunset = "$serverUrl/sunset"

    var name2Coordinates = mapOf<String, JSONObject>()

    @Throws(Exception::class)
    suspend fun getLocations() =
        withContext(Dispatchers.IO) {
            val json = async { CoroutineEngine.fetchJson(locations) }
            val parsedJson = CoroutineEngine.parseJson(json)
            val locations = mutableMapOf<String, JSONObject>()
            parsedJson.keys().forEach { k -> locations.put(k, parsedJson.get(k) as JSONObject) }
            name2Coordinates = locations.toMap()
            return@withContext name2Coordinates.keys.toTypedArray()
        }

    @Throws(Exception::class)
    suspend fun getPrediction(location: JSONObject): List<String> =
        withContext(Dispatchers.IO) {
            val lat = location["lat"] as Double
            val long = location["long"] as Double
            val json = async { CoroutineEngine.fetchJson("$sunset?lat=$lat&long=$long") }
            val parsedJson = CoroutineEngine.parseJsonArray(json)
            val days = (0 until parsedJson.length()).map { i ->
                val json = parsedJson[i] as JSONObject
                """${json["date"]}: ${json["mark"]}/${json["maxMark"]}. ${json["description"]}"""
            }

            return@withContext days
        }
}