package kaem.android.notes.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kaem.android.notes.model.Note
import java.util.ArrayList

class NotesDataBaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createNotesTable = "CREATE TABLE " + TABLE_NOTE +
                "(" +
                KEY_NOTE_ID + " INTEGER PRIMARY KEY," + // Define a primary key

                ENTITLE + " TEXT" + "," +
                DATE + " TEXT" + "," +
                CONTENT + " TEXT" +
                ")"

        db.execSQL(createNotesTable)
        }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
            onCreate(db)
        }
    }

    fun addNote(note: Note) {
        val db = writableDatabase

        db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(ENTITLE, note.title)
            values.put(DATE, note.date)
            values.put(CONTENT, note.content)
            if (note.id > 0) {
                values.put(KEY_NOTE_ID, note.id)
            }

            db.insertOrThrow(TABLE_NOTE, null, values)
            db.setTransactionSuccessful()

        } catch (e: Exception) {
            Log.e("DataBase ERROR", "Error while trying to add note to database")
        } finally {
            db.endTransaction()
        }
    }

    fun getAllNotes(): ArrayList<Note> {
        val notes = ArrayList<Note>()

        val notesSelectQuery = String.format("SELECT * FROM %s", TABLE_NOTE)

        val db = readableDatabase
        val cursor = db.rawQuery(notesSelectQuery, null)
        try {
            if (cursor!!.moveToFirst()) {
                do {
                    val newNote = Note(cursor.getInt(cursor.getColumnIndex(KEY_NOTE_ID)),
                                            cursor.getString(cursor.getColumnIndex(ENTITLE)),
                                            cursor.getString(cursor.getColumnIndex(DATE)),
                                            cursor.getString(cursor.getColumnIndex(CONTENT)))

                    notes.add(newNote)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("DataBase ERROR", "Error while trying to get notes from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return notes
    }

    fun updateNote(note: Note, id: Int): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(ENTITLE, note.title)
        values.put(DATE, note.date)
        values.put(CONTENT, note.content)

        return db.update(TABLE_NOTE, values, "$KEY_NOTE_ID = ?", arrayOf(id.toString()))
    }

    fun deleteNote(note: Note) {
        val db = writableDatabase
        db.beginTransaction()

        val clause = "$KEY_NOTE_ID = ?"
        val clauseArgs = arrayOf(note.id.toString())
        try {
            db.delete(TABLE_NOTE, clause, clauseArgs)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d("DataBase ERROR", "Error while trying to delete note")
        } finally {
            db.endTransaction()
        }
    }

    fun deleteAllNotes() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_NOTE, null, null)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d("DataBase ERROR", "Error while trying to delete all notes")
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        // Database Info
        private val DB_NAME = "notesDatabase"
        private val DB_VERSION = 1

        // Table Names
        private val TABLE_NOTE = "note"

        // Post Table Columns
        private val KEY_NOTE_ID = "id"
        private val ENTITLE = "title"
        private val DATE = "date"
        private val CONTENT = "content"

    }
}