package kaem.android.notes.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import kaem.android.notes.R
import kaem.android.notes.model.Note
import kaem.android.notes.utils.NotesDataBaseHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var dbHandler: NotesDataBaseHelper? = null
    lateinit var noteList : MutableList<Note>
    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = NotesDataBaseHelper(this)

        //noteList = dbHandler!!.getAllNotes()

        noteList = arrayListOf()
        noteList.add(Note(1, "Test", "Aujourd'hui", "Test"))
        noteList.add(Note(1, "Test 2", "Hier", "Test 2"))
        noteList.add(Note(1, "Test 3", "Demain", "Test 3"))
        noteList.add(Note(1, "Test 4", "Le 4", "Test 4"))

        adapter = NotesAdapter(noteList, this)
        val recyclerView = findViewById<RecyclerView>(R.id.notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.create_note_button).setOnClickListener(this)
    }

    fun addNote() {
        val intent = Intent(this, CreateOrEditActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        if (v?.tag != null) {
            Toast.makeText(this, (v.tag as Note).title, Toast.LENGTH_SHORT).show()
        } else {
            when (v?.id) {
                R.id.create_note_button -> addNote()
            }
        }
    }
}
