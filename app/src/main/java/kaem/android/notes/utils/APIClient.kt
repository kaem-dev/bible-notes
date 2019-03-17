package kaem.android.notes.utils

import okhttp3.*
import org.jsoup.Jsoup

class APIClient {

    companion object {
        private val client = OkHttpClient()
        private val apiAdress = "http://ibibles.net/quote.php?lsg-"

        fun getVerse(book: String, chapter: Int, startVerse: Int, endVerse: Int?) : String? {
            var url : String
            if (endVerse == null) {
                url = "$apiAdress$book/$chapter:$startVerse"
            } else {
                url = "$apiAdress$book/$chapter:$startVerse-$endVerse"
            }

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                val html = response.body()?.string()
                val doc = Jsoup.parse(html)
                val body = doc.select("body").html()

                var verse = body.toString()
                    .replace("<br> ", "")
                    .replace("<br>", "")
                    .replace(" <small>","")
                    .replace("<small>","")
                    .replace("</small>"," ")
                    .replace("\n\n", "\n")
                    .trim()

                return verse
            }
        }
    }
}