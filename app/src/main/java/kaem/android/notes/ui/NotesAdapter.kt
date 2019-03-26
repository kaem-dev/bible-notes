package kaem.android.notes.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kaem.android.notes.R
import kaem.android.notes.model.Note


import kotlinx.android.synthetic.main.item_note.view.*

class NotesAdapter(
    private val noteList: List<Note>,
    private val itemClickListener: View.OnClickListener?,
    private val itemLongClickListener: View.OnLongClickListener?
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = noteList[position]
        holder.mTitleView.text = item.title
        holder.mDateView.text = item.date

        with(holder.mCardView) {
            tag = position
            setOnClickListener(itemClickListener)
            setOnLongClickListener(itemLongClickListener)
        }
    }

    override fun getItemCount(): Int = noteList.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mCardView = mView.card_view
        val mTitleView: TextView = mView.note_title
        val mDateView: TextView = mView.note_date

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
