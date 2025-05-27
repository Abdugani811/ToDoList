package com.example.todolist

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.databinding.AddTaskDialogViewBinding
import com.example.todolist.databinding.UpdateDialogViewBinding
import java.time.LocalDateTime
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val tasksList = mutableListOf<TasksModel>()
    private lateinit var dbManager: TasksDbManager
    lateinit var tasksAdapter: TasksAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbManager = TasksDbManager(this)
        dbManager.onCreate()
        tasksAdapter = TasksAdapter(this)

        binding.floatingActionButton.setOnClickListener {
            val dialogViewBinding = AddTaskDialogViewBinding.inflate(layoutInflater, null, false)
            val dialog = AlertDialog.Builder(this).setView(dialogViewBinding.root).create()
            dialogViewBinding.addTaskBtn.setOnClickListener {
                val taskTitle = dialogViewBinding.enteriTask.text

                if (taskTitle.isEmpty()) {
                    Toast.makeText(this, "Enter task", Toast.LENGTH_SHORT).show()
                } else {
                    val currentDateTimeForTask = LocalDateTime.now() // Yangi obyekt yaratiladi
                    dbManager.insertTask(
                        TasksModel(
                            Tasktitle = taskTitle.toString(),
                            Addhour = currentDateTimeForTask.hour,
                            Addminute = currentDateTimeForTask.minute,
                            IsChecked = false
                        )
                    )
                }

                tasksAdapter.submitList(fetchTasks())
                tasksAdapter.notifyItemInserted(tasksList.size)
                dialog.dismiss()
            }
            dialog.show()
        }

        fetchTasks()
        tasksAdapter.submitList(tasksList)
        val myLayoutManager = LinearLayoutManager(this)
        binding.tasksView.apply {
            adapter = tasksAdapter
            layoutManager = myLayoutManager
        }

        tasksAdapter.setTaskLitener(object : TaskLitener {
            override fun onDeleteTask(position: Int) {
                tasksList[position].id?.let { dbManager.deleteTask(it) }
                tasksAdapter.submitList(fetchTasks())
                tasksAdapter.notifyItemRemoved(position)
            }

            override fun isChecked(id: Int) {
                dbManager.updateIsChecked(id, true)
                tasksAdapter.submitList(fetchTasks())
                tasksAdapter.notifyItemInserted(id)

            }

            override fun unChecked(id: Int) {
                dbManager.updateIsChecked(id, false)
                tasksAdapter.submitList(fetchTasks())
                tasksAdapter.notifyItemInserted(id)
            }

            override fun updateTask(tasksModel: TasksModel) =
                run {
                    val dialogViewBinding =
                        UpdateDialogViewBinding.inflate(layoutInflater, null, false)
                    val dialog =
                        AlertDialog.Builder(this@MainActivity).setView(dialogViewBinding.root)
                            .create()

                    dialogViewBinding.updateTaskTitle.text = tasksModel.Tasktitle
                    dialogViewBinding.updateTask.setText(tasksModel.Tasktitle)
                    dialogViewBinding.updateTaskBtn.setOnClickListener {

                        val taskTitle = dialogViewBinding.updateTask.text.toString()
                        val currentDateTimeForTask = LocalDateTime.now()

                        val updatedTasksModel = tasksModel.copy(
                            Tasktitle = taskTitle,
                            Addhour = currentDateTimeForTask.hour,
                            Addminute = currentDateTimeForTask.minute,
                            IsChecked = false
                        )
                        dbManager.updateTask(updatedTasksModel)


                        tasksAdapter.submitList(fetchTasks())
                        tasksAdapter.notifyItemRangeChanged(
                            tasksList.indexOf(tasksModel), tasksModel.id!!
                        )

                        dialog.dismiss()
                    }

                    dialog.show()
                }
        })

        Log.d("TAG", "onCreate: ${fetchTasks()}")

        val recyclerView = binding.recyclerView
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val daysInMonth = getDaysInMonth(month, year)
        val startingDay = getStartingDay(month, year)
        val adapter = CalendarAdapter(daysInMonth, startingDay)
        recyclerView.adapter = adapter
    }

    private fun fetchTasks(): List<TasksModel> {
        val cursor = dbManager.fetch()
        tasksList.clear()
        if (cursor != null) {
            val idIndex = cursor.getColumnIndex(TasksSQLiteHelper.id)
            val titleIndex = cursor.getColumnIndex(TasksSQLiteHelper.TaskTitle)
            val timeHourIndex = cursor.getColumnIndex(TasksSQLiteHelper.AddTimeHour)
            val timeMinuteIndex = cursor.getColumnIndex(TasksSQLiteHelper.AddTimeMinute)
            val isCheckedIndex = cursor.getColumnIndex(TasksSQLiteHelper.IsChecked)

            do {
                val id = cursor.getInt(idIndex)
                val title = cursor.getString(titleIndex)
                val timeHour = cursor.getInt(timeHourIndex)
                val timeMinute = cursor.getInt(timeMinuteIndex)
                val isChecked = cursor.getInt(isCheckedIndex) != 0

                val tasksModel = TasksModel(id, title, timeHour, timeMinute, isChecked)

                tasksList.add(tasksModel)

            } while (cursor.moveToNext())
        }
        return tasksList
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun getStartingDay(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
}
