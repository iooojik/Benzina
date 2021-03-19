package com.kirovcompany.bensina

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(base!!, "ru"))
    }

}