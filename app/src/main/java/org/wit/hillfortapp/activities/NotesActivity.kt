package org.wit.hillfortapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.note_placement.*
import org.jetbrains.anko.AnkoLogger
import org.wit.hillfortapp.MainApp
import org.wit.hillfortapp.R
import org.wit.hillfortapp.models.Note

class NotesActivity : MainActivity(), AnkoLogger {

    lateinit var app: MainApp
    var note = Note()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_notes, content_frame)
        app = application as MainApp

        if (intent.hasExtra("note_edit")) {
            note = intent.extras?.getParcelable("note_edit")!!
            noteActivityTitle.text = note.title
            noteActivityContent.text = note.content
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.noteMenuBack -> {
                finish()
            }
            R.id.noteMenuEdit -> {

            }
        }
        return super.onOptionsItemSelected(item)

    }
}