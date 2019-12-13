package org.wit.hillfortapp.views.hillfort

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycle_item_note.view.*
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.NoteModel

interface NoteListener {
    fun onNoteClick(noteModel: NoteModel)
}

class HillfortNotesAdapter constructor(private var notes: ArrayList<NoteModel>,
                                       private val listener: NoteListener
) : RecyclerView.Adapter<HillfortNotesAdapter.MainHolder>() {


    fun removeAt(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }

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

        fun bind(noteModel: NoteModel, listener: NoteListener) {

            if (noteModel.content.length > 30) {
                // only show part of string to prevent recycleview from resizing
                itemView.noteContent.text = "${noteModel.content.substring(0, 30)}..."
            } else {
                itemView.noteContent.text = noteModel.content
            }

            itemView.noteTitle.text = noteModel.title
            itemView.setOnClickListener { listener.onNoteClick(noteModel) }
        }
    }
}