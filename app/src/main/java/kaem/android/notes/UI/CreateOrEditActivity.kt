package kaem.android.notes.UI

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kaem.android.notes.Model.Note
import kaem.android.notes.R
import kaem.android.notes.Utils.APIClient
import kotlinx.android.synthetic.main.activity_create_or_edit.*
import java.text.SimpleDateFormat
import java.util.*

class CreateOrEditActivity : AppCompatActivity() {
    lateinit var titleEditText: EditText
    lateinit var dateTextView: TextView
    lateinit var bookSpinner: Spinner
    lateinit var chapterEditText: EditText
    lateinit var startVerseEditText: EditText
    lateinit var endVerseEditText: EditText
    lateinit var noteEditText: EditText
    lateinit var booksValues : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_edit)

        booksValues = resources.getStringArray(R.array.books_values_array)

        initViews()
    }

    fun getVerseButton(v : View){
        Thread {
            if(!checkValidParams()){
                runOnUiThread {
                    Toast.makeText(applicationContext, "La saisie n'est pas valide", Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            val book = booksValues[bookSpinner.selectedItemPosition]
            val chapter = Integer.valueOf(chapterEditText.text.toString())
            val startVerse = Integer.valueOf(startVerseEditText.text.toString())
            var endVerse : Int?
            var ref : String
            if (endVerseEditText.text.isEmpty()) {
                endVerse = null
                ref = "${bookSpinner.selectedItem} $chapter:$startVerse"
            } else {
                endVerse = Integer.valueOf(endVerseEditText.text.toString())
                ref = "${bookSpinner.selectedItem} $chapter:$startVerse-$endVerse"
            }
            val verse = APIClient.getVerse(book, chapter, startVerse, endVerse)

            runOnUiThread {
                hideKeyboard()
                if(verse!!.contains("Bible verse not found.")) {
                    Toast.makeText(applicationContext, "Le verset n'existe pas", Toast.LENGTH_SHORT).show()
                } else {
                    noteEditText.setText("${noteEditText.text} \n\n$ref\n$verse", TextView.BufferType.EDITABLE)
                }
            }
        }.start()
    }

    private fun checkValidParams() : Boolean {
        var isOk = true

        if(chapterEditText.text.isEmpty())
            isOk = false

        if(startVerseEditText.text.isEmpty())
            isOk = false

        return isOk
    }

    fun saveNote() {
        val note = Note(editTextTitle.text.toString(), editTextDate.text.toString(), editTextNote.text.toString())
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initViews(){
        titleEditText = findViewById(R.id.editTextTitle)

        dateTextView = findViewById(R.id.editTextDate)
        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val todayString = formatter.format(todayDate)
        dateTextView.text = todayString

        bookSpinner = findViewById(R.id.spinnerBooks)
        ArrayAdapter.createFromResource(
            this,
            R.array.books_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bookSpinner.adapter = adapter
        }

        chapterEditText = findViewById(R.id.editTextChapter)
        startVerseEditText = findViewById(R.id.editTextStartVerse)
        endVerseEditText = findViewById(R.id.editTextEndVerse)
        noteEditText = findViewById(R.id.editTextNote)
    }
}
