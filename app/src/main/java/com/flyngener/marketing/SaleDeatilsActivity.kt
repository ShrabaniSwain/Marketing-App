package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivitySaleDeatilsBinding
import com.flyngener.marketing.databinding.ActivityStockDetailsBinding

class SaleDeatilsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaleDeatilsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val stockAdapter = SaleDeatilsAdapter()
        binding.rvSaleDetails.adapter = stockAdapter
        binding.rvSaleDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}