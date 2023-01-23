package com.example.androidpi

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidpi.departments.Department
import com.example.androidpi.departments.DepartmentOperator
import com.example.androidpi.departments.Courier
import com.example.androidpi.departments.dbWithRoom.DepartmentOperatorDao
import com.example.androidpi.departments.dbWithRoom.App
import com.example.androidpi.departments.dbWithRoom.AppDatabase
import com.example.androidpi.databinding.ActivityMainBinding
import com.example.androidpi.forRecyclerView.CustomRecyclerAdapterForExams
import com.example.androidpi.forRecyclerView.RecyclerItemClickListener
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    CourierDetailsDialogFragment.OnInputListenerSortId
{
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()
    private val serverIP = "192.168.1.69"
    private val serverPort = 9789
    private lateinit var connection: Connection
    private var connectionStage: Int = 0
    private var startTime: Long = 0

    private lateinit var db: AppDatabase
    private lateinit var deoDao: DepartmentOperatorDao

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nv: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerViewPlanes: RecyclerView
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val data: Intent? = result.data
            processOnActivityResult(data)
        }
    }

    private var deo: DepartmentOperator = DepartmentOperator()
    private var currentDepartmentID: Int = -1
    private var currentCourierID: Int = -1
    private var waitingForUpdate: Boolean = false
    private lateinit var departmentTitle: String

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        nv = binding.navView
        nv.setNavigationItemSelectedListener(this)
        toolbar = findViewById(R.id.toolbar)
        toolbar.apply { setNavigationIcon(R.drawable.ic_my_menu) }
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        progressBar = findViewById(R.id.progressBar)
        recyclerViewPlanes = findViewById(R.id.recyclerViewExams)
        recyclerViewPlanes.visibility = View.INVISIBLE
        recyclerViewPlanes.layoutManager = LinearLayoutManager(this)

        recyclerViewPlanes.addOnItemTouchListener(
            RecyclerItemClickListener(
                recyclerViewPlanes,
                object : RecyclerItemClickListener.OnItemClickListener
                {
                    override fun onItemClick(view: View, position: Int)
                    {
                        currentCourierID = position
                        val toast = Toast.makeText(
                            applicationContext,
                            "Транспорт: ${deo.getCourier(currentDepartmentID, currentCourierID)
                                .transport}",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    }
                    override fun onItemLongClick(view: View, position: Int)
                    {
                        currentCourierID = position
                        val examDetails = CourierDetailsDialogFragment()
                        val tempExam = deo.getCourier(currentDepartmentID, currentCourierID)
                        val bundle = Bundle()
                        bundle.putString("name", tempExam.name)
                        bundle.putString("transport", tempExam.transport)
                        bundle.putString("numOfNotDone", tempExam.numOfNotDone.toString())
                        bundle.putString("dateOfBirth", tempExam.dateOfBirth)
                        bundle.putString("dateStart", tempExam.dateStart)
                        bundle.putString("numOfDone", tempExam.numOfDone.toString())
                        bundle.putString("isOutsideTheCity", tempExam.isOutsideTheCity.toString())
                        bundle.putString("comment", tempExam.comment)
                        bundle.putString("connection", connectionStage.toString())
                        examDetails.arguments = bundle
                        examDetails.show(fragmentManager, "MyCustomDialog")
                    }
                }
            )
        )

        db = App.instance?.database!!
        deoDao = db.groupOperatorDao()
        startTime = System.currentTimeMillis()
        connection = Connection(serverIP, serverPort, "REFRESH", this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean       // Инициализация Toolbar
    {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean      // Обновление вида Toolbar
    {
        if (currentDepartmentID != -1 && connectionStage == 1)
        {
            menu.getItem(0).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean     // Слушатель "плюсика"
    {
        val id = item.itemId
        if (id == R.id.action_add)
        {
            val intent = Intent()
            intent.setClass(this, EditCourierActivity::class.java)
            intent.putExtra("action", 1)
            resultLauncher.launch(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class Connection(
        private val SERVER_IP: String,
        private val SERVER_PORT: Int,
        private val refreshCommand: String,
        private val activity: Activity
    ) {
        private var outputServer: PrintWriter? = null
        private var inputServer: BufferedReader? = null
        var thread1: Thread? = null
        private var threadT: Thread? = null

        internal inner class Thread1Server : Runnable       // Подключение
        {
            override fun run()
            {
                val socket: Socket
                try {
                    socket = Socket(SERVER_IP, SERVER_PORT)
                    outputServer = PrintWriter(socket.getOutputStream())
                    inputServer = BufferedReader(InputStreamReader(socket.getInputStream()))
                    Thread(Thread2Server()).start()
                    sendDataToServer(refreshCommand)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        internal inner class Thread2Server : Runnable       // Прием
        {
            override fun run() {
                while (true) {
                    try {
                        val message = inputServer!!.readLine()
                        if (message != null)
                        {
                            activity.runOnUiThread { processingInputStream(message) }
                        } else {
                            thread1 = Thread(Thread1Server())
                            thread1!!.start()
                            return
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        internal inner class Thread3Server(private val message: String) : Runnable  // Отправка
        {
            override fun run()
            {
                outputServer!!.write(message)
                outputServer!!.flush()
            }
        }

        internal inner class ThreadT : Runnable     // Отсчет пяти секунд
        {
            override fun run() {
                while (true)
                {
                    if (System.currentTimeMillis() - startTime > 5000L && connectionStage == 0)
                    {
                        activity.runOnUiThread { val toast = Toast.makeText(
                            applicationContext,
                            "Работа оффлайн",
                            Toast.LENGTH_SHORT)
                            toast.show() }
                        connectionStage = -1
                        activity.runOnUiThread { progressBar.visibility = View.INVISIBLE }
                        deo = deoDao.getById(1)
                        for (i in 0 until deo.getDepartments().size)
                        {
                            activity.runOnUiThread { nv.menu.add(0, i, 0,
                                deo.getDepartments()[i].name as CharSequence) }
                        }
                    }
                }
            }
        }

        fun sendDataToServer(text: String)      // Отправка данных на сервер
        {
            Thread(Thread3Server(text + "\n")).start()
        }

        private fun processingInputStream(text: String)     // Обработка сообщения от сервера
        {
            deoDao.delete(DepartmentOperator())
            val tempGo: DepartmentOperator = gson.fromJson(text, DepartmentOperator::class.java)
            deoDao.insert(tempGo)

            if (connectionStage != 1)
            {
                val toast = Toast.makeText(
                    applicationContext,
                    "Работа с сервером.",
                    Toast.LENGTH_SHORT)
                toast.show()
            }

            progressBar.visibility = View.INVISIBLE
            for (i in 0 until deo.getDepartments().size)
            {
                nv.menu.removeItem(i)
            }
            val tempArrayListDepartments: ArrayList<Department> = tempGo.getDepartments()
            deo.setDepartments(tempArrayListDepartments)
            for (i in 0 until tempArrayListDepartments.size)
            {
                nv.menu.add(
                    0, i, 0,
                    tempArrayListDepartments[i].name as CharSequence
                )
            }
            if (waitingForUpdate || connectionStage == -1)
            {
                waitingForUpdate = false
                if (currentDepartmentID != -1)
                {
                    recyclerViewPlanes.adapter = CustomRecyclerAdapterForExams(
                        deo.getCouriersNames(currentDepartmentID),
                        deo.getCouriersNumbersOfNotDone(currentDepartmentID)
                    )
                }
            }
            connectionStage = 1
        }

        init {
            thread1 = Thread(Thread1Server())
            thread1!!.start()
            threadT = Thread(ThreadT())
            threadT!!.start()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean      // Слушатель NavigationView
    {
        drawerLayout.closeDrawer(GravityCompat.START)
        departmentTitle = "${item.title}"
        toolbar.title = departmentTitle
        invalidateOptionsMenu()
        currentDepartmentID = item.itemId
        recyclerViewPlanes.adapter = CustomRecyclerAdapterForExams(
            deo.getCouriersNames(currentDepartmentID),
            deo.getCouriersNumbersOfNotDone(currentDepartmentID))
        recyclerViewPlanes.visibility = View.VISIBLE
        return true
    }

    fun delCourier()        // Удаление курьера
    {
        connection.sendDataToServer("d$currentDepartmentID,$currentCourierID")
        waitingForUpdate = true
    }

    override fun sendInputSortId(sortId: Int)
    {
        if (sortId > -1 && sortId < 8)      // Сортировка
        {
            deo.sortCouriers(currentDepartmentID, sortId)
            if (connectionStage == 1)
            {
                connection.sendDataToServer("u" + gson.toJson(deo))
            }
            toolbar.title = when (sortId)
            {
                0 -> "$departmentTitle сорт. ФИО"
                1 -> "$departmentTitle сорт. Транспорт"
                2 -> "$departmentTitle сорт. Не вып."
                3 -> "$departmentTitle сорт. Дата ождения"
                4 -> "$departmentTitle сорт. Дата пр."
                5 -> "$departmentTitle сорт. Вып."
                6 -> "$departmentTitle сорт. За город"
                7 -> "$departmentTitle сорт. Описание"
                else -> departmentTitle
            }
        }
        if (sortId == 8)        // Удаление (диалоговое окно с подтверждением)
        {
            val manager: FragmentManager = supportFragmentManager
            val myDialogFragmentDelCourier = MyDialogFragmentDelCourier()
            val bundle = Bundle()
            bundle.putString("name", deo.getCourier(currentDepartmentID, currentCourierID).name)
            myDialogFragmentDelCourier.arguments = bundle
            myDialogFragmentDelCourier.show(manager, "myDialog")
        }
        if (sortId == 9)        // Изменение
        {
            val tempCourier = deo.getCourier(currentDepartmentID, currentCourierID)
            val intent = Intent()
            intent.setClass(this, EditCourierActivity::class.java)
            intent.putExtra("action", 2)
            intent.putExtra("name", tempCourier.name)
            intent.putExtra("transport", tempCourier.transport)
            intent.putExtra("numOfNotDone", tempCourier.numOfNotDone.toString())
            intent.putExtra("dateOfBirth", tempCourier.dateOfBirth)
            intent.putExtra("dateStart", tempCourier.dateStart)
            intent.putExtra("numOfDone", tempCourier.numOfDone.toString())
            intent.putExtra("isOutsideTheCity", tempCourier.isOutsideTheCity.toString())
            intent.putExtra("comment", tempCourier.comment)
            resultLauncher.launch(intent)
        }
        recyclerViewPlanes.adapter = CustomRecyclerAdapterForExams(
            deo.getCouriersNames(currentDepartmentID),
            deo.getCouriersNumbersOfNotDone(currentDepartmentID))
    }

    private fun processOnActivityResult(data: Intent?)
    {
        val action = data!!.getIntExtra("action", -1)
        val model = data.getStringExtra("name")
        val color = data.getStringExtra("transport")
        val number = data.getIntExtra("numOfNotDone", -1)
        val factory = data.getStringExtra("dateOfBirth")
        val productionDate = data.getStringExtra("dateStart")
        val seats = data.getIntExtra("numOfDone", -1)
        val isCargo = data.getIntExtra("isOutsideTheCity", 0)
        val comment = data.getStringExtra("comment")
        val tempCourier = Courier(model!!, color!!, number, factory!!, productionDate!!, seats
            , isCargo, comment!!)
        val tempPlaneJSON: String = gson.toJson(tempCourier)

        if (action == 1)
        {
            val tempStringToSend = "a${deo.getDepartments()[currentDepartmentID].name}##$tempPlaneJSON"
            connection.sendDataToServer(tempStringToSend)
            waitingForUpdate = true
        }
        if (action == 2)
        {
            val tempStringToSend = "e$currentDepartmentID,$currentCourierID##$tempPlaneJSON"
            connection.sendDataToServer(tempStringToSend)
            waitingForUpdate = true
        }
        if (action == -1)
        {
            val toast = Toast.makeText(
                applicationContext,
                "Ошибка добавления/изменения!",
                Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}