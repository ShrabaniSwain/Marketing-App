package com.billing.marketing

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(context: Context) {
    companion object {
        private const val PREF_NAME = "LoginPreference"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_CUSTOMER_ID = "customerId"
        private const val KEY_CUSTOMER_NAME = "customerName"
        private const val KEY_CUSTOMER_MOBILE_NUMBER = "customerMobileNumber"
        private const val KEY_CUSTOMER_EMIL_ID = "customerEmailId"
        private const val KEY_CUSTOMER_PROFILE_IMAGE = "customerProfileImage"
        private const val KEY_CUSTOMER_warehouse_id = "customerWarehouseId"
        private const val KEY_CUSTOMER_CODE = "customerCode"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun saveLoginData(customerId: String, customerName: String, customerMobileNumber: String, customerEmailId: String, profileImage: String, warehouse_id:String, code:String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_CUSTOMER_ID, customerId)
        editor.putString(KEY_CUSTOMER_NAME, customerName)
        editor.putString(KEY_CUSTOMER_MOBILE_NUMBER, customerMobileNumber)
        editor.putString(KEY_CUSTOMER_EMIL_ID, customerEmailId)
        editor.putString(KEY_CUSTOMER_PROFILE_IMAGE, profileImage)
        editor.putString(KEY_CUSTOMER_warehouse_id, warehouse_id)
        editor.putString(KEY_CUSTOMER_CODE, code)
        editor.apply()
    }

    fun getCustomerId(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_ID, "") ?: ""
    }

    fun getCustomerName(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_NAME, "") ?: ""
    }

    fun getCustomerMobileNumber(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_MOBILE_NUMBER, "") ?: ""
    }

    fun getCustomerEmailId(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_EMIL_ID, "") ?: ""
    }

    fun getCustomerCode(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_CODE, "") ?: ""
    }

    fun getCustomerProfleiImage(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_PROFILE_IMAGE, "") ?: ""
    }
    fun getCustomerWirehouse_id(context: Context): String {
        return getSharedPreferences(context).getString(KEY_CUSTOMER_warehouse_id, "") ?: ""
    }

    fun saveCustomerMpin(userName: String, emailId:String, password:String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_CUSTOMER_NAME, userName)
        editor.putString(KEY_CUSTOMER_EMIL_ID, emailId)
        editor.putString(KEY_CUSTOMER_CODE, password)
        editor.apply()
    }

    fun profileImage(profileImage: String){
        val editor = sharedPreferences.edit()
        editor.putString(KEY_CUSTOMER_PROFILE_IMAGE, profileImage)
        editor.apply()
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.putString(KEY_CUSTOMER_ID, "")
        editor.putString(KEY_CUSTOMER_NAME, "")
        editor.putString(KEY_CUSTOMER_MOBILE_NUMBER, "")
        editor.putString(KEY_CUSTOMER_EMIL_ID, "")
        editor.putString(KEY_CUSTOMER_CODE, "")
        editor.putString(KEY_CUSTOMER_PROFILE_IMAGE, "")
        editor.apply()
    }

}