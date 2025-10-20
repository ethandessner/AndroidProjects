package com.example.chatappgroupproject.utilities

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {
    private lateinit var sharedPreferences : SharedPreferences

    constructor(context : Context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun putBoolean(key : String, value : Boolean) {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBoolean(key : String) : Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key : String, value : String) {
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun getString(key : String) : String? {
        return sharedPreferences.getString(key, null)
    }
    fun clear() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}