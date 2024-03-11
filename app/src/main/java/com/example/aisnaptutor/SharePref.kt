package com.example.aisnaptutor

import android.content.Context
import android.content.SharedPreferences

class SharePref {
    companion object {
        private lateinit var pref: SharedPreferences

        private fun initPref() {
            pref = ApplicationClass.getContext()
                .getSharedPreferences("AISnapTutor", Context.MODE_PRIVATE)
        }

        fun putString(key: String, value: String) {
            if (!Companion::pref.isInitialized)
                initPref()
            pref.edit().putString(key, value).apply()
        }

        fun putBoolean(key: String, value: Boolean) {
            if (!Companion::pref.isInitialized)
                initPref()
            pref.edit().putBoolean(key, value).apply()
        }
        fun remove(key: String){
            if (!Companion::pref.isInitialized)
                initPref()
            pref.edit().remove(key).apply()
        }

        fun getString(key: String , default: String): String {
            if (!Companion::pref.isInitialized)
                initPref()
            return pref.getString(key, default)!!
        }

        fun getBoolean(key: String , default: Boolean = false): Boolean {
            if (!Companion::pref.isInitialized)
                initPref()
            return pref.getBoolean(key, default)
        }

        fun getInt(key: String) : Int{
            if (!Companion::pref.isInitialized)
                initPref()
            return pref.getInt(key, 1)
        }

        fun putInt(key: String , value:Int){
            if (!Companion::pref.isInitialized)
                initPref()
            pref.edit().putInt(key, value).apply()
        }

        fun putLong(key: String , value: Long){
            if (!Companion::pref.isInitialized)
                initPref()
            pref.edit().putLong(key, value).apply()
        }
        fun getLong(key: String):Long{
            if (!Companion::pref.isInitialized)
                initPref()
            return pref.getLong(key, 1)
        }
    }
}