package com.example.androidpi

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class CourierDetailsDialogFragment: android.app.DialogFragment()
{
    private val exceptionTag = "PharmacyDetailsDialogFragment"

    interface OnInputListenerSortId
    {
        fun sendInputSortId(sortId: Int)
    }

    lateinit var onInputListenerSortId: OnInputListenerSortId

    private lateinit var textViewNameTitle: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewTransportTitle: TextView
    private lateinit var textViewTransport: TextView
    private lateinit var textViewNumOfNotDoneTitle: TextView
    private lateinit var textViewNumOfNotDone: TextView
    private lateinit var textViewDateOfBirthTitle: TextView
    private lateinit var textViewDateOfBirth: TextView
    private lateinit var textViewDateStartTitle: TextView
    private lateinit var textViewDateStart: TextView
    private lateinit var textViewNumOfDoneTitle: TextView
    private lateinit var textViewNumOfDone: TextView
    private lateinit var textViewIsOutsideTheCityTitle: TextView
    private lateinit var textViewIsOutsideTheCity: TextView
    private lateinit var textViewCommentTitle: TextView
    private lateinit var textViewComment: TextView
    private lateinit var buttonDel: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonOk: Button
    private lateinit var textViewCurrSort: TextView

    private var currentIdForSort: Int = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        val view: View = inflater!!.inflate(R.layout.courier_details, container, false)
        textViewNameTitle = view.findViewById(R.id.textViewExamNameTitle)
        textViewName = view.findViewById(R.id.textViewExamName)
        textViewTransportTitle = view.findViewById(R.id.textViewTeacherNameTitle)
        textViewTransport = view.findViewById(R.id.textViewTeacherName)
        textViewNumOfNotDoneTitle = view.findViewById(R.id.textViewAuditoryTitle)
        textViewNumOfNotDone = view.findViewById(R.id.textViewAuditory)
        textViewDateOfBirthTitle = view.findViewById(R.id.textViewDateTitle)
        textViewDateOfBirth = view.findViewById(R.id.textViewDate)
        textViewDateStartTitle = view.findViewById(R.id.textViewTimeTitle)
        textViewDateStart = view.findViewById(R.id.textViewTime)
        textViewNumOfDoneTitle = view.findViewById(R.id.textViewPeopleTitle)
        textViewNumOfDone = view.findViewById(R.id.textViewPeople)
        textViewIsOutsideTheCityTitle = view.findViewById(R.id.textViewAbstractTitle)
        textViewIsOutsideTheCity = view.findViewById(R.id.textViewAbstract)
        textViewCommentTitle = view.findViewById(R.id.textViewCommentTitle)
        textViewComment = view.findViewById(R.id.textViewComment)
        buttonDel = view.findViewById(R.id.button_details_delete)
        buttonEdit = view.findViewById(R.id.button_details_edit)
        buttonOk = view.findViewById(R.id.button_details_ok)
        textViewCurrSort = view.findViewById(R.id.textViewCurrentSort)

        textViewNameTitle.setOnLongClickListener { setSortId(0) }
        textViewName.setOnLongClickListener { setSortId(0) }
        textViewTransportTitle.setOnLongClickListener { setSortId(1) }
        textViewTransport.setOnLongClickListener { setSortId(1) }
        textViewNumOfNotDoneTitle.setOnLongClickListener { setSortId(2) }
        textViewNumOfNotDone.setOnLongClickListener { setSortId(2) }
        textViewDateOfBirthTitle.setOnLongClickListener { setSortId(3) }
        textViewDateOfBirth.setOnLongClickListener { setSortId(3) }
        textViewDateStartTitle.setOnLongClickListener { setSortId(4) }
        textViewDateStart.setOnLongClickListener { setSortId(4) }
        textViewNumOfDoneTitle.setOnLongClickListener { setSortId(5) }
        textViewNumOfDone.setOnLongClickListener { setSortId(5) }
        textViewIsOutsideTheCityTitle.setOnLongClickListener { setSortId(6) }
        textViewIsOutsideTheCity.setOnLongClickListener { setSortId(6) }
        textViewCommentTitle.setOnLongClickListener { setSortId(7) }
        textViewComment.setOnLongClickListener { setSortId(7) }

        buttonDel.setOnClickListener { returnDel() }
        buttonEdit.setOnClickListener { returnEdit() }
        buttonOk.setOnClickListener { returnIdForSort() }

        val arguments: Bundle = getArguments()
        textViewName.text = arguments.getString("name")
        textViewTransport.text = arguments.getString("transport")
        textViewNumOfNotDone.text = arguments.getString("numOfNotDone")
        textViewDateOfBirth.text = arguments.getString("dateOfBirth")
        textViewDateStart.text = arguments.getString("dateStart")
        textViewNumOfDone.text = arguments.getString("numOfDone")
        if (arguments.getString("isOutsideTheCity") == "1")
        {
            textViewIsOutsideTheCity.text = "да"
        }
        else
        {
            textViewIsOutsideTheCity.text = "нет"
        }
        textViewComment.text = arguments.getString("comment")
        if (arguments.getString("connection") != "1")
        {
            buttonDel.isEnabled = false
            buttonEdit.isEnabled = false
        }

        return view
    }

    override fun onAttach(activity: Activity?)
    {
        super.onAttach(activity)
        try {
            onInputListenerSortId = getActivity() as OnInputListenerSortId
        }
        catch (e: ClassCastException)
        {
            Log.e(exceptionTag, "onAttach: ClassCastException: " + e.message)
        }
    }

    private fun setSortId(id: Int): Boolean
    {
        currentIdForSort = id
        if (currentIdForSort == 0)
        {
            textViewCurrSort.text = "Сортировка по ФИО"
        }
        else if (currentIdForSort == 1)
        {
            textViewCurrSort.text = "Сортировка по транспорту"
        }
        else if (currentIdForSort == 2)
        {
            textViewCurrSort.text = "Сортировка по кол-ву не вып."
        }
        else if (currentIdForSort == 3)
        {
            textViewCurrSort.text = "Сортировка по дате рождения"
        }
        else if (currentIdForSort == 4)
        {
            textViewCurrSort.text = "Сортировка по дате приема на работу"
        }
        else if (currentIdForSort == 5)
        {
            textViewCurrSort.text = "Сортировка по кол-ву вып."
        }
        else if (currentIdForSort == 6)
        {
            textViewCurrSort.text = "Сортировка возможности доставки за город"
        }
        else if (currentIdForSort == 7)
        {
            textViewCurrSort.text = "Сортировка по описанию"
        }
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200
            , VibrationEffect.DEFAULT_AMPLITUDE))
        return true
    }

    private fun returnIdForSort()
    {
        onInputListenerSortId.sendInputSortId(currentIdForSort)
        dialog.dismiss()
    }

    private fun returnDel()
    {
        currentIdForSort = 8
        returnIdForSort()
    }

    private fun returnEdit()
    {
        currentIdForSort = 9
        returnIdForSort()
    }
}