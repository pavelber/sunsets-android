package org.bernshtam.sunset

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

object CoroutineEngine {
    @Throws(Exception::class)
    suspend fun fetchJson(url: String) =
        withContext(Dispatchers.IO) {
            return@withContext URL(url).readText()
        }

    @Throws(Exception::class)
    suspend fun     parseJson(json: Deferred<String>) =
        withContext(Dispatchers.IO) {
            return@withContext JSONObject(json.await())
        }


    @Throws(Exception::class)
    suspend fun     parseJsonArray(json: Deferred<String>) =
        withContext(Dispatchers.IO) {
            return@withContext JSONArray(json.await())
        }
}