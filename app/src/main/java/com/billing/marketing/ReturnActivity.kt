package com.billing.marketing

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.billing.marketing.databinding.ActivityReturnBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ReturnActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReturnBinding
    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    private val itemDetailsList = mutableListOf<ProductData>()
    private val productInfo = mutableListOf<ProductInfo>()
    private var invoiceNumber = 0
    private lateinit var qnt: String
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editableText = Editable.Factory.getInstance().newEditable(currentDate)
        binding.tvDate.text = editableText

        binding.btnAddItems2.setOnClickListener {
            val intent = Intent(this@ReturnActivity, AddItemReturnActivty::class.java)
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE)
        }

        binding.ibBack2.setOnClickListener {
            onBackPressed()
        }

        binding.btnSave2.setOnClickListener {
            if (binding.totalAmount.text.isEmpty() || binding.tvDate.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.rvItem.isEmpty()){
                Toast.makeText(this, "Please add the return item.", Toast.LENGTH_SHORT).show()
            }
            else {
                saveReturnDataToApi()
            }
        }

        val itemsAdapter = ItemsAdapter(applicationContext,itemDetailsList)
        binding.rvItem.adapter = itemsAdapter
        binding.rvItem.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        binding.tvDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                val editableTextDate = Editable.Factory.getInstance().newEditable(selectedDate)
                binding.tvDate.text = editableTextDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            invoiceNumber++
            val productId = data?.getStringExtra("productId")
            val itemName = data?.getStringExtra("itemName")
            val unit = data?.getStringExtra("unit")
            val tax = data?.getStringExtra("tax")
            val gst = data?.getStringExtra("gst")
            val totalPrice = data?.getStringExtra("totalPrice")
            qnt = data?.getStringExtra("qnt").toString()
            val rate = data?.getStringExtra("rate")
            val disAmount = data?.getStringExtra("disAmount")
            val taxType = data?.getStringExtra("taxType")
            val dis = data?.getStringExtra("dis")

            itemName?.let {
                val itemDetails = ProductData(productId.toString(),it, qnt, unit.toString(),rate.toString(),dis.toString(),disAmount.toString(),gst.toString(),
                    tax.toString(), totalPrice.toString(),taxType.toString())
                itemDetailsList.add(itemDetails)
                val productData = ProductInfo(productId.toString(),qnt)
                productInfo.add(productData)
                binding.rvItem.adapter?.notifyDataSetChanged()

                var totalAmount = 0.0
                for (item in itemDetailsList) {
                    totalAmount += totalPrice?.toDoubleOrNull() ?: 0.0
                }
                val editableAmount = Editable.Factory.getInstance().newEditable(totalAmount.toString())
                binding.totalAmount.text = editableAmount
            }
        }


    }

    companion object {
        private const val ADD_ITEM_REQUEST_CODE = 1
    }

    private fun saveReturnDataToApi() {
        binding.btnSave2.isClickable = false
        binding.btnSave2.isFocusable = false
        val salesAmount = binding.totalAmount.text.toString()
        val description = binding.tvDescription.text.toString()
        val data = binding.tvDate.text.toString()

        val saleTableData = ReturnRequest(
            warehouse_id = Constant.customer_wirehouse_id,
            sales_executive_id = Constant.customer_id,
            total_product = invoiceNumber.toString(),
            return_date = data,
            binding.totalAmount.text.toString(),
            product_info = productInfo

        )

        Log.i("TAG", "saleRequest: "+saleTableData)
        val call = RetrofitClient.api.sendReturnData(saleTableData)
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>) {
                Log.i("TAG", "successs: "+response.isSuccessful)
                Log.i("TAG", "message: "+response.body())
                if (response.isSuccessful) {
                    val addsaleResponse = response.body()
                    Log.i("TAG", "onResponse: "+response.body())
                    addsaleResponse?.let {
                        if (it.isSuccess) {
                            Toast.makeText(applicationContext, "Return request send successfully", Toast.LENGTH_SHORT).show()
                            val intent =
                                Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Failed to send return request. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(applicationContext, "Failed to send return request. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}