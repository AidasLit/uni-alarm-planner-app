package com.example.labworks

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import java.io.BufferedReader
import java.io.InputStreamReader

class PrivacyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.privacy_fragment, container, false)

        val textView = view.findViewById<TextView>(R.id.privacy_text)


        val inputStream = resources.openRawResource(R.raw.privacy)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val privacyText = reader.readText()
        reader.close()

        textView.text = privacyText

        return view
    }
}
