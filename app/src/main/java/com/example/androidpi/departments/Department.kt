package com.example.androidpi.departments

data class Department(
    val name: String,
    var listOfCouriers: ArrayList<Courier> = ArrayList()
)
