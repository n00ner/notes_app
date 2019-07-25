package ru.n00ner.notesapp.data

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao


    companion object {
        private var instance: NoteDatabase? = null
        private var notes = ""

        fun getInstance(context: Context): NoteDatabase? {
            notes = fetchNotes(context)
            if (instance == null) {
                synchronized(NoteDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDatabase::class.java, "notes_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }

        fun fetchNotes(context: Context?): String{
            try {
                val conf = context?.assets?.open("notes.json")
                val isr = InputStreamReader(conf)
                val br = BufferedReader(isr)
                var notes = ""
                var line: String?
                while (true) {
                    line = br.readLine()
                    if (line == null)
                        break
                    notes += line + "\n"
                }
                br.readLine()
                return notes

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return ""
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(instance, notes)
                    .execute()
            }
        }
    }
}

class PopulateDbAsyncTask(db: NoteDatabase?,private val notes: String) : AsyncTask<Unit, Unit, Unit>() {
    private val noteDao = db?.noteDao()

    override fun doInBackground(vararg p0: Unit?) {
        val listType = object : TypeToken<List<Note>>() { }.type
        val newList = Gson().fromJson<List<Note>>(notes, listType)
        newList.forEach {
            noteDao?.insert(it)
        }
    }
}