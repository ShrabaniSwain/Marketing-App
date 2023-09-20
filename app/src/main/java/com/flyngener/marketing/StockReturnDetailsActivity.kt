package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityStockReturnDetailsBinding

class StockReturnDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockReturnDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockReturnDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val stockReturnAdapter = StockReturnAdapter()
        binding.rvStockReturnDetails.adapter = stockReturnAdapter
        binding.rvStockReturnDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}