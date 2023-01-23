package com.example.androidpi.departments

data class Courier(
    val name: String,
    val transport: String,
    val numOfNotDone: Int,
    val dateOfBirth: String,
    val dateStart: String,
    val numOfDone: Int,
    val isOutsideTheCity: Int,
    val comment: String
)
