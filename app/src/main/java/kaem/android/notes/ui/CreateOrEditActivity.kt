package kaem.android.notes.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kaem.android.notes.model.Note
import kaem.android.notes.R
import kaem.android.notes.utils.APIClient
import java.text.SimpleDateFormat
import java.util.*

class CreateOrEditActivity : AppCompatActivity(), View.OnClickListener {
    private var noteId : Int? = null
    private lateinit var noteExtra : Note
    private lateinit var titleEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var bookSpinner: Spinner
    private lateinit var chapterEditText: EditText
    private lateinit var startVerseEditText: EditText
    private lateinit var endVerseEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var verseLayout: ConstraintLayout
    private lateinit var booksValues : Array<String>

    companion object {
        const val REQUEST_EDIT_NOTE = 0
        const val SAVE_CODE = 1
        const val DELETE_CODE = 2
        const val EXTRA_NOTE  = "note"
        const val EXTRA_ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_edit)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initViewObjects()

        findViewById<Button>(R.id.show_verse_button).setOnClickListener(this)
        findViewById<Button>(R.id.add_verse_button).setOnClickListener(this)

        booksValues = resources.getStringArray(R.array.books_values_array)

        if (intent.hasExtra(EXTRA_NOTE)){
            supportActionBar!!.title = getString(R.string.editNoteToolbarTitle)
            noteExtra = intent.getParcelableExtra(EXTRA_NOTE)
            noteId = noteExtra.id
            initNote(noteExtra)
        }
    }

    override fun onBackPressed() {
        if(verseLayout.visibility == View.VISIBLE) {
            val animate = TranslateAnimation(0f, 0f, 0f,verseLayout.height.toFloat())
            animate.duration = 300
            verseLayout.startAnimation(animate)
            verseLayout.visibility = View.INVISIBLE
        } else {
            saveNote()
        }
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

    override fun onClick(v: View) {
        Thread {
            if(!checkValidParams()){
                runOnUiThread {
                    Toast.makeText(applicationContext, getString(R.string.wrongVerseParams), Toast.LENGTH_SHORT).show()
                }
                return@Thread
            }

            val book = booksValues[bookSpinner.selectedItemPosition]
            val chapter = Integer.valueOf(chapterEditText.text.toString())
            val startVerse = Integer.valueOf(startVerseEditText.text.toString())
            val endVerse : Int?
            val ref : String
            if (endVerseEditText.text.isEmpty()) {
                endVerse = null
                ref = "${bookSpinner.selectedItem} $chapter:$startVerse"
            } else {
                endVerse = Integer.valueOf(endVerseEditText.text.toString())
                ref = "${bookSpinner.selectedItem} $chapter:$startVerse-$endVerse"
            }
            val verse = APIClient.getVerse(book, chapter, startVerse, endVerse)

            runOnUiThread {
                if(verse!!.contains("Bible verse not found.")) {
                    Toast.makeText(applicationContext, getString(R.string.verseNotFound), Toast.LENGTH_SHORT).show()
                } else {
                    when (v?.id) {
                        R.id.show_verse_button -> showVerseButton(ref, verse)
                        R.id.add_verse_button -> addVerseButton(ref, verse)
                    }
                }
            }
        }.start()
    }

    private fun showVerseButton(ref : String, verse : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(ref)
        builder.setMessage(verse)
        builder.setPositiveButton("Ajouter"){ _, _ ->
            contentEditText.append(getString(R.string.verse, ref, verse))
            contentEditText.requestFocus()
            contentEditText.setSelection(contentEditText.text.length)
        }
        builder.setNegativeButton(getString(R.string.no)){ _, _ ->
        }
        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    private fun addVerseButton(ref : String, verse : String){
        contentEditText.append(getString(R.string.verse, ref, verse))
        contentEditText.requestFocus()
        contentEditText.setSelection(contentEditText.text.length)
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
        if (noteId != null) {
            note.id = noteId!!
        } else {
            note.id = -1
        }
        note.title = titleEditText.text.toString()
        note.date = dateTextView.text.toString()
        note.content = contentEditText.text.toString()

        intent = Intent()
        intent.putExtra(EXTRA_NOTE, note as Parcelable)
        setResult(SAVE_CODE, intent)
        finish()
    }

    private fun deleteNote(){
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.deleteNoteValidation))

        builder.setPositiveButton(getString(R.string.yes)){ _, _ ->
            intent = Intent()
            if (noteId != null) {
                intent.putExtra(EXTRA_ID, noteId!!)
            }
            setResult(DELETE_CODE, intent)
            finish()
        }

        builder.setNegativeButton(getString(R.string.no)){ _, _ ->
        }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    fun showVerseLayout(v : View){
        if(verseLayout.visibility == View.INVISIBLE) {
            val animate = TranslateAnimation(0f, 0f, verseLayout.height.toFloat(), 0f)
            animate.duration = 300
            verseLayout.startAnimation(animate)
            verseLayout.visibility = View.VISIBLE
        } else {
            val animate = TranslateAnimation(0f, 0f, 0f,verseLayout.height.toFloat())
            animate.duration = 300
            verseLayout.startAnimation(animate)
            verseLayout.visibility = View.INVISIBLE
        }
    }

    private fun initNote(note : Note){
        titleEditText.setText(note.title, TextView.BufferType.EDITABLE)
        dateTextView.text = note.date
        contentEditText.setText(note.content, TextView.BufferType.EDITABLE)
    }

    private fun initViewObjects(){
        titleEditText = findViewById(R.id.editTextTitle)

        dateTextView = findViewById(R.id.editTextDate)
        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE)
        val todayString = formatter.format(todayDate)
        dateTextView.text = todayString

        bookSpinner = findViewById(R.id.spinnerBooks)
        ArrayAdapter.createFromResource(
            this,
            R.array.books_array,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bookSpinner.adapter = adapter
        }

        chapterEditText = findViewById(R.id.editTextChapter)
        startVerseEditText = findViewById(R.id.editTextStartVerse)
        endVerseEditText = findViewById(R.id.editTextEndVerse)
        contentEditText = findViewById(R.id.editTextNote)
        verseLayout = findViewById<ConstraintLayout>(R.id.verseLayout)
    }
}
