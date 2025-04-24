package com.example.labworks

import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.labworks.R
import java.text.SimpleDateFormat
import java.util.*

class WeekCalendarFragment : Fragment() {

    private lateinit var weekTitle: TextView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var weekContainer: LinearLayout

    private var currentCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.week_calendar_fragment, container, false)

        weekTitle = view.findViewById(R.id.text_current_week)
        previousButton = view.findViewById(R.id.button_previous_week)
        nextButton = view.findViewById(R.id.button_next_week)
        weekContainer = view.findViewById(R.id.week_days_container)

        updateWeekView()

        previousButton.setOnClickListener {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -1)
            updateWeekView()
        }

        nextButton.setOnClickListener {
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            updateWeekView()
        }

        return view
    }

    private fun updateWeekView() {
        // Start from Monday
        val weekStart = currentCalendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val weekEnd = weekStart.clone() as Calendar
        weekEnd.add(Calendar.DAY_OF_WEEK, 6)

        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val fullFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val startStr = dateFormat.format(weekStart.time)
        val endStr = dateFormat.format(weekEnd.time)
        val yearStr = SimpleDateFormat("yyyy", Locale.getDefault()).format(weekStart.time)
        weekTitle.text = "Week of $startStr – $endStr, $yearStr"

        weekContainer.removeAllViews()

        val today = Calendar.getInstance()

        for (i in 0 until 7) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_WEEK, i)

            val btn = Button(requireContext())
            val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            layoutParams.setMargins(0, 4, 0, 8) // optional margin between buttons
            btn.layoutParams = layoutParams
            btn.text = SimpleDateFormat("EEE\ndd", Locale.getDefault()).format(day.time)
            btn.textAlignment = View.TEXT_ALIGNMENT_CENTER
            btn.textSize = 16f
            btn.setPadding(0, 4, 0, 8) // make text more centered vertically


            // Highlight today
            if (day.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                day.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                btn.setTypeface(null, Typeface.BOLD)
                btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            }

            btn.setOnClickListener {
                Toast.makeText(requireContext(), "Clicked: ${fullFormat.format(day.time)}", Toast.LENGTH_SHORT).show()
                // You can use this to open daily events, etc.
            }

            weekContainer.addView(btn)
        }
    }
}
