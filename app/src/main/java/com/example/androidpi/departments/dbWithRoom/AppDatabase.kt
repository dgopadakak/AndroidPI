package com.example.androidpi.departments.dbWithRoom

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidpi.departments.DepartmentOperator

@Database(entities = [ DepartmentOperator::class ], version=7, exportSchema = false)
abstract class AppDatabase: RoomDatabase()
{
    public abstract fun groupOperatorDao(): DepartmentOperatorDao
}