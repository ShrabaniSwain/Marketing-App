package com.flyngener.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityStockReturnDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StockReturnDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockReturnDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockReturnDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getReturnReport(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val lotteryResponse = response.body()
                lotteryResponse?.let { handleReturnReportResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

    }

    private fun handleReturnReportResponse(result: List<ReturnItem>) {
        val stockReturnAdapter = StockReturnAdapter(applicationContext,result)
        binding.rvStockReturnDetails.adapter = stockReturnAdapter
        binding.rvStockReturnDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}