package com.example.androidpi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class EditCourierActivity : AppCompatActivity()
{
    private lateinit var editName: EditText
    private lateinit var editTransport: EditText
    private lateinit var editNumOfNotDone: EditText
    private lateinit var editDateOfBirth: EditText
    private lateinit var editDateStart: EditText
    private lateinit var editNumOfDone: EditText
    private lateinit var editIsOutsideTheCity: EditText
    private lateinit var editComment: EditText

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_courier)

        editName = findViewById(R.id.editTextExamName)
        editTransport = findViewById(R.id.editTextTeacherName)
        editNumOfNotDone = findViewById(R.id.editTextAuditory)
        editDateOfBirth = findViewById(R.id.editTextDate)
        editDateStart = findViewById(R.id.editTextTime)
        editNumOfDone = findViewById(R.id.editTextPeople)
        editIsOutsideTheCity = findViewById(R.id.editTextAbstract)
        editComment = findViewById(R.id.editTextComment)

        val action = intent.getIntExtra("action", 1)

        findViewById<Button>(R.id.button_confirm).setOnClickListener { confirmChanges(action) }

        if (action == 2)
        {
            editName.setText(intent.getStringExtra("name") as String)
            editTransport.setText(intent.getStringExtra("transport") as String)
            editNumOfNotDone.setText(intent.getStringExtra("numOfNotDone") as String)
            editDateOfBirth.setText(intent.getStringExtra("dateOfBirth") as String)
            editDateStart.setText(intent.getStringExtra("dateStart") as String)
            editNumOfDone.setText(intent.getStringExtra("numOfDone") as String)
            if (intent.getStringExtra("isOutsideTheCity") as String == "1")
            {
                editIsOutsideTheCity.setText("да")
            }
            else
            {
                editIsOutsideTheCity.setText("нет")
            }
            editComment.setText(intent.getStringExtra("comment") as String)
        }
    }

    private fun confirmChanges(action: Int)
    {
        if (editName.text.toString() != "" && editTransport.text.toString() != ""
            && editNumOfNotDone.text.toString() != "" && editDateOfBirth.text.toString() != ""
            && editDateStart.text.toString() != "" && editNumOfDone.text.toString() != ""
            && editIsOutsideTheCity.text.toString() != "")
        {
            if (editIsOutsideTheCity.text.toString().trim().lowercase(Locale.ROOT) == "да"
                || editIsOutsideTheCity.text.toString().trim().lowercase(Locale.ROOT) == "нет")
            {
                if (isDateValid(editDateStart.text.toString().trim())
                    && isDateValid(editDateOfBirth.text.toString().trim()))
                {
                    val intent = Intent(this@EditCourierActivity,
                        MainActivity::class.java)
                    intent.putExtra("action",    action)
                    intent.putExtra("name",      editName.text.toString().trim())
                    intent.putExtra("transport",   editTransport.text.toString().trim())
                    intent.putExtra("numOfNotDone",    editNumOfNotDone.text.toString().trim().toInt())
                    intent.putExtra("dateOfBirth",  editDateOfBirth.text.toString().trim())
                    intent.putExtra("dateStart", editDateStart.text.toString().trim())
                    intent.putExtra("numOfDone",   editNumOfDone.text.toString().trim().toInt())
                    if (editIsOutsideTheCity.text.toString().trim().lowercase(Locale.ROOT) == "да")
                    {
                        intent.putExtra("isOutsideTheCity", 1)
                    }
                    else
                    {
                        intent.putExtra("isOutsideTheCity", 0)
                    }
                    intent.putExtra("comment", editComment.text.toString().trim())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else
                {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Проверьте даты!",
                        Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            else
            {
                val toast = Toast.makeText(
                    applicationContext,
                    "Поле \"Доставляет за город\" поддерживает только " +
                            "значения \"да\" или \"нет\"!",
                    Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        else
        {
            val toast = Toast.makeText(
                applicationContext,
                "Заполните обязательные поля!",
                Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isDateValid(date: String?): Boolean
    {
        val myFormat = SimpleDateFormat("dd.MM.yyyy")
        myFormat.isLenient = false
        return try
        {
            if (date != null)
            {
                myFormat.parse(date)
            }
            true
        }
        catch (e: Exception)
        {
            false
        }
    }
}