package com.billing.marketing

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.billing.marketing.databinding.ActivityEditCustomerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditCustomer : AppCompatActivity() {

    private lateinit var binding: ActivityEditCustomerBinding
    private val stateSelect = listOf("Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Puducherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customerName = intent.getStringExtra("customer_name")
        val mobileNumber = intent.getStringExtra("mobile_number")
        val emailId = intent.getStringExtra("email_id")
        val gender = intent.getStringExtra("gender")
        val city = intent.getStringExtra("city")
        val customer_id = intent.getStringExtra("customer_id")
        val pincode = intent.getStringExtra("pin_code")
        val address = intent.getStringExtra("address")
        val state = intent.getStringExtra("state")

        binding.etName.text = Editable.Factory.getInstance().newEditable(customerName.toString())
        binding.etMobileNumber.text = Editable.Factory.getInstance().newEditable(mobileNumber.toString())
        binding.etEmailId.text = Editable.Factory.getInstance().newEditable(emailId.toString())
        binding.etGender.text = Editable.Factory.getInstance().newEditable(gender.toString())
        binding.etCity.text = Editable.Factory.getInstance().newEditable(city.toString())
        binding.etPinCode.text = Editable.Factory.getInstance().newEditable(pincode.toString())
        binding.etFullAddress.text = Editable.Factory.getInstance().newEditable(address.toString())
        binding.etState.text = Editable.Factory.getInstance().newEditable(state.toString())

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        val stateAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, stateSelect)
        binding.etState.setAdapter(stateAdapter)

        binding.btnSave.setOnClickListener {
            val userName = binding.etName.text.toString()
            val etMobileNumber = binding.etMobileNumber.text.toString()
            val etEmail = binding.etEmailId.text.toString()
            val etGender = binding.etGender.text.toString()
            val etCity = binding.etCity.text.toString()
            val etPinCode = binding.etPinCode.text.toString()
            val etFullAddress = binding.etFullAddress.text.toString()
            val etState = binding.etState.text.toString()
            if (userName.isEmpty() || etEmail.isEmpty() || etMobileNumber.isEmpty() || etGender.isEmpty() || etCity.isEmpty() || etPinCode.isEmpty() || etFullAddress.isEmpty() || etState.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                editCustomer(userName,etMobileNumber,etGender,etEmail,etCity,etPinCode,etFullAddress,customer_id.toString(),etState)
            }
        }
    }

    private fun editCustomer(customer_name: String, mobile_number: String, gender: String, email_id: String, city: String, pincode: String, full_address: String, customer_id: String, state: String) {
        val addCustomer = EditCustomerInfo(customer_name,mobile_number,gender,email_id,city,pincode,full_address,customer_id,state)

        val call = RetrofitClient.api.editCustomerDetails(addCustomer)
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Toast.makeText(applicationContext, "Customer details updated successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, CustomertListing::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            applicationContext.startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Failed to update customer details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to update customer details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}