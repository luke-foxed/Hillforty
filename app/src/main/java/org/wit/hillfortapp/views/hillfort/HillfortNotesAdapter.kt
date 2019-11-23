package org.wit.hillfortapp.views.hillfort

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycle_item_note.view.*
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.Note

interface NoteListener {
    fun onNoteClick(note: Note)
}

class HillfortNotesAdapter constructor(private var notes: ArrayList<Note>,
                                   private val listener: NoteListener
) : RecyclerView.Adapter<HillfortNotesAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycle_item_note,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val note = notes[holder.adapterPosition]
        holder.bind(note, listener)
    }

    override fun getItemCount(): Int = notes.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(note: Note, listener: NoteListener) {

            if (note.content.length > 30) {
                // only show part of string to prevent recycleview from resizing
                itemView.noteContent.text = note.content.substring(0, 30) + "..."
            } else {
                itemView.noteContent.text = note.content
            }

            itemView.noteTitle.text = note.title
            itemView.setOnClickListener { listener.onNoteClick(note) }
        }
    }
}