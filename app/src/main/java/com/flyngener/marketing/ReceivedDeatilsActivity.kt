package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityReceivedDeatilsBinding

class ReceivedDeatilsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceivedDeatilsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceivedDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val receivedAdapter = ReceivedAdapter()
        binding.rvStockReturnDetails.adapter = receivedAdapter
        binding.rvStockReturnDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}