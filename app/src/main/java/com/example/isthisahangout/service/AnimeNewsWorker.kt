package com.example.isthisahangout.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.isthisahangout.models.AnimeNews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.HttpException
import java.io.IOException

class AnimeNewsWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result =
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val serverData = getHTML()
                val div =
                    serverData.getElementsByClass("sentinel-tag-latestArticles browse-half clip-half")
                        .first()
                val articles = div.getElementsByTag("article").map { article ->
                    val linkTag = article.getElementsByClass("bc-img-link")
                    val url = linkTag.attr("href")
                    val imageTag = article.getElementsByTag("source").first()
                    val image: String =
                        if (imageTag.hasAttr("srcset"))
                            imageTag.attr("srcset")
                        else
                            imageTag.attr("data-srcset")
                    val title = article.getElementsByClass("bc-title-link").text().trim()
                    val author = article.getElementsByClass("bc-author").text().trim()
                    val desc = article.getElementsByClass("bc-excerpt").text().trim()
                    AnimeNews(
                        title = title,
                        image = image,
                        url = url,
                        author = author,
                        desc = desc
                    )
                }
//                animeNewsDao.deleteAnimeNews()
//                animeNewsDao.insertAnimeNews(animeNews = articles)
            }
            Result.success()
        } catch (exception: IOException) {
            Result.failure()
        } catch (exception: HttpException) {
            Result.failure()
        }
    private suspend fun getHTML() = Jsoup.connect("https://www.cbr.com/tag/anime/").get()
}