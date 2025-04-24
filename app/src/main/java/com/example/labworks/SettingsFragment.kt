package com.example.labworks

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader

private val RINGTONE_REQUEST_CODE = 101
private lateinit var durationSpinner: Spinner
private lateinit var prefs: android.content.SharedPreferences
private var isSpinnerInitialized = false

class SettingsFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        val aboutTitle = view.findViewById<TextView>(R.id.text_about)

        // Load the about.txt file from res/raw
        val inputStream = resources.openRawResource(R.raw.about)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val aboutText = reader.readText()
        reader.close()

        aboutTitle.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment())
                .addToBackStack(null)
                .commit()
        }

        val privacyText = view.findViewById<TextView>(R.id.text_privacy)

        privacyText.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PrivacyFragment())
                .addToBackStack(null)
                .commit()
        }

        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_time_format)
        val radio12 = view.findViewById<RadioButton>(R.id.radio_12)
        val radio24 = view.findViewById<RadioButton>(R.id.radio_24)

        val prefs = requireContext().getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
        val is24h = prefs.getBoolean("use24Hour", true) // default: true (24h)

        if (is24h) radio24.isChecked = true else radio12.isChecked = true

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selected24 = (checkedId == R.id.radio_24)
            prefs.edit().putBoolean("use24Hour", selected24).apply()
            Toast.makeText(requireContext(), "Time format updated", Toast.LENGTH_SHORT).show()
        }

        val soundButton = view.findViewById<Button>(R.id.button_pick_sound)
        val soundLabel = view.findViewById<TextView>(R.id.selected_sound_name)

        val soundUriString = prefs.getString("alarmSound", null)
        val soundUri = soundUriString?.let { Uri.parse(it) }

        // Show sound name if already saved
        soundUri?.let {
            val ringtone = RingtoneManager.getRingtone(requireContext(), it)
            soundLabel.text = ringtone.getTitle(requireContext())
        }

        soundButton.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, soundUri)
            startActivityForResult(intent, RINGTONE_REQUEST_CODE)
        }

        // Setup duration spinner
        durationSpinner = view.findViewById(R.id.spinner_ring_duration)

        val durations = listOf("5 seconds", "10 seconds", "15 seconds", "30 seconds", "1 minute")
        val durationValues = listOf(5, 10, 15, 30, 60) // seconds

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        durationSpinner.adapter = adapter

        // Load saved duration
        val savedDuration = prefs.getInt("alarmDuration", 10) // default to 10 seconds
        val indexToSelect = durationValues.indexOf(savedDuration)
        if (indexToSelect != -1) {
            durationSpinner.setSelection(indexToSelect)
        }

        // Save selection when changed

        isSpinnerInitialized = false

        durationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedDuration = durationValues[position]
                    prefs.edit().putInt("alarmDuration", selectedDuration).apply()
                    Toast.makeText(requireContext(), "Ring duration set to $selectedDuration seconds", Toast.LENGTH_SHORT).show()
                } else {
                    isSpinnerInitialized = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RINGTONE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

            if (uri != null) {
                val prefs = requireContext().getSharedPreferences(
                    "AppPrefs",
                    android.content.Context.MODE_PRIVATE
                )
                prefs.edit().putString("alarmSound", uri.toString()).apply()

                val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
                view?.findViewById<TextView>(R.id.selected_sound_name)?.text =
                    ringtone.getTitle(requireContext())
            }
        }
    }
}