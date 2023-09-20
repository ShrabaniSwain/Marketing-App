package com.flyngener.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityAddCustomerBinding
import com.flyngener.marketing.databinding.ActivityCustomertListingBinding

class CustomertListing : AppCompatActivity() {

    private lateinit var binding: ActivityCustomertListingBinding

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

        val customerListingAdapter = CustomerListingAdapter()
        binding.rvCustomerList.adapter = customerListingAdapter
        binding.rvCustomerList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}