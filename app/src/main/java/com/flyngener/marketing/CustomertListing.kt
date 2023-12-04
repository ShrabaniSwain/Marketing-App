package com.flyngener.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityAddCustomerBinding
import com.flyngener.marketing.databinding.ActivityCustomertListingBinding
import kotlinx.coroutines.*

class CustomertListing : AppCompatActivity() {

    private lateinit var binding: ActivityCustomertListingBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomertListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.ftAdd.setOnClickListener{
            val intent = Intent(this@CustomertListing, AddCustomer::class.java)
            startActivity(intent)
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getCustomerDetails(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val lotteryResponse = response.body()
                lotteryResponse?.let { handleCustomerDetailsResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }
    private fun handleCustomerDetailsResponse(result: List<CustomerDetails>) {
        val customerListingAdapter = CustomerListingAdapter(applicationContext,result)
        binding.rvCustomerList.adapter = customerListingAdapter
        binding.rvCustomerList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}