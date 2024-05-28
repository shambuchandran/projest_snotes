package com.example.myapplicationnote.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ImageListTypeConverter {
    @TypeConverter
    fun fromStringToList(value: String): MutableList<String> {
        val listType = object : TypeToken<MutableList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListToString(list: MutableList<String>): String {
        return Gson().toJson(list)
    }

}