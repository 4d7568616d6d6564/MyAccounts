package com.mkdevelopment.myaccounts.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(
    context: Context
) {
    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("LocalPasswordManager_Data", Context.MODE_PRIVATE)
    }

    fun clearAllData() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun setName(value: String) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putString("name", value)
        prefsEditor.apply()
    }

    fun getName(): String {
        return sharedPreferences.getString("launcher", "").toString()
    }

    fun isAccountCreated(): Boolean {
        return sharedPreferences.getBoolean("account_created", false)
    }

    fun setAccountCreated(value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putBoolean("account_created", value)
        prefsEditor.apply()
    }

    fun getProtectionRemainingTime(): Long {
        return sharedPreferences.getLong("remaining_time", 0)
    }

    fun setProtectionRemainingTime(value: Long) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putLong("remaining_time", value)
        prefsEditor.apply()
    }
    fun setAccountPassword(value: String) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putString("account_password", value)
        prefsEditor.apply()
    }

    fun getAccountPassword(): String {
        return sharedPreferences.getString("account_password", "").toString()
    }

    fun setRetryCount(value: Int) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putInt("retry_count", value)
        prefsEditor.apply()
    }

    fun getRetryCount(): Int {
        return sharedPreferences.getInt("retry_count", 0)
    }

    fun setDateFormatPattern(value: String) {
        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putString("date_format_pattern", value)
        prefsEditor.apply()
    }

    fun getDateFormatPattern(): String {
        return sharedPreferences.getString("date_format_pattern", "").toString()
    }

}