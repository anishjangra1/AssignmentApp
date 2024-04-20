package com.innoxapp.assignmentapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class Repository {

    suspend fun fetchData(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100")
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connectTimeout = 10000 // 10 seconds
                urlConnection.readTimeout = 10000 // 10 seconds

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = urlConnection.inputStream
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    jsonString
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
    suspend fun parseJson(jsonString: String): List<Article> {
        val jsonArray = JSONArray(jsonString)
        val articles = mutableListOf<Article>()
        for (i in 0 until jsonArray.length()) {
            val jsonObj = jsonArray.getJSONObject(i)
            val id = jsonObj.getString("id")
            val title = jsonObj.getString("title")
            val thumbnailJson = jsonObj.getJSONObject("thumbnail")
            val thumbnail = Thumbnail(
                domain = thumbnailJson.getString("domain"),
                basePath = thumbnailJson.getString("basePath"),
                key = thumbnailJson.getString("key")
            )
            val coverageURL = jsonObj.getString("coverageURL")
            val article = Article(id, title, thumbnail, coverageURL)
            articles.add(article)
        }
        return articles
    }
}