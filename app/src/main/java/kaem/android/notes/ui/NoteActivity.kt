package kaem.android.notes.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kaem.android.notes.R
import kaem.android.notes.model.Note
import kaem.android.notes.utils.NotesDataBaseHelper

class NoteActivity : AppCompatActivity() {
    private lateinit var titleTextView : TextView
    private lateinit var dateTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var noteExtra : Note
    private var dbHandler: NotesDataBaseHelper? = null

    companion object {
        const val REQUEST_NOTE = 3
        const val UPDATE_CODE = 4
        const val EXTRA_NOTE  = "note"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_note)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        dbHandler = NotesDataBaseHelper(this)

        initViewObjects()

        if (intent.hasExtra(EXTRA_NOTE)){
            noteExtra = intent.getParcelableExtra(EXTRA_NOTE)
            initNote(noteExtra)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.edit_note -> {
                val intent = Intent(this, CreateOrEditActivity::class.java)
                intent.putExtra(CreateOrEditActivity.EXTRA_NOTE, noteExtra)
                startActivityForResult(intent, CreateOrEditActivity.REQUEST_EDIT_NOTE)
                return true
            }
            R.id.delete_note -> {
                confirmToDelete()
                return true
            }
            R.id.share_note -> {
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = "${noteExtra.title} (${noteExtra.date})\n\n${noteExtra.content}"
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)))
                return true
            }
        }
        return  super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != CreateOrEditActivity.REQUEST_EDIT_NOTE || data == null){
            return
        }
        when(resultCode){
            CreateOrEditActivity.SAVE_CODE -> {
                val note = data.getParcelableExtra<Note>(CreateOrEditActivity.EXTRA_NOTE)
                editNote(note)
            }
            CreateOrEditActivity.DELETE_CODE ->{
                deleteNote()
            }
        }
    }

    private fun editNote(note : Note) {
        if(checkValues(note)){
            dbHandler?.updateNote(note)
            noteExtra = note
            initNote(noteExtra)
            intent = Intent()
            setResult(NoteActivity.UPDATE_CODE, intent)
        }
    }

    private fun confirmToDelete() {
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.deleteNoteValidation))

        builder.setPositiveButton(getString(R.string.yes)){ _, _ ->
            deleteNote()
        }

        builder.setNegativeButton(getString(R.string.no)){ _, _ -> }

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    private fun deleteNote() {
        dbHandler?.deleteNote(noteExtra.id)
        intent = Intent()
        setResult(NoteActivity.UPDATE_CODE, intent)
        finish()
    }

    private fun checkValues(note: Note) : Boolean{
        return !note.title.trim().isBlank()
    }

    private fun initNote(note : Note) {
        titleTextView.text = note.title
        dateTextView.text = note.date
        contentTextView.text = note.content
    }

    private fun initViewObjects() {
        titleTextView = findViewById(R.id.title_text_view)
        dateTextView = findViewById(R.id.date_text_view)
        contentTextView = findViewById(R.id.content_text_view)
    }
}
