package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityMainBinding
import com.flyngener.marketing.databinding.ActivityStockDetailsBinding

class StockDetails : AppCompatActivity() {

    private lateinit var binding: ActivityStockDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val stockAdapter = StockDetailsAdapter()
        binding.rvStockDetails.adapter = stockAdapter
        binding.rvStockDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}