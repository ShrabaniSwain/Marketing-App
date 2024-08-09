package com.billing.marketing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.billing.marketing.databinding.ActivitySaleDeatilsBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaleDeatilsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaleDeatilsBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getSaleReport(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val lotteryResponse = response.body()
                lotteryResponse?.let { handleSaleReportResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }

    private fun handleSaleReportResponse(result: List<SaleReportItem>) {
        val stockAdapter = SaleDeatilsAdapter(applicationContext,result)
        binding.rvSaleDetails.adapter = stockAdapter
        binding.rvSaleDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }
}