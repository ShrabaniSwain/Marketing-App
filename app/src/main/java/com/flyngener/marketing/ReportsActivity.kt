package com.flyngener.marketing

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityCustomerDeatilsBinding
import com.flyngener.marketing.databinding.ActivityReportsBinding
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
    val currentDate: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)

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

        val reportSaleAdapter = ReportSaleAdapter()
        binding.rvSalesDetails.adapter = reportSaleAdapter
        binding.rvSalesDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val stockReportAdapter = StockReportAdapter()
        binding.rvstockDetails.adapter = stockReportAdapter
        binding.rvstockDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val returnReportAdapter = ReturnReportAdapter()
        binding.rvreturnDetails.adapter = returnReportAdapter
        binding.rvreturnDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val receivedReportAdapter = ReceivedReportAdapter()
        binding.rvreceiveDetails.adapter = receivedReportAdapter
        binding.rvreceiveDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        val dueReportAdapter = DueReportAdapter()
        binding.rvdueDetails.adapter = dueReportAdapter
        binding.rvdueDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

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
            binding.tvSaleDate.backgroundTintList = ContextCompat.getColorStateList(this,R.color.white)
            binding.tvSaleDate.setTextColor(ContextCompat.getColor(this, R.color.black))
        }


        binding.tvstockDateRange.setOnClickListener {
            binding.tvStockTo.visibility = View.VISIBLE
            binding.tvstockSelectDateRange.visibility = View.VISIBLE

            binding.tvstockDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvstockDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvstockDate.backgroundTintList = ContextCompat.getColorStateList(this,R.color.white)
            binding.tvstockDate.setTextColor(ContextCompat.getColor(this, R.color.black))
        }


        binding.tvreturnDateRange.setOnClickListener {
            binding.tvReturnTo.visibility = View.VISIBLE
            binding.tvreturnSelectDateRange.visibility = View.VISIBLE

            binding.tvreturnDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvreturnDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvreturnDate.backgroundTintList = ContextCompat.getColorStateList(this,R.color.white)
            binding.tvreturnDate.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        binding.tvreceiveDateRange.setOnClickListener {
            binding.tvReceiveTo.visibility = View.VISIBLE
            binding.tvreceiveSelectDateRange.visibility = View.VISIBLE

            binding.tvreceiveDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvreceiveDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvreceiveDate.backgroundTintList = ContextCompat.getColorStateList(this,R.color.white)
            binding.tvreceiveDate.setTextColor(ContextCompat.getColor(this, R.color.black))
        }


        binding.tvdueDateRange.setOnClickListener {
            binding.tvDueTo.visibility = View.VISIBLE
            binding.tvdueSelectDateRange.visibility = View.VISIBLE

            binding.tvdueDateRange.backgroundTintList = ContextCompat.getColorStateList(this,R.color.light_blue)
            binding.tvdueDateRange.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvdueDate.backgroundTintList = ContextCompat.getColorStateList(this,R.color.white)
            binding.tvdueDate.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        binding.tvSaleTodayDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvSaleTodayDate.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvSaleSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvSaleSelectDateRange.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvstockTodayDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvstockTodayDate.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvstockSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvstockSelectDateRange.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreturnTodayDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreturnTodayDate.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreturnSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreturnSelectDateRange.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreceiveTodayDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreceiveTodayDate.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvreceiveSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvreceiveSelectDateRange.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvdueTodayDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvdueTodayDate.text = selectedDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.tvdueSelectDateRange.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                binding.tvdueSelectDateRange.text = selectedDate
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
}