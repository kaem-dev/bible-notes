package kaem.android.notes.UI

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kaem.android.notes.R
import kaem.android.notes.Utils.APIClient

class CreateOrEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_edit)
    }

    fun testButton(v : View){
        val textView = findViewById<TextView>(R.id.textViewTest)

        Thread {
            val testVerse = APIClient.getVerse("gen", 1, 1, null)

            runOnUiThread {
                //textView.text = testVerse
                textView.text = Html.fromHtml(testVerse)
                Toast.makeText(applicationContext, testVerse, Toast.LENGTH_SHORT).show()
            }
        }.start()
    }
}
