package com.kirovcompany.bensina.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.FragmentUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.car.CarModel
import com.kirovcompany.bensina.localdb.timer.TimerModel
import kotlin.concurrent.thread

class AddCarInfo : Fragment(), FragmentUtil, View.OnClickListener {

    lateinit var database : AppDatabase
    lateinit var rootView : View
    lateinit var carBrandField : AutoCompleteTextView
    lateinit var carModelField : EditText
    lateinit var carYearField : EditText
    lateinit var carOdometerField : EditText
    lateinit var carEngineAmountField : EditText
    lateinit var carRateField : EditText
    lateinit var preferences: SharedPreferences
    private val staticVars = StaticVars()


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
        rootView.findViewById<Button>(R.id.go_next).setOnClickListener(this)
        requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab).hide()
        preferences = getSharedPreferences(requireActivity())

        database = getAppDatabase(requireContext())
        database.petrolDao().deleteAll()
        database.timerDao().deleteAll()
        database.routeProgressDao().deleteAll()

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
        //TODO if (!emptyFields()){
            val carBrand = carBrandField.text.toString()
            val carModel = carModelField.text.toString()
            val carYear = carYearField.text.toString()
            val carOdometer = carOdometerField.text.toString()
            val carEngineAmount = carEngineAmountField.text.toString()
            val carRate = carRateField.text.toString()
        saveCarInfo(carBrand, carModel, carYear, carOdometer, carEngineAmount, carRate)
            requireActivity().runOnUiThread {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_beginRoute)
            }
        //} else {
        //    requireActivity().runOnUiThread {
        //        Snackbar.make(rootView, "Не все поля заполнены", Snackbar.LENGTH_LONG).show()
        //    }
       // }

    }

    private fun saveCarInfo(carBrand: String, carModel: String, carYear: String,
                                  carOdometer: String, carEngineAmount: String, carRate: String) {
        val carModelObj = CarModel(
            null,
            carBrand,
            carModel,
            carYear,
            carOdometer,
            carEngineAmount,
            carRate
        )
        database.carModelDao().deleteAll()
        database.carModelDao().insert(carModelObj)
        database.timerDao().insert(TimerModel(null, 0))
        saveBooleanToPrefs(staticVars.userAddedCar, true)
    }

    private fun saveStringToPrefs(key : String, value : String){
        saveStringToSharedPreferences(preferences, key, value)
    }

    private fun saveBooleanToPrefs(key : String, value : Boolean){
        saveBooleanToSharedPreferences(preferences, key, value)
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