package ru.n00ner.notesapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import ru.n00ner.notesapp.adapters.NoteAdapter
import ru.n00ner.notesapp.data.Note
import ru.n00ner.notesapp.viewmodels.NoteViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_NOTE_ACTION = 1
        const val EDIT_NOTE_ACTION = 2
    }

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_create_note.setOnClickListener {
            startActivityForResult(
                Intent(this, EditNoteActivity::class.java),
                ADD_NOTE_ACTION
            )
        }



        notes_list.layoutManager = LinearLayoutManager(this)
        notes_list.setHasFixedSize(true)

        var adapter = NoteAdapter()

        notes_list.adapter = adapter

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        noteViewModel.getAllNotes().observe(this, Observer<List<Note>> {
            adapter.submitList(it)
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                Toast.makeText(baseContext, "Note Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        ).attachToRecyclerView(notes_list)

        adapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(note: Note) {
                var intent = Intent(baseContext, EditNoteActivity::class.java)
                intent.putExtra(EditNoteActivity.EXTRA_ID, note.id)
                intent.putExtra(EditNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(EditNoteActivity.EXTRA_DESCRIPTION, note.description)
                intent.putExtra(EditNoteActivity.EXTRA_COLOR, note.color)

                startActivityForResult(intent, EDIT_NOTE_ACTION)
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_ACTION && resultCode == Activity.RESULT_OK) {
            val newNote = Note(
                data!!.getStringExtra(EditNoteActivity.EXTRA_TITLE),
                data.getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION),
                data.getStringExtra(EditNoteActivity.EXTRA_COLOR)
            )
            noteViewModel.insert(newNote)

            Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
        } else if (requestCode == EDIT_NOTE_ACTION && resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra(EditNoteActivity.EXTRA_ID, -1)

            if (id == -1) {
                Toast.makeText(this, getString(R.string.cant_update), Toast.LENGTH_SHORT).show()
            }

            val updateNote = Note(
                data!!.getStringExtra(EditNoteActivity.EXTRA_TITLE),
                data.getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION),
                data.getStringExtra(EditNoteActivity.EXTRA_COLOR)
            )
            updateNote.id = data.getIntExtra(EditNoteActivity.EXTRA_ID, -1)
            noteViewModel.update(updateNote)

        } else {
            Toast.makeText(this, getString(R.string.cant_save), Toast.LENGTH_SHORT).show()
        }


    }
}
