package com.example.labworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class MonthCalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var monthTitle: TextView
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.month_calendar_fragment, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        monthTitle = view.findViewById(R.id.month_title)

        updateMonthTitle(calendar.timeInMillis)

        // Handle day click
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            updateMonthTitle(selectedDate.timeInMillis)

            val dateStr = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(selectedDate.time)
            Toast.makeText(requireContext(), "Selected: $dateStr", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun updateMonthTitle(timeInMillis: Long) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTitle.text = sdf.format(Date(timeInMillis))
    }
}