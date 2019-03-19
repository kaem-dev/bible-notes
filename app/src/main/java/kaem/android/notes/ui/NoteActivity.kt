package kaem.android.notes.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import kaem.android.notes.R
import kaem.android.notes.model.Note

class NoteActivity : AppCompatActivity() {
    private lateinit var titleTextView : TextView
    private lateinit var dateTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var noteExtra : Note

    companion object {
        const val REQUEST_NOTE = 0
        const val EXTRA_NOTE  = "note"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_note)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
                editNote()
                return true
            }
        }
        return  super.onOptionsItemSelected(item)
    }

    private fun editNote() {
        Toast.makeText(this, "Modifier la note", Toast.LENGTH_SHORT).show()
    }

    private fun initNote(note : Note) {
        titleTextView.text = noteExtra.title
        dateTextView.text = noteExtra.date
        contentTextView.text = noteExtra.content
    }

    private fun initViewObjects() {
        titleTextView = findViewById(R.id.title_text_view)
        dateTextView = findViewById(R.id.date_text_view)
        contentTextView = findViewById(R.id.content_text_view)
    }
}
