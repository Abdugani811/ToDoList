package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase


class TasksDbManager(val context: Context) {
    private lateinit var sqLiteHelper: TasksSQLiteHelper
    private lateinit var database: SQLiteDatabase

    fun onCreate() {
        sqLiteHelper = TasksSQLiteHelper(context)
        database = sqLiteHelper.writableDatabase
    }

    fun insertTask(tasksModel: TasksModel) {
        val cv = database
            .insert("tasks", null, ContentValues().apply {
                put("TaskTitle", tasksModel.Tasktitle)
                put("AddTimeHour", tasksModel.Addhour)
                put("AddTimeMinute", tasksModel.Addminute)
                put("isChecked", tasksModel.IsChecked)
            })
    }

    fun fetch(): Cursor? {
        val cursor = database.query(
            TasksSQLiteHelper.TableName,
            arrayOf(
                TasksSQLiteHelper.id,
                TasksSQLiteHelper.TaskTitle,
                TasksSQLiteHelper.AddTimeHour,
                TasksSQLiteHelper.AddTimeMinute,
                TasksSQLiteHelper.IsChecked
            ), null, null, null, null, null, null
        )
        return if (cursor.moveToFirst()) {

            cursor

        } else {
            null
        }
    }

    fun updateTask(tasksModel: TasksModel): Int {
        val contentValues = ContentValues()
        contentValues.put(TasksSQLiteHelper.TaskTitle, tasksModel.Tasktitle)
        contentValues.put(TasksSQLiteHelper.AddTimeHour, tasksModel.Addhour)
        contentValues.put(TasksSQLiteHelper.AddTimeMinute, tasksModel.Addminute)
        contentValues.put(TasksSQLiteHelper.IsChecked, tasksModel.IsChecked)
        return database.update(
            TasksSQLiteHelper.TableName,
            contentValues,
            "${TasksSQLiteHelper.id}=${tasksModel.id}",
            null,
        )
    }

    fun updateIsChecked(id: Int, IsChecked: Boolean): Int {
        val contentValues = ContentValues()
        contentValues.put(TasksSQLiteHelper.IsChecked, IsChecked)
        return database.update(
            TasksSQLiteHelper.TableName,
            contentValues,
            "${TasksSQLiteHelper.id}=${id}",
            null,
        )
    }

    fun deleteTask(id: Int) {
        database.delete(
            TasksSQLiteHelper.TableName,
            "${TasksSQLiteHelper.id}=$id",
            null
        )
    }
}