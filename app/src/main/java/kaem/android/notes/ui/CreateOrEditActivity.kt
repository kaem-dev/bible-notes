package kaem.android.notes.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kaem.android.notes.model.Note
import kaem.android.notes.R
import kaem.android.notes.utils.APIClient
import java.text.SimpleDateFormat
import java.util.*

class CreateOrEditActivity : AppCompatActivity() {
    private var noteIndex : Int = -1
    private lateinit var noteExtra : Note
    private lateinit var titleEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var bookSpinner: Spinner
    private lateinit var chapterEditText: EditText
    private lateinit var startVerseEditText: EditText
    private lateinit var endVerseEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var booksValues : Array<String>

    companion object {
        const val REQUEST_EDIT_NOTE = 0
        const val SAVE_CODE = 1
        const val DELETE_CODE = 2
        const val EXTRA_NOTE  = "note"
        const val EXTRA_INDEX = "index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_edit)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initViews()

        booksValues = resources.getStringArray(R.array.books_values_array)

        if (intent.hasExtra(EXTRA_NOTE)){
            noteIndex = intent.getIntExtra(EXTRA_INDEX, -1)
            noteExtra = intent.getParcelableExtra(EXTRA_NOTE)
            initNote(noteExtra)
        }
    }

    override fun onBackPressed() {
        saveNote()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_create_or_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.delete_note -> {
                deleteNote()
                return true
            }
        }
        return  super.onOptionsItemSelected(item)
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
                    contentEditText.setText("${contentEditText.text} \n\n$ref\n$verse", TextView.BufferType.EDITABLE)
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

    private fun saveNote() {
        val note = Note()
        note.id = noteIndex
        note.title = titleEditText.text.toString()
        note.date = dateTextView.text.toString()
        note.content = contentEditText.text.toString()

        intent = Intent()
        intent.putExtra(EXTRA_NOTE, note as Parcelable)
        intent.putExtra(EXTRA_INDEX, noteIndex)
        setResult(SAVE_CODE, intent)
        finish()
    }

    private fun deleteNote(){
        val builder = AlertDialog.Builder(this)

        builder.setMessage("Voulez-vous vraiment supprimer cette note ?")

        builder.setPositiveButton("Oui"){ _, _ ->
            intent = Intent()
            intent.putExtra(EXTRA_INDEX,noteIndex)
            setResult(DELETE_CODE, intent)
            finish()
        }

        builder.setNegativeButton("Non"){_,_ ->
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()

    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initNote(note : Note){
        titleEditText.setText(note.title, TextView.BufferType.EDITABLE)
        dateTextView.text = note.date
        contentEditText.setText(note.content, TextView.BufferType.EDITABLE)
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
        contentEditText = findViewById(R.id.editTextNote)
    }
}
