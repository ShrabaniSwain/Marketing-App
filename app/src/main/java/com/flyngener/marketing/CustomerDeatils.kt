package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityCustomerDeatilsBinding
import com.flyngener.marketing.databinding.ActivityStockDetailsBinding

class CustomerDeatils : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDeatilsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val stockAdapter = CustomerDeatilsAdapter()
        binding.rvCustomerDetails.adapter = stockAdapter
        binding.rvCustomerDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}