package com.kirovcompany.bensina.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.interfaces.FragmentUtil


class BeginRoute : Fragment(), View.OnClickListener, FragmentUtil {

    lateinit var rootView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_begin_route, container, false)
        checkPermissions()
        initViews()
        return rootView
    }

    override fun initViews() {
        rootView.findViewById<Button>(R.id.begin_route_button).setOnClickListener(this)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).show()
        setFabAction(requireActivity().findViewById(R.id.fab), requireContext(), requireActivity())
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.begin_route_button -> {
                checkPermissionsAndStartRoute()
            }
        }
    }

    private fun checkPermissionsAndStartRoute() {
        if (checkPermissions())
            findNavController().navigate(R.id.navigation_routeProcess)
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).toTypedArray(),
                101
            )
        } else return true
        return false
    }

}