package com.kirovcompany.bensina.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import java.util.*


class HelloFragment : Fragment(), View.OnClickListener {

    private lateinit var rootView : View
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)

        rootView = inflater.inflate(R.layout.fragment_hello, container, false)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).hide()
        rootView.findViewById<ExtendedFloatingActionButton>(R.id.go_next).setOnClickListener(this)
        rootView.findViewById<ExtendedFloatingActionButton>(R.id.select_lang).setOnClickListener(this)

        if (!preferences.getBoolean(StaticVars().preferencesLanguageSelected, false))
            selectLang()
        return rootView
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_next -> {
                findNavController().navigate(R.id.navigation_addCarInfo)
            }
            R.id.select_lang -> {
                selectLang()
            }
        }
    }

    private fun selectLang(){
        val items = arrayOf("\uD83C\uDDF7\uD83C\uDDFA Русский", "\uD83C\uDDEC\uD83C\uDDE7 English")

        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Выберите язык | Select language")
                .setItems(items) { _, which ->
                    when(which){
                        0 -> {
                            setRussianLanguage()
                        }
                        1 -> {
                            setGBLanguage()
                        }
                    }
                    preferences.edit().putBoolean(StaticVars().preferencesLanguageSelected, true).apply()
                }
                .setCancelable(false)
                .show()
    }

    private fun setGBLanguage(){
        preferences.edit().putString(StaticVars().preferencesLanguage, "en").apply()
        requireActivity().recreate()
    }

    private fun setRussianLanguage(){
        preferences.edit().putString(StaticVars().preferencesLanguage, "ru").apply()
        requireActivity().recreate()
    }

}