package ru.n00ner.notesapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_note.*
import petrov.kristiyan.colorpicker.ColorPicker



class EditNoteActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "note_id"
        const val EXTRA_TITLE = "note_title"
        const val EXTRA_DESCRIPTION = "note_description"
        const val EXTRA_COLOR = "note_color"
    }

    private var pickedColor = "#ffffff"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        if (intent.hasExtra(EXTRA_ID)) {
            title = getString(R.string.title_edit)
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_description.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            color_view.setBackgroundColor(Color.parseColor(intent.getStringExtra(EXTRA_COLOR)))
            pickedColor = intent.getStringExtra(EXTRA_COLOR)
        } else {
            title = getString(R.string.title_create)
        }

        btn_pick_color.setOnClickListener {
            val colorPicker = ColorPicker(this@EditNoteActivity)
            colorPicker.show()
            colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    pickedColor = String.format("#%06X", 0xFFFFFF and color)
                    color_view.setBackgroundColor(Color.parseColor(pickedColor))
                }

                override fun onCancel() {

                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_note -> {
                saveNote()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        if (edit_text_title.text.toString().trim().isBlank() || edit_text_description.text.toString().trim().isBlank()) {
            Toast.makeText(this, getString(R.string.exp_blank), Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent().apply {
            putExtra(EXTRA_TITLE, edit_text_title.text.toString())
            putExtra(EXTRA_DESCRIPTION, edit_text_description.text.toString())
            putExtra(EXTRA_COLOR, pickedColor)
            if (intent.getIntExtra(EXTRA_ID, -1) != -1) {
                putExtra(EXTRA_ID, intent.getIntExtra(EXTRA_ID, -1))
            }
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }
}
