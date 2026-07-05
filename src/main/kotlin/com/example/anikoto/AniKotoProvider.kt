package com.example.anikoto

import org.jsoup.Jsoup
import java.net.URLEncoder

class AniKotoProvider {
    companion object {
        private const val MAIN_URL = "https://anikoto.cz"
        const val NAME = "AniKoto"
        const val VERSION = "1.0.0"
    }

    fun search(query: String): List<AnimeResult> {
        return try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val searchUrl = "$MAIN_URL/search?q=$encodedQuery"
            val doc = Jsoup.connect(searchUrl).get()
            
            val results = mutableListOf<AnimeResult>()
            doc.select("div.anime-card").forEach { element ->
                val title = element.selectFirst("h3")?.text() ?: return@forEach
                val link = element.selectFirst("a")?.attr("href") ?: return@forEach
                val poster = element.selectFirst("img")?.attr("src") ?: ""
                
                results.add(AnimeResult(
                    title = title,
                    link = link,
                    poster = poster
                ))
            }
            results
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAnimeDetails(url: String): AnimeDetails? {
        return try {
            val doc = Jsoup.connect(url).get()
            val title = doc.selectFirst("h1")?.text() ?: return null
            val description = doc.selectFirst("div.description")?.text() ?: ""
            val episodes = doc.select("a.episode").size
            
            AnimeDetails(
                title = title,
                description = description,
                episodes = episodes,
                url = url
            )
        } catch (e: Exception) {
            null
        }
    }

    data class AnimeResult(
        val title: String,
        val link: String,
        val poster: String
    )

    data class AnimeDetails(
        val title: String,
        val description: String,
        val episodes: Int,
        val url: String
    )
}
