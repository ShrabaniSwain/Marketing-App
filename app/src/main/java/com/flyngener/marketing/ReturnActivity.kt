package com.flyngener.marketing

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import com.flyngener.marketing.databinding.ActivityMainBinding
import com.flyngener.marketing.databinding.ActivityReturnBinding
import com.flyngener.marketing.databinding.ActivitySaleBinding
import java.text.SimpleDateFormat
import java.util.*

class ReturnActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReturnBinding
    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentDate: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editableText = Editable.Factory.getInstance().newEditable(currentDate)
        binding.tvDate.text = editableText

        binding.btnAddItems2.setOnClickListener {
            val intent = Intent(this@ReturnActivity, AddItemReturnActivty::class.java)
            startActivity(intent)
        }

        binding.ibBack2.setOnClickListener {
            onBackPressed()
        }

        val itemsAdapter = ItemsAdapter()
        binding.rvItem.adapter = itemsAdapter
        binding.rvItem.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        binding.tvDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                val editableTextDate = Editable.Factory.getInstance().newEditable(selectedDate)
                binding.tvDate.text = editableTextDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

    }
}