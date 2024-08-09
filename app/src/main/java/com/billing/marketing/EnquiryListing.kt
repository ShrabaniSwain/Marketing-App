package com.billing.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.billing.marketing.databinding.ActivityEnquiryListingBinding
import kotlinx.coroutines.*

class EnquiryListing : AppCompatActivity() {

    private lateinit var binding: ActivityEnquiryListingBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnquiryListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.ftAdd.setOnClickListener{
            val intent = Intent(this@EnquiryListing, AddEnquire::class.java)
            startActivity(intent)
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getEnquiryDetails(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val lotteryResponse = response.body()
                lotteryResponse?.let { handleEnquiryDetailsResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

    }

    private fun handleEnquiryDetailsResponse(result: List<EnquiryDetails>) {
        val enquireListingAdapter = EnquireListingAdapter(applicationContext,result)
        binding.rvCustomerList.adapter = enquireListingAdapter
        binding.rvCustomerList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}