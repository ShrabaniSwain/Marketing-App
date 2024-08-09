package com.billing.marketing

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.billing.marketing.databinding.ActivityAddEnquireBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEnquire : AppCompatActivity() {

    private lateinit var binding: ActivityAddEnquireBinding
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val productNames = mutableListOf<String>()
    private val productPhNo = mutableMapOf<String, String>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEnquireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.etEmailId.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            requestLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
                addEnquiry("", userName,etMobileNumber,etEmail,etGender,Constant.customer_wirehouse_id,Constant.customer_id)
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
                val customAutoCompleteAdapter = CustomCustomerName(this@AddEnquire, filteredNames)
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

    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                val locationText = getLocationName(location.latitude, location.longitude)
                Log.d("LocationUpdate", "Location name: $locationText")

                // Update the UI on the main thread
                runOnUiThread {
                    binding.etEmailId.text = Editable.Factory.getInstance().newEditable(locationText)
                    binding.progressBar.visibility = View.GONE
                }

                // Stop location updates
                fusedLocationClient?.removeLocationUpdates(this)
            }
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                return address.getAddressLine(0)
            }
        } catch (e: IOException) {
            Log.e("LocationError", "Error fetching location", e)
        }
        return "Location not found"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            }
        }
    }

    private fun addEnquiry(enquiry_id:String, customer_name: String, mobile_number: String, location: String, details: String, warehouse_id: String, sales_executive_id: String) {
        val addEnquire = EnquiryDetails(enquiry_id, customer_name,mobile_number, location, details, warehouse_id, sales_executive_id)

        val call = RetrofitClient.api.addEnquiry(addEnquire)
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    updateProfileResponse?.let {
                        if (it.isSuccess) {
                            binding.etName.text = Editable.Factory.getInstance().newEditable(customer_name)
                            binding.etMobileNumber.text = Editable.Factory.getInstance().newEditable(mobile_number)
                            binding.etEmailId.text = Editable.Factory.getInstance().newEditable(location)
                            binding.etGender.text = Editable.Factory.getInstance().newEditable(details)
//                            Constant.customer_name = etUsername
//                            Constant.customer_EmialId = emailId
//                            Constant.customer_Code = password

                            Toast.makeText(applicationContext, "Enquiry details added successfully", Toast.LENGTH_SHORT).show()
                            val intent =
                                Intent(applicationContext, EnquiryListing::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Failed to save enquiry details. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to save enquiry details details. Please try again.", Toast.LENGTH_SHORT).show()
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