package com.example.todolist

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val daysInMonth: Int,
    private val startingDay: Int
) : RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val dayOfMonth = position - startingDay + 3
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_MONTH)
        cal.add(Calendar.DAY_OF_MONTH, position - startingDay)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1

        holder.dayNumber.text = dayOfMonth.toString()
        holder.dayName.text = getDayOfWeekName(dayOfWeek)
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        if (dayOfMonth == today) {
            holder.dayNumber.setBackgroundResource(R.drawable.item_calendar_today_background)
            holder.dayNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

            holder.dayNumber.layoutParams.width = 80
            holder.dayNumber.layoutParams.height = 80
            holder.dayNumber.setPadding(0, 10, 0, 0)
            holder.dayName.setTextColor(Color.parseColor("#DFBD43"))
        }
    }

    override fun getItemCount(): Int {
        return daysInMonth
    }

    private fun getDayOfWeekName(dayOfWeek: Int): String {
        return SimpleDateFormat("EEE", Locale.getDefault()).format(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
        }.time)
    }
}

class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayNumber: TextView = itemView.findViewById(R.id.dayNumber)
    val dayName: TextView = itemView.findViewById(R.id.dayName)
}
