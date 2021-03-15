package com.kirovcompany.bensina.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.FragmentInit
import com.kirovcompany.bensina.localdb.AppDatabase
import kotlin.concurrent.thread

class AddCarInfo : Fragment(), FragmentInit, View.OnClickListener {

    lateinit var database : AppDatabase
    lateinit var rootView : View
    lateinit var carBrandField : AutoCompleteTextView
    lateinit var carModelField : EditText
    lateinit var carYearField : EditText
    lateinit var carOdometerField : EditText
    lateinit var carEngineAmountField : EditText
    lateinit var carRateField : EditText
    lateinit var goNext : Button
    lateinit var preferences: SharedPreferences
    val staticVars = StaticVars()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_add_car_info, container, false)
        initViews()
        return rootView
    }

    override fun initViews() {
        carBrandField = rootView.findViewById(R.id.car_brand_field)
        carModelField = rootView.findViewById(R.id.car_model_field)
        carYearField = rootView.findViewById(R.id.car_year_field)
        carOdometerField = rootView.findViewById(R.id.car_odometer_field)
        carEngineAmountField = rootView.findViewById(R.id.car_engine_amount_field)
        carRateField = rootView.findViewById(R.id.car_rate_field)
        goNext = rootView.findViewById(R.id.go_next)
        goNext.setOnClickListener(this)

        preferences = getSharedPreferences(requireActivity())

        database = getAppDatabase(requireContext())
        setCarModels()
    }

    private fun setCarModels() {
        //выбор модели авто из базы данных
        val tempModels = database.carBrandDao().getAll()
        val models = mutableListOf<String>()
        for (model in tempModels)
            models.add(model.carBrand.toString())
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, models)
        carBrandField.setAdapter(adapter)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_next -> {
                thread {
                    //сохранение информации об авто
                    saveCarInfo()
                }
            }
        }
    }

    private fun saveCarInfo() {
        //если нет пустых полей, то сохраняем информацию и переходим на следующее окно
        //иначе выводим уведомление
        if (!emptyFields()){
            val carBrand = carBrandField.text.toString()
            val carModel = carModelField.text.toString()
            val carYear = carYearField.text.toString()
            val carOdometer = carOdometerField.text.toString()
            val carEngineAmount = carEngineAmountField.text.toString()
            val carRate = carRateField.text.toString()
            saveCarInfoToPrefs(carBrand, carModel, carYear, carOdometer, carEngineAmount, carRate)
            requireActivity().runOnUiThread {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_beginRoute)
            }
        } else {
            requireActivity().runOnUiThread {
                Snackbar.make(rootView, "Не все поля заполнены", Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun saveCarInfoToPrefs(carBrand: String, carModel: String, carYear: String,
                                  carOdometer: String, carEngineAmount: String, carRate: String) {
        saveStringToPrefs(staticVars.carBrand, carBrand)
        saveStringToPrefs(staticVars.carModel, carModel)
        saveStringToPrefs(staticVars.carYear, carYear)
        saveStringToPrefs(staticVars.carOdometer, carOdometer)
        saveStringToPrefs(staticVars.carEngineAmount, carEngineAmount)
        saveStringToPrefs(staticVars.carRate, carRate)
    }

    private fun saveStringToPrefs(key : String, value : String){
        saveStringToSharedPreferences(preferences, key, value)
    }

    private fun emptyFields() : Boolean{
        //проверка на пустые поля
        return carBrandField.text.isNullOrEmpty() ||
               carModelField.text.isNullOrEmpty() ||
               carYearField.text.isNullOrEmpty() ||
               carOdometerField.text.isNullOrEmpty() ||
               carEngineAmountField.text.isNullOrEmpty() ||
               carRateField.text.isNullOrEmpty()
    }
}