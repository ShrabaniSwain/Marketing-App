package com.flyngener.marketing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityCustomertListingBinding
import com.flyngener.marketing.databinding.ActivityEnquiryListingBinding

class EnquiryListing : AppCompatActivity() {

    private lateinit var binding: ActivityEnquiryListingBinding

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

        val enquireListingAdapter = EnquireListingAdapter()
        binding.rvCustomerList.adapter = enquireListingAdapter
        binding.rvCustomerList.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}