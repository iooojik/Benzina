package com.kirovcompany.bensina

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import java.util.*

class LocaleHelper {
    companion object {

        fun onAttach(context: Context): Context {
            val lang = getPersistedData(context, Locale.getDefault().language)
            return setLocale(context, lang)
        }

        fun onAttach(context: Context, defaultLang: String): Context {
            val lang = getPersistedData(context, defaultLang)
            return setLocale(context, lang)
        }

        fun setLocale(context: Context, lang: String): Context {
            persist(context, lang)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return updateResources(context, lang)
            }

            return updateResourcesLegacy(context, lang)
        }

        fun updateResourcesLegacy(context: Context, lang: String): Context {
            val locale = Locale(lang)
            Locale.setDefault(locale)

            val resources = context.resources

            val config = resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            return context
        }

        fun updateResources(context: Context, lang: String): Context {
            val locale = Locale(lang)
            Locale.setDefault(locale)

            val config = context.resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            return context.createConfigurationContext(config)
        }

        private fun persist(context: Context, lang: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(StaticVars().preferencesLanguage, lang)
            editor.apply()
        }

        fun getPersistedData(context: Context, language: String): String {
            val preferences = context.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
            return preferences.getString(StaticVars().preferencesLanguage, language).toString()
        }
    }
}