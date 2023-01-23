package com.example.androidpi.departments.dbWithRoom

import androidx.room.*
import com.example.androidpi.departments.DepartmentOperator

@Dao
interface DepartmentOperatorDao
{
    @Query("SELECT * FROM DepartmentOperator")
    fun getAll(): List<DepartmentOperator?>?

    @Query("SELECT * FROM DepartmentOperator WHERE id = :id")
    fun getById(id: Int): DepartmentOperator

    @Insert
    fun insert(go: DepartmentOperator?)

    @Delete
    fun delete(go: DepartmentOperator?)
}