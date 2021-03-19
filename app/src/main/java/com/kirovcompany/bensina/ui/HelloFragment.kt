package com.kirovcompany.bensina.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import java.util.*


class HelloFragment : Fragment(), View.OnClickListener {

    private lateinit var rootView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_hello, container, false)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).hide()
        rootView.findViewById<ExtendedFloatingActionButton>(R.id.go_next).setOnClickListener(this)

        rootView.findViewById<Button>(R.id.russian_locale).setOnClickListener(this)
        rootView.findViewById<Button>(R.id.english_locale).setOnClickListener(this)

        return rootView
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_next -> {
                findNavController().navigate(R.id.navigation_addCarInfo)
            }
            R.id.russian_locale -> {
                requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE).edit().
                        putString(StaticVars().preferencesLanguage, "ru").apply()
                restartActivity()

            }
            R.id.english_locale -> {
                requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE).edit().
                putString(StaticVars().preferencesLanguage, "en").apply()
                restartActivity()
            }
        }
    }

    private fun restartActivity(){
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
    }

}