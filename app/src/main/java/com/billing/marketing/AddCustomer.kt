package com.billing.marketing

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.billing.marketing.databinding.ActivityAddCustomerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCustomer : AppCompatActivity() {

    private lateinit var binding: ActivityAddCustomerBinding
    private val stateSelect = listOf("Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Puducherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            val userName = binding.etName.text.toString()
            val etMobileNumber = binding.etMobileNumber.text.toString()
            val etEmail = binding.etEmailId.text.toString()
            val etGender = binding.etGender.text.toString()
            val etCity = binding.etCity.text.toString()
            val etPinCode = binding.etPinCode.text.toString()
            val etFullAddress = binding.etFullAddress.text.toString()
            val state = binding.etState.text.toString()
            if (userName.isEmpty() || etMobileNumber.isEmpty() || etCity.isEmpty() ) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                addCustomer("",userName,etMobileNumber,etGender,etEmail,etCity,"",etPinCode,etFullAddress,Constant.customer_wirehouse_id,Constant.customer_id,"","","", binding.etState.text.toString())
            }
        }

        val stateAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, stateSelect)

        binding.etState.setAdapter(stateAdapter)

    }

    private fun addCustomer(customer_id:String, customer_name: String, mobile_number: String, gender: String, email_id: String, city: String, customer_code: String, pincode: String, full_address: String, warehouse_id: String,
                            sales_executive_id: String, join_date: String, due_balance: String, add_date: String, state: String) {
        val addCustomer = CustomerDetails(customer_id,customer_name,mobile_number,gender, email_id, city, customer_code, pincode, full_address, warehouse_id, sales_executive_id, join_date, due_balance, add_date, state)
        Log.i("TAG", "addCustomer: "+ addCustomer)
        val call = RetrofitClient.api.addcustomer(addCustomer)
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            binding.etName.text = Editable.Factory.getInstance().newEditable(customer_name)
                            binding.etMobileNumber.text = Editable.Factory.getInstance().newEditable(mobile_number)
                            binding.etEmailId.text = Editable.Factory.getInstance().newEditable(email_id)
//                            Constant.customer_name = etUsername
//                            Constant.customer_EmialId = emailId
//                            Constant.customer_Code = password

                            Toast.makeText(applicationContext, "Customer details added successfully", Toast.LENGTH_SHORT).show()
                            val intent =
                                Intent(applicationContext, CustomertListing::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Failed to save customer details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to save customer details details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}