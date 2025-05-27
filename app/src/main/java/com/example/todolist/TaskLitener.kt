package com.example.todolist

interface TaskLitener {
    fun onDeleteTask(position: Int)
    fun isChecked(id: Int)
    fun unChecked(id: Int)
    fun updateTask(tasksModel: TasksModel)


}