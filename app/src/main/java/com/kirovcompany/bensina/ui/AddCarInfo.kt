package com.kirovcompany.bensina.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.interfaces.FragmentInit

class AddCarInfo : Fragment(), FragmentInit {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initViews()
        return inflater.inflate(R.layout.fragment_add_car_info, container, false)
    }

    override fun initViews() {

    }


}