package com.example.myapplicationnote.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplicationnote.model.AudioListTypeConverter
import com.example.myapplicationnote.model.ImageListTypeConverter
import com.example.myapplicationnote.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(AudioListTypeConverter::class,ImageListTypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao

    companion object {
        @Volatile
        private var instance: NoteDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java, "note_db")
                .build()

        fun deleteDatabase(context: Context) {
            //closeDatabase(context)
            val databaseFile = context.getDatabasePath("note_db")
            if (databaseFile.exists()) {
                databaseFile.delete()
            }
        }
    }

}