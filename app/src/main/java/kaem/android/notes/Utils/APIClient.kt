package kaem.android.notes.Utils

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import android.R.string
import android.os.AsyncTask.execute



class APIClient {

    companion object {
        private val client = OkHttpClient()
        private val apiAdress = "http://ibibles.net/quote.php?lsg-"

        fun getVerse(book: String, chapter: Int, startVerse: Int, endVerse: Int?) : String? {

            val request = Request.Builder()
                //.url("$apiAdress-$book/$chapter:$startVerse")
                .url("http://ibibles.net/quote.php?lsg-gen/1:1")
                .build()

            client.newCall(request).execute().use { response -> return response.body()?.string() }

        }
    }
}