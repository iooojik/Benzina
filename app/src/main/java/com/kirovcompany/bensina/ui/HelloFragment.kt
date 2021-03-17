package com.kirovcompany.bensina.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R


class HelloFragment : Fragment(), View.OnClickListener {

    private lateinit var rootView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_hello, container, false)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).hide()
        rootView.findViewById<ExtendedFloatingActionButton>(R.id.go_next).setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_next -> {
                findNavController().navigate(R.id.navigation_addCarInfo)
            }
        }
    }

}