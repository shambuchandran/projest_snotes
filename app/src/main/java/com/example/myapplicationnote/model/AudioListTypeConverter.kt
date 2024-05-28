package com.example.myapplicationnote.model

import androidx.room.TypeConverter
import com.example.myapplicationnote.fragments.AudioFile
import com.google.gson.Gson


class AudioListTypeConverter {

    @TypeConverter
    fun audioFileListFromString(audioFileString: String?): MutableList<AudioFile> {
        return Gson().fromJson(audioFileString, Array<AudioFile>::class.java).asList().toMutableList()
    }
    @TypeConverter
    fun audioFileListToString(audioFileList:MutableList<AudioFile>):String{
        return Gson().toJson(audioFileList)
    }
}