package com.example.myapplicationnote.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myapplicationnote.fragments.AudioFile
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id :Int,
    val noteTitle :String,
    val noteDesc :String,
    val date: String,
    val alarm:String="",
    val imagePaths:MutableList<String> = mutableListOf(),
    val audioFiles: MutableList<AudioFile>
):Parcelable
