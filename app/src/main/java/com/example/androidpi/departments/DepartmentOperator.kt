package com.example.androidpi.departments

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.androidpi.departments.dbWithRoom.DepartmentOperatorConverter
import java.util.*
import kotlin.collections.ArrayList

@Entity
class DepartmentOperator
{
    @PrimaryKey
    private var id: Int = 1

    @TypeConverters(DepartmentOperatorConverter::class)
    private var departments: ArrayList<Department> = ArrayList()

    fun getDepartments(): ArrayList<Department>
    {
        return departments
    }

    fun setDepartments(newDepartments: ArrayList<Department>)
    {
        departments = newDepartments
    }

    fun setId(id: Int)
    {
        this.id = id
    }

    fun getId(): Int
    {
        return id
    }

    fun getCouriersNames(indexGroup: Int): ArrayList<String>
    {
        val arrayListForReturn: ArrayList<String> = ArrayList()
        for (i in departments[indexGroup].listOfCouriers)
        {
            arrayListForReturn.add(i.name)
        }
        return arrayListForReturn
    }

    fun getCouriersNumbersOfNotDone(indexGroup: Int): ArrayList<Int>
    {
        val arrayListForReturn: ArrayList<Int> = ArrayList()
        for (i in departments[indexGroup].listOfCouriers)
        {
            arrayListForReturn.add(i.numOfNotDone)
        }
        return arrayListForReturn
    }

    fun getCourier(airlineIndex: Int, planeIndex: Int): Courier
    {
        return departments[airlineIndex].listOfCouriers[planeIndex]
    }

    fun sortCouriers(departmentIndex: Int, sortIndex: Int)
    {
        if (sortIndex == 0)
        {
            val tempArrayListOfTasksNames: ArrayList<String> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                tempArrayListOfTasksNames.add(i.name.lowercase(Locale.ROOT))
            }
            tempArrayListOfTasksNames.sort()
            for (i in tempArrayListOfTasksNames)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    if (i == j.name.lowercase(Locale.ROOT)
                        && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }

        if (sortIndex == 1)
        {
            val tempArrayListOfTasksConditions: ArrayList<String> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                tempArrayListOfTasksConditions.add(i.transport.lowercase(Locale.ROOT))
            }
            tempArrayListOfTasksConditions.sort()
            for (i in tempArrayListOfTasksConditions)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    if (i == j.transport.lowercase(Locale.ROOT)
                        && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }

        if (sortIndex == 2)
        {
            val tempArrayListOfTasksNumbers: ArrayList<Int> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                tempArrayListOfTasksNumbers.add(i.numOfNotDone)
            }
            tempArrayListOfTasksNumbers.sort()
            for (i in tempArrayListOfTasksNumbers)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    if (i == j.numOfNotDone && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }

        if (sortIndex == 3)
        {
            val tempArrayListOfTasksNumOfParticipants: ArrayList<GregorianCalendar> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                val d: List<String> = i.dateOfBirth.split(".")
                tempArrayListOfTasksNumOfParticipants.add(GregorianCalendar(d[2].toInt(),
                    d[1].toInt(), d[0].toInt()))
            }
            tempArrayListOfTasksNumOfParticipants.sort()
            for (i in tempArrayListOfTasksNumOfParticipants)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    val d: List<String> = j.dateOfBirth.split(".")
                    val tempGregorianCalendar = GregorianCalendar(d[2].toInt(), d[1].toInt(),
                        d[0].toInt())
                    if (i == tempGregorianCalendar && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }

        if (sortIndex == 4)
        {
            val tempArrayListOfTasksNumOfParticipants: ArrayList<GregorianCalendar> = ArrayList()
            val tempArrayListOfTours: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                val d: List<String> = i.dateStart.split(".")
                tempArrayListOfTasksNumOfParticipants.add(GregorianCalendar(d[2].toInt(),
                    d[1].toInt(), d[0].toInt()))
            }
            tempArrayListOfTasksNumOfParticipants.sort()
            for (i in tempArrayListOfTasksNumOfParticipants)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    val d: List<String> = j.dateStart.split(".")
                    val tempGregorianCalendar = GregorianCalendar(d[2].toInt(), d[1].toInt(),
                        d[0].toInt())
                    if (i == tempGregorianCalendar && !tempArrayListOfTours.contains(j))
                    {
                        tempArrayListOfTours.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfTours
        }

        if (sortIndex == 5)
        {
            val tempArrayListOfTasksMaxScore: ArrayList<Int> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                tempArrayListOfTasksMaxScore.add(i.numOfDone)
            }
            tempArrayListOfTasksMaxScore.sort()
            for (i in tempArrayListOfTasksMaxScore)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    if (i == j.numOfDone && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }

        if (sortIndex == 6)
        {
            val tempArrayListOfTasksIsComplicated: ArrayList<Int> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                tempArrayListOfTasksIsComplicated.add(i.isOutsideTheCity)
            }
            tempArrayListOfTasksIsComplicated.sort()
            for (i in tempArrayListOfTasksIsComplicated)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    if (i == j.isOutsideTheCity && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }

        if (sortIndex == 7)
        {
            val tempArrayListOfTasksHints: ArrayList<String> = ArrayList()
            val tempArrayListOfCouriers: ArrayList<Courier> = ArrayList()
            for (i in departments[departmentIndex].listOfCouriers)
            {
                tempArrayListOfTasksHints.add(i.comment.lowercase(Locale.ROOT))
            }
            tempArrayListOfTasksHints.sort()
            for (i in tempArrayListOfTasksHints)
            {
                for (j in departments[departmentIndex].listOfCouriers)
                {
                    if (i == j.comment.lowercase(Locale.ROOT)
                        && !tempArrayListOfCouriers.contains(j))
                    {
                        tempArrayListOfCouriers.add(j)
                        break
                    }
                }
            }
            departments[departmentIndex].listOfCouriers = tempArrayListOfCouriers
        }
    }
}