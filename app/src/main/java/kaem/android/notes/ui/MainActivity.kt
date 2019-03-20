package kaem.android.notes.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import kaem.android.notes.R
import kaem.android.notes.model.Note
import kaem.android.notes.utils.NotesDataBaseHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var dbHandler: NotesDataBaseHelper? = null
    private lateinit var noteList : MutableList<Note>
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHandler = NotesDataBaseHelper(this)

        noteList = dbHandler!!.getAllNotes()

        adapter = NotesAdapter(noteList, this)
        val recyclerView = findViewById<RecyclerView>(R.id.notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.create_note_button).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.tag != null) {
            openNote(v.tag as Int)
        } else {
            when (v?.id) {
                R.id.create_note_button -> addNote()
            }
        }
    }

    private fun openNote(index: Int) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(NoteActivity.EXTRA_NOTE, noteList[index])
        startActivityForResult(intent, NoteActivity.REQUEST_NOTE)
    }

    private fun addNote() {
        val intent = Intent(this, CreateOrEditActivity::class.java)
        startActivityForResult(intent, CreateOrEditActivity.REQUEST_EDIT_NOTE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode != CreateOrEditActivity.REQUEST_EDIT_NOTE && requestCode != NoteActivity.REQUEST_NOTE) ||
            data == null){
            return
        }
        when(resultCode){
            NoteActivity.UPDATE_CODE -> {
                Log.i("TEST", "ça passe ici")
                updateList()
            }
            CreateOrEditActivity.SAVE_CODE -> {
                val note = data.getParcelableExtra<Note>(CreateOrEditActivity.EXTRA_NOTE)
                if(checkValues(note)){
                    if(note.id < 0) {
                        note.id = getNewId()
                        dbHandler?.addNote(note)
                        updateList()
                    } else {
                        dbHandler?.updateNote(note)
                        updateList()
                    }
                }
            }
            CreateOrEditActivity.DELETE_CODE ->{
                val id = data.getIntExtra(CreateOrEditActivity.EXTRA_ID, -1)
                if(id >= 0){
                    dbHandler?.deleteNote(id)
                    Toast.makeText(this,  getString(R.string.noteDeleted), Toast.LENGTH_SHORT).show()
                    updateList()
                }
            }
        }
    }

    private fun updateList() {
        noteList.clear()
        noteList.addAll(dbHandler!!.getAllNotes())
        adapter.notifyDataSetChanged()
    }

    private fun checkValues(note: Note) : Boolean{
        return !note.title.trim().isBlank()
    }

    private fun getNewId() : Int {
        return if(noteList.size == 0)
            1
        else
            noteList.last().id + 1
    }
}
