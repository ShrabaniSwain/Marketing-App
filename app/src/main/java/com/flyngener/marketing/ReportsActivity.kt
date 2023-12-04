package com.flyngener.marketing

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityCustomerDeatilsBinding
import com.flyngener.marketing.databinding.ActivityReportsBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private var isSalesCardVisible = false
    private var isStockCardVisible = false
    private var isReturnCardVisible = false
    private var isReceiveCardVisible = false
    private val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    private var saleReportItems: List<SaleReportItem> = emptyList()
    private var stockReportItems: List<StockReportItem> = emptyList()
    private var returnReportItems: List<ReturnItem> = emptyList()
    private var receivedReportItems: List<ReceivedReportItem> = emptyList()
    private var dueReportItems: List<DueItem> = emptyList()
    private var selectedStartDate: String = currentDate
    private var selectedEndDate: String = currentDate


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.tvSaleTodayDate.text = currentDate
        binding.tvSaleSelectDateRange.text = currentDate

        binding.tvstockTodayDate.text = currentDate
        binding.tvstockSelectDateRange.text = currentDate

        binding.tvreturnTodayDate.text = currentDate
        binding.tvreturnSelectDateRange.text = currentDate

        binding.tvreceiveTodayDate.text = currentDate
        binding.tvreceiveSelectDateRange.text = currentDate

        binding.tvdueTodayDate.text = currentDate
        binding.tvdueSelectDateRange.text = currentDate

        filterAndDisplayStockItems(currentDate,currentDate)
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getSaleReport(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                val lotteryResponse = response.body()
                lotteryResponse?.let {
                    saleReportItems = it.result
                    handleSaleReportResponse(saleReportItems)
                    filterAndDisplayItems(currentDate,currentDate)
                }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getReturnReport(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                val lotteryResponse = response.body()
                lotteryResponse?.let {
                    returnReportItems = it.result
                    handleReturnReportResponse(returnReportItems)
                    filterAndDisplayReturnItems(currentDate,currentDate)
                }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getReceivedReport(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                val lotteryResponse = response.body()
                lotteryResponse?.let {
                    receivedReportItems = it.result
                    handleReceivedReportResponse(receivedReportItems)
                    filterAndDisplayReceiveItems(currentDate,currentDate)
                }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getDueReport(Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                val lotteryResponse = response.body()
                lotteryResponse?.let {
                    dueReportItems = it.result
                    handleDueReportResponse(dueReportItems)
                    filterAndDisplayDueItems(currentDate,currentDate)
                }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }

        binding.tvSales.setOnClickListener {
            toggleCardVisibility(binding.salesCardView, isSalesCardVisible)
            isSalesCardVisible = !isSalesCardVisible
        }

        binding.tvStock.setOnClickListener {
            toggleCardVisibility(binding.StockCardView, isStockCardVisible)
            isStockCardVisible = !isStockCardVisible
        }

        binding.tvReturn.setOnClickListener {
            toggleCardVisibility(binding.returnCardView, isReturnCardVisible)
            isReturnCardVisible = !isReturnCardVisible
        }

        binding.tvReceive.setOnClickListener {
            toggleCardVisibility(binding.receiveCardView, isReceiveCardVisible)
            isReceiveCardVisible = !isReceiveCardVisible
        }

        binding.tvDue.setOnClickListener {
            binding.dueCardView.visibility = View.VISIBLE
            binding.tvDue.visibility = View.GONE
        }

        binding.dueBox.setOnClickListener {
            binding.dueCardView.visibility = View.GONE
            binding.tvDue.visibility = View.VISIBLE
        }

        binding.tvSaleDateRange.setOnClickListener {
            binding.tvSaleTo.visibility = View.VISIBLE
            binding.tvSaleSelectDateRange.visibility = View.VISIBLE

            binding.tvSaleDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvSaleDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.tvstockDateRange.setOnClickListener {
            binding.tvStockTo.visibility = View.VISIBLE
            binding.tvstockSelectDateRange.visibility = View.VISIBLE

            binding.tvstockDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvstockDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.tvreturnDateRange.setOnClickListener {
            binding.tvReturnTo.visibility = View.VISIBLE
            binding.tvreturnSelectDateRange.visibility = View.VISIBLE

            binding.tvreturnDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvreturnDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.tvreceiveDateRange.setOnClickListener {
            binding.tvReceiveTo.visibility = View.VISIBLE
            binding.tvreceiveSelectDateRange.visibility = View.VISIBLE

            binding.tvreceiveDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvreceiveDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.tvdueDateRange.setOnClickListener {
            binding.tvDueTo.visibility = View.VISIBLE
            binding.tvdueSelectDateRange.visibility = View.VISIBLE

            binding.tvdueDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvdueDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.tvSaleTodayDate.setOnClickListener {
            binding.tvSaleTo.visibility = View.VISIBLE
            binding.tvSaleSelectDateRange.visibility = View.VISIBLE

            binding.tvSaleDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvSaleDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))

            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvSaleTodayDate.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvSaleSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvSaleSelectDateRange.text = selectedDate
                selectedEndDate = selectedDate
                filterAndDisplayItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvstockTodayDate.setOnClickListener {
            binding.tvStockTo.visibility = View.VISIBLE
            binding.tvstockSelectDateRange.visibility = View.VISIBLE

            binding.tvstockDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvstockDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))

            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvstockTodayDate.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayStockItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvstockSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvstockSelectDateRange.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayStockItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreturnTodayDate.setOnClickListener {
            binding.tvReturnTo.visibility = View.VISIBLE
            binding.tvreturnSelectDateRange.visibility = View.VISIBLE

            binding.tvreturnDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvreturnDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))

            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreturnTodayDate.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayReturnItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreturnSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreturnSelectDateRange.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayReturnItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreceiveTodayDate.setOnClickListener {
            binding.tvReceiveTo.visibility = View.VISIBLE
            binding.tvreceiveSelectDateRange.visibility = View.VISIBLE

            binding.tvreceiveDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvreceiveDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))

            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreceiveTodayDate.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayReceiveItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreceiveSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreceiveSelectDateRange.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayReceiveItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvdueTodayDate.setOnClickListener {
            binding.tvDueTo.visibility = View.VISIBLE
            binding.tvdueSelectDateRange.visibility = View.VISIBLE

            binding.tvdueDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvdueDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))

            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvdueTodayDate.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayDueItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvdueSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvdueSelectDateRange.text = selectedDate
                selectedStartDate = selectedDate
                filterAndDisplayDueItems(selectedStartDate, selectedEndDate)
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

    }


    private fun toggleCardVisibility(cardView: CardView, isVisible: Boolean) {
        if (isVisible) {
            cardView.visibility = View.GONE
        } else {
            cardView.visibility = View.VISIBLE
        }
    }

    private fun handleSaleReportResponse(result: List<SaleReportItem>) {
        val reportSaleAdapter = ReportSaleAdapter(applicationContext,result)
        binding.rvSalesDetails.adapter = reportSaleAdapter
        binding.rvSalesDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleStockReportResponse(result: List<StockReportItem>) {
        val stockReportAdapter = StockReportAdapter(applicationContext,result)
        binding.rvstockDetails.adapter = stockReportAdapter
        binding.rvstockDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleReturnReportResponse(result: List<ReturnItem>) {
        val returnReportAdapter = ReturnReportAdapter(applicationContext,result)
        binding.rvreturnDetails.adapter = returnReportAdapter
        binding.rvreturnDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun handleReceivedReportResponse(result: List<ReceivedReportItem>) {
        val receivedReportAdapter = ReceivedReportAdapter(applicationContext,result)
        binding.rvreceiveDetails.adapter = receivedReportAdapter
        binding.rvreceiveDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

    }

    private fun handleDueReportResponse(result: List<DueItem>) {
        val dueReportAdapter = DueReportAdapter(applicationContext,result)
        binding.rvdueDetails.adapter = dueReportAdapter
        binding.rvdueDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun filterAndDisplayItems(startDate: String, endDate: String) {
        val filteredItems = saleReportItems.filter {
            it.date in startDate..endDate
        }
        val reportSaleAdapter = ReportSaleAdapter(applicationContext, filteredItems)
        binding.rvSalesDetails.adapter = reportSaleAdapter
        binding.rvSalesDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        if (filteredItems.isEmpty()) {
            binding.noData.visibility = View.VISIBLE
        } else {
            binding.noData.visibility = View.GONE
        }
    }

    @OptIn(DelicateCoroutinesApi::class)

    private fun filterAndDisplayStockItems(startDate: String, endDate: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getStockDateChnage(Constant.customer_wirehouse_id, Constant.customer_id, startDate, endDate)
            }
            Log.i("TAG", "filterAndDisplayStockItems: "+ startDate +" "+ endDate)

            if (response.isSuccessful) {
                val lotteryResponse = response.body()
                Log.i("TAG", "filterAndDisplayStockItems: "+ response.body())
                lotteryResponse?.let {
                    stockReportItems = it.result
                    handleStockReportResponse(stockReportItems)

                    if (stockReportItems.isEmpty()) {
                        binding.stockNoData.visibility = View.VISIBLE
                    } else {
                        binding.stockNoData.visibility = View.GONE
                    }

                }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }


    private fun filterAndDisplayReturnItems(startDate: String, endDate: String) {
        val filteredItems = returnReportItems.filter {
            it.return_date in startDate..endDate
        }
        val returnReportAdapter = ReturnReportAdapter(applicationContext,filteredItems)
        binding.rvreturnDetails.adapter = returnReportAdapter
        binding.rvreturnDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        if (filteredItems.isEmpty()) {
            binding.tvReturnNoData.visibility = View.VISIBLE
        } else {
            binding.tvReturnNoData.visibility = View.GONE
        }
    }

    private fun filterAndDisplayReceiveItems(startDate: String, endDate: String) {
        val filteredItems = receivedReportItems.filter {
            it.date in startDate..endDate
        }
        val receivedReportAdapter = ReceivedReportAdapter(applicationContext,filteredItems)
        binding.rvreceiveDetails.adapter = receivedReportAdapter
        binding.rvreceiveDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        if (filteredItems.isEmpty()) {
            binding.receiveNoData.visibility = View.VISIBLE
        } else {
            binding.receiveNoData.visibility = View.GONE
        }
    }

    private fun filterAndDisplayDueItems(startDate: String, endDate: String) {
        val filteredItems = dueReportItems.filter {
            it.date in startDate..endDate
        }
        val dueReportAdapter = DueReportAdapter(applicationContext,filteredItems)
        binding.rvdueDetails.adapter = dueReportAdapter
        binding.rvdueDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        if (filteredItems.isEmpty()) {
            binding.dueNoData.visibility = View.VISIBLE
        } else {
            binding.dueNoData.visibility = View.GONE
        }
    }
}