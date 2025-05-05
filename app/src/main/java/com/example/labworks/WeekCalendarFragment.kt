package com.example.labworks

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.labworks.R
import java.lang.reflect.Array.set
import java.text.SimpleDateFormat
import java.util.*

class WeekCalendarFragment : Fragment() {

    private lateinit var weekTitle: TextView
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var weekContainer: LinearLayout

    private var currentCalendar: Calendar = Calendar.getInstance()

    private lateinit var rootView: View  // 👈 add this property to the class

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.week_calendar_fragment, container, false)

        rootView.setBackgroundColor(android.graphics.Color.parseColor("#121212"))

        weekTitle = rootView.findViewById(R.id.text_current_week)
        previousButton = rootView.findViewById(R.id.button_previous_week)
        nextButton = rootView.findViewById(R.id.button_next_week)
        weekContainer = rootView.findViewById(R.id.week_grid_container)

        styleButton(previousButton)
        styleButton(nextButton)

        updateWeekGrid(rootView) // ✅ now passes the root view

        previousButton.setOnClickListener {
            animateButtonBounce(previousButton)
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -1)
            animateWeekChange(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        nextButton.setOnClickListener {
            animateButtonBounce(nextButton)
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            animateWeekChange(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        return rootView
    }


    private fun styleButton(button: Button) {
        val backgroundDrawable = GradientDrawable()
        backgroundDrawable.setColor(android.graphics.Color.parseColor("#333333")) // Tamsus fonas
        backgroundDrawable.cornerRadius = 12f
        button.background = backgroundDrawable
        button.setTextColor(android.graphics.Color.WHITE)
    }

    private fun updateWeekGrid(rootView: View) {
        val gridContainer = rootView.findViewById<LinearLayout>(R.id.week_grid_container)
        gridContainer.removeAllViews()

        val context = requireContext()
        val today = Calendar.getInstance()
        val weekStart = currentCalendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // LEFT: Hours column
        val hourColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(8, 0, 8, 0)
            }
        }

        // Add empty space on top for alignment with date headers
        hourColumn.addView(TextView(context).apply {
            text = ""
            height = 100
        })

        for (hour in 0..23) {
            hourColumn.addView(TextView(context).apply {
                text = "$hour:00"
                textSize = 14f
                setTextColor(android.graphics.Color.GRAY)
                setPadding(8, 8, 8, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    100
                )
            })
        }
        gridContainer.addView(hourColumn)

        for (i in 0 until 7) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_WEEK, i)

            val column = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    100,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(8, 0, 12, 0)
                }
            }

            // Header
            val header = TextView(requireContext()).apply {
                text = SimpleDateFormat("EEE\ndd", Locale.getDefault()).format(day.time)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                setTextColor(android.graphics.Color.WHITE)
                setTypeface(null, Typeface.BOLD)
            }
            column.addView(header)

            // Time slots
            for (hour in 0..23) {
                val slot = TextView(requireContext()).apply {
                    text = ""
                    setBackgroundColor(android.graphics.Color.parseColor("#1E1E1E"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        100
                    )
                    setOnClickListener {
                        val time = (day.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                        }.time
                        val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(time)
                        Toast.makeText(context, "Clicked: $formatted", Toast.LENGTH_SHORT).show()
                    }

                }
                column.addView(slot)
            }

            gridContainer.addView(column)
        }




    }

    private fun animateWeekChange(enterAnim: Int, exitAnim: Int) {
        val exitAnimation = android.view.animation.AnimationUtils.loadAnimation(requireContext(), exitAnim)
        val enterAnimation = android.view.animation.AnimationUtils.loadAnimation(requireContext(), enterAnim)

        exitAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                updateWeekGrid(rootView) // ✅ use stored rootView
                weekContainer.startAnimation(enterAnimation)
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        weekContainer.startAnimation(exitAnimation)
    }


    private fun animateButtonBounce(button: View) {
        button.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }


}
