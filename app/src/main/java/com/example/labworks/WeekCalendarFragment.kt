package com.example.labworks

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
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

        // Dar tamsesnis backgroundas visam fragmentui
        view.setBackgroundColor(android.graphics.Color.parseColor("#121212"))

        weekTitle = view.findViewById(R.id.text_current_week)
        previousButton = view.findViewById(R.id.button_previous_week)
        nextButton = view.findViewById(R.id.button_next_week)
        weekContainer = view.findViewById(R.id.week_days_container)

        // Previous ir Next mygtukų stilizavimas
        styleButton(previousButton)
        styleButton(nextButton)

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

    private fun styleButton(button: Button) {
        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.setColor(android.graphics.Color.parseColor("#333333")) // Tamsus fonas
        backgroundDrawable.cornerRadius = 12f
        button.background = backgroundDrawable
        button.setTextColor(android.graphics.Color.WHITE)
    }

    private fun updateWeekView() {
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
            layoutParams.setMargins(8, 4, 8, 8) // Didesni tarpai tarp mygtukų
            btn.layoutParams = layoutParams
            btn.text = SimpleDateFormat("EEE\ndd", Locale.getDefault()).format(day.time)
            btn.textAlignment = View.TEXT_ALIGNMENT_CENTER
            btn.textSize = 16f
            btn.setPadding(0, 4, 0, 8)

            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.setColor(android.graphics.Color.parseColor("#1E1E1E")) // Mygtuko fonas
            backgroundDrawable.setStroke(3, android.graphics.Color.parseColor("#555555")) // Aiškesnis rėmelis
            backgroundDrawable.cornerRadius = 16f

            btn.background = backgroundDrawable
            btn.setTextColor(android.graphics.Color.WHITE)

            if (day.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                day.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                backgroundDrawable.setColor(android.graphics.Color.parseColor("#424242")) // Highlight šiandienai
                btn.setTypeface(null, Typeface.BOLD)
            }

            btn.setOnClickListener {
                Toast.makeText(requireContext(), "Clicked: ${fullFormat.format(day.time)}", Toast.LENGTH_SHORT).show()
            }

            weekContainer.addView(btn)
        }
    }
}
