package com.example.androidpi.departments.dbWithRoom

import androidx.room.TypeConverter
import com.example.androidpi.departments.Department
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DepartmentOperatorConverter
{
    @TypeConverter
    fun fromGO(departments: ArrayList<Department>): String
    {
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        return gson.toJson(departments)
    }

    @TypeConverter
    fun toGO(data: String): ArrayList<Department>
    {
        val gsonBuilder = GsonBuilder()
        val gson: Gson = gsonBuilder.create()
        return try {
            val type: Type = object : TypeToken<ArrayList<Department>>() {}.type
            gson.fromJson(data, type)
        } catch (e: Exception) {
            ArrayList()
        }
    }
}