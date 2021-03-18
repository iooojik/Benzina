package com.kirovcompany.bensina.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.kirovcompany.bensina.MainActivity
import com.kirovcompany.bensina.R
import com.kirovcompany.bensina.StaticVars
import com.kirovcompany.bensina.localdb.AppDatabase
import com.kirovcompany.bensina.localdb.petrol.PetrolModel

class BottomSheetPetrol (
    private val context: Context,
    private val activity: Activity,
    private val fragment: RouteProcess
) : View.OnClickListener{

    private val bottomView : View = activity.layoutInflater.inflate(R.layout.bottom_sheet_petrol, null)
    val bottomSheetDialog : BottomSheetDialog = BottomSheetDialog(context)
    private val database : AppDatabase = AppDatabase.getAppDataBase(context)!!
    private val staticVars = StaticVars()

    init {
        //"подготовка" bottomSheetDialog
        initialize()
        bottomSheetDialog.setContentView(bottomView)
    }

    private fun initialize() {
        //слушатель на кнопку
        val adapter = ArrayAdapter(context, R.layout.dropdown_item, staticVars.currencyValues)
        bottomView.findViewById<AutoCompleteTextView>(R.id.currency_text_view).setAdapter(adapter)
        bottomView.findViewById<Button>(R.id.add_button).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.add_button -> {
                savePetrolInfo(getCurrency(), getPrice(), getAmount())
            }
        }
    }

    private fun savePetrolInfo(currency: String, price: Double, amount: Double) {
        if (price > 0 && amount > 0 && currency.isNotBlank()){
            database.petrolDao().insert(PetrolModel(null, currency, price, amount))
            activity.runOnUiThread { fragment.showStatistics(true) }
            Toast.makeText(context, "Добавлено", Toast.LENGTH_LONG).show()
            bottomSheetDialog.hide()
        }
    }

    private fun getAmount(): Double {
        val amountText = bottomView.findViewById<EditText>(R.id.amount_text_view).text
        var amount = 0f.toDouble()
        amount = if (amountText.isNullOrBlank()) 0.0
        else amountText.toString().toDouble()
        return amount
    }

    private fun getPrice(): Double {
        val priceText = bottomView.findViewById<EditText>(R.id.price_text_view).text
        var price = 0f.toDouble()
        price = if (priceText.isNullOrBlank()) 0.0
                else priceText.toString().toDouble()
        return price
    }

    private fun getCurrency(): String {
        return bottomView.findViewById<AutoCompleteTextView>(R.id.currency_text_view).text.toString()
    }


}