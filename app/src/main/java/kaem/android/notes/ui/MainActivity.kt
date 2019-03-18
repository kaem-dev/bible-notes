package kaem.android.notes.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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

        noteList = dbHandler!!.getAllNotes()

        adapter = NotesAdapter(noteList, this)
        val recyclerView = findViewById<RecyclerView>(R.id.notes_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.create_note_button).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.tag != null) {
            editNote(v.tag as Int)
        } else {
            when (v?.id) {
                R.id.create_note_button -> addNote()
            }
        }
    }

    private fun editNote(index : Int) {
        val intent = Intent(this, CreateOrEditActivity::class.java)
        intent.putExtra(CreateOrEditActivity.EXTRA_NOTE, noteList[index])
        intent.putExtra(CreateOrEditActivity.EXTRA_INDEX, index)
        startActivityForResult(intent, CreateOrEditActivity.REQUEST_EDIT_NOTE)
    }

    private fun addNote() {
        val intent = Intent(this, CreateOrEditActivity::class.java)
        startActivityForResult(intent, CreateOrEditActivity.REQUEST_EDIT_NOTE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode != CreateOrEditActivity.REQUEST_EDIT_NOTE || data == null){
            return
        }
        when(resultCode){
            CreateOrEditActivity.SAVE_CODE -> {
                var note = data.getParcelableExtra<Note>(CreateOrEditActivity.EXTRA_NOTE)
                var index  = data.getIntExtra(CreateOrEditActivity.EXTRA_INDEX, -1)
                if(checkValues(note)){
                    if(index < 0) {
                        note.id = getNewId()
                        noteList.add(note)
                        dbHandler?.addNote(note)
                    } else {
                        noteList[index] = note
                        dbHandler?.updateNote(note)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            CreateOrEditActivity.DELETE_CODE ->{
                var index = data.getIntExtra(CreateOrEditActivity.EXTRA_INDEX, -1)
                if(index >= 0){
                    var note = noteList.removeAt(index)
                    dbHandler?.deleteNote(note)
                    Toast.makeText(this,  getString(R.string.noteDeleted), Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            }
        }
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
