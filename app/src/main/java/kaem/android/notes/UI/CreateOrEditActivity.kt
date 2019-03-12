package kaem.android.notes.UI

import android.annotation.SuppressLint
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import kaem.android.notes.R
import kaem.android.notes.Utils.APIClient

class CreateOrEditActivity : AppCompatActivity() {
    lateinit var titleEditText: EditText
    lateinit var dateEditText: EditText
    lateinit var bookSpinner: Spinner
    lateinit var chapterEditText: EditText
    lateinit var startVerseEditText: EditText
    lateinit var endVerseEditText: EditText
    lateinit var noteEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_edit)
    }

    @SuppressWarnings("deprecation")
    fun getVerseButton(v : View){
        val textView = findViewById<EditText>(R.id.editTextContent)

        Thread {
            val verse = APIClient.getVerse("gen", 1, 1, null)
            runOnUiThread {
                textView.setText("${textView.text} \n $verse", TextView.BufferType.EDITABLE)
            }
        }.start()
    }

    fun initViews(){
        
    }
}
