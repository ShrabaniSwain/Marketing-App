package com.billing.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.billing.marketing.databinding.ActivityUpdateEnquireBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateEnquire : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateEnquireBinding
    private val productNames = mutableListOf<String>()
    private val productPhNo = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateEnquireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val customerName = intent.getStringExtra("customer_name")
        val mobileNumber = intent.getStringExtra("mobile_number")
        val location = intent.getStringExtra("location")
        val details = intent.getStringExtra("details")
        val enquiry_id = intent.getStringExtra("enquiry_id")

        binding.etName.text = Editable.Factory.getInstance().newEditable(customerName.toString())
        binding.etMobileNumber.text = Editable.Factory.getInstance().newEditable(mobileNumber.toString())
        binding.etEmailId.text = Editable.Factory.getInstance().newEditable(location.toString())
        binding.etGender.text = Editable.Factory.getInstance().newEditable(details.toString())

        binding.btnSIgnUp.setOnClickListener {
            val userName = binding.etName.text.toString()
            val etMobileNumber = binding.etMobileNumber.text.toString()
            val etEmail = binding.etEmailId.text.toString()
            val etGender = binding.etGender.text.toString()
            if (userName.isEmpty() || etEmail.isEmpty() || etMobileNumber.isEmpty() || etGender.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                editEnquiry(userName,etMobileNumber,etEmail,etGender, enquiry_id.toString())
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getCustomerDetails(Constant.customer_wirehouse_id, Constant.customer_id)
            }

            if (response.isSuccessful) {
                val customerDetailsResponse = response.body()
                customerDetailsResponse?.let { handleCustomerResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
        val customAutoCompleteAdapter = CustomCustomerName(this, productNames)
        binding.etName.setAdapter(customAutoCompleteAdapter)

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString().toLowerCase()
                val filteredNames = productNames.filter { it.toLowerCase().contains(userInput) }.toMutableList()
                Log.i("TAG", "onTextChanged: "+filteredNames)
                filteredNames.add(0, "Add Customer")
                val customAutoCompleteAdapter = CustomCustomerName(this@UpdateEnquire, filteredNames)
                binding.etName.setAdapter(customAutoCompleteAdapter)
                binding.etName.setOnItemClickListener { _, _, position, _ ->
                    val selectedCustomerName = filteredNames[position]
                    if (selectedCustomerName != "Add Customer") {
                        val mobileNumber = productPhNo[selectedCustomerName]
                        binding.etName.setText(selectedCustomerName)
                        binding.etMobileNumber.setText(mobileNumber)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun editEnquiry(customer_name: String, mobile_number: String, location: String, details: String, enquiry_id: String) {
        val editEnquiry = EditEnquiryDetails(customer_name, mobile_number, location, details, enquiry_id)

        val call = RetrofitClient.api.editEnquiryDetails(editEnquiry)
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            Toast.makeText(applicationContext, "Enquiry details updated successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, EnquiryListing::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            applicationContext.startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Failed to update enquiry details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to update enquiry details. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleCustomerResponse(result: List<CustomerDetails>) {
        productNames.clear()
        productNames.add("Add Customer")
        productPhNo.clear()
        productPhNo.putAll(result.associate { it.customer_name to it.mobile_number })

        productNames.addAll(result.map { it.customer_name })
        Log.i("TAG", "Product names: $productNames")

        val nameAdapter = binding.etName.adapter as CustomCustomerName
        nameAdapter.notifyDataSetChanged()
    }
}