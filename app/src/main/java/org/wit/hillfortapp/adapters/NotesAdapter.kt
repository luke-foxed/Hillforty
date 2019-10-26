package org.wit.hillfortapp.adapters

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

class NotesAdapter constructor(private var notes: ArrayList<Note>,
                                   private val listener: NoteListener
) : RecyclerView.Adapter<NotesAdapter.MainHolder>() {

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

            itemView.noteTitle.text = note.title
            itemView.noteContent.text = note.content

            itemView.setOnClickListener { listener.onNoteClick(note) }
        }
    }
}