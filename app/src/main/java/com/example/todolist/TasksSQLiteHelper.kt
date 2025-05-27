package com.example.todolist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TasksSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "my.db", null, 3) {

    companion object {

        const val TableName = "tasks"
        const val id = "_id"
        const val TaskTitle = "TaskTitle"
        const val AddTimeHour = "AddTimeHour"
        const val AddTimeMinute = "AddTimeMinute"
        const val IsChecked = "isChecked"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE $TableName($id INTEGER PRIMARY KEY AUTOINCREMENT, $TaskTitle VARCHAR, $AddTimeHour INTEGER, $AddTimeMinute INTEGER, $IsChecked BOOLEAN);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("DROP TABLE IF EXISTS $TableName")
        onCreate(db)
    }
}