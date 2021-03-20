package com.kirovcompany.bensina.ui

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kirovcompany.bensina.LocaleHelper
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.interfaces.FragmentUtil
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.car.CarModel
import com.kirovcompany.bensina.localdb.service.ServiceModel
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

        val args = arguments
        if (args != null){
            val state = args.getBoolean("update")
            if (state){
                changeViewsValues()
                rootView.findViewById<Button>(R.id.change_lang).visibility = View.VISIBLE
                rootView.findViewById<Button>(R.id.change_lang).setOnClickListener(this)
            }
        }

        setCarModels()
    }

    private fun changeViewsValues() {
        val car = database.carModelDao().getLast()
        carBrandField.setText(car.carBrand)
        carModelField.setText(car.carModel)
        carYearField.setText(car.carYear)
        carRateField.setText(car.carRate)
        carOdometerField.setText(car.carOdometer)
        carEngineAmountField.setText(car.carEngineAmount)
    }

    private fun setCarModels() {
        //выбор модели авто из базы данных
        val tempModels = database.carBrandDao().getAll()
        val models = mutableListOf<String>()
        for (model in tempModels)
            models.add(model.carBrand.toString())
        models.sort()
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, models)
        carBrandField.setAdapter(adapter)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.go_next -> {
                //сохранение информации об авто
                saveCarInfo()
            }
            R.id.change_lang -> {
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
        LocaleHelper.onAttach(requireActivity().applicationContext, "en")
        preferences.edit().putInt(StaticVars().firstStartUP, 1).apply()
        requireActivity().recreate()
    }

    private fun setRussianLanguage(){
        preferences.edit().putString(StaticVars().preferencesLanguage, "ru").apply()
        LocaleHelper.onAttach(requireActivity().applicationContext, "ru")
        preferences.edit().putInt(StaticVars().firstStartUP, 1).apply()
        requireActivity().recreate()
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

            val args = Bundle()

            val car = database.carModelDao().getLast()

            if (args != null){
                if (args.getBoolean("update") && carModelField.text.toString() != car.carModel){
                    database.petrolDao().deleteAll()
                    database.routesPerDayModel().deleteAll()
                    database.routeProgressDao().deleteAll()
                }
            }

            saveCarInfo(carBrand, carModel, carYear, carOdometer, carEngineAmount, carRate)

            checkPermissions()
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_routeProcess)

        } else {
            requireActivity().runOnUiThread {
                Snackbar.make(rootView, requireActivity().resources.getString(R.string.not_all_fields), Snackbar.LENGTH_LONG).show()
            }
        }

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
        database.timerDao().deleteAll()
        database.timerDao().insert(TimerModel(null, 0))
        database.serviceDao().deleteAll()
        database.serviceDao().insert(ServiceModel(null, false))
        saveBooleanToPrefs(staticVars.userAddedCar, true)
    }

    private fun saveStringToPrefs(key : String, value : String){
        saveStringToSharedPreferences(preferences, key, value)
    }

    private fun saveBooleanToPrefs(key : String, value : Boolean){
        saveBooleanToSharedPreferences(preferences, key, value)
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