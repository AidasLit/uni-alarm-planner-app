package com.example.labworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.InputStreamReader

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.about_fragment, container, false)

        val aboutTextView = view.findViewById<TextView>(R.id.about_text)

        // Load text from raw file
        val inputStream = resources.openRawResource(R.raw.about)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val aboutText = reader.readText()
        reader.close()

        aboutTextView.text = aboutText

        return view
    }

}
