package ru.n00ner.notesapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    val title: String,
    val description: String,
    val color: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}