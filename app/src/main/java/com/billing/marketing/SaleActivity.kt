package com.billing.marketing

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.billing.marketing.databinding.ActivitySaleBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaleBinding
    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    private val productNames = mutableListOf<String>()
    private val productPhNo = mutableMapOf<String, String>()
    private val customerId = mutableMapOf<String, String>()
    private var id: String = "0"
    private val itemDetailsList = mutableListOf<ProductData>()
    private var invoiceNumber = 0
    private val paymentMethod = listOf("Cash", "Bank")
    private val stateSelect = listOf("Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Puducherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal")


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editableText = Editable.Factory.getInstance().newEditable(currentDate)
        binding.tvDate.text = editableText

        binding.ibBack2.setOnClickListener{
            onBackPressed()
        }
        binding.btnAddItems2.setOnClickListener {
            val intent = Intent(this@SaleActivity, AddItemSaleActivity::class.java)
            startActivityForResult(intent, 2)
        }

        binding.tvCash.setOnClickListener{
            showPaymentOptionsMenu()
        }

        binding.tvSelectState.setOnClickListener {
            showStateOptionsMenu()
        }

        binding.totalAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateBalance()
            }
        })

        binding.tvPaidAmt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateBalance()
            }
        })

        binding.btnSave2.setOnClickListener {
            if (binding.etCustomer.text.isEmpty() || binding.tvDate.text.isEmpty() || binding.etContactNo.text.isEmpty() || binding.totalAmount.text.isEmpty() || binding.tvPaidAmt.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.tvSelectState.text == "Select State"){
                Toast.makeText(this, "Please select the state.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.tvCash.text == "Select payment type"){
                Toast.makeText(this, "Please select the payment type.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.rvItem.isEmpty()){
                Toast.makeText(this, "Please add the item.", Toast.LENGTH_SHORT).show()
            }
            else {
                saveSalesDataToApi()
            }
        }

        binding.ivshare.setOnClickListener {
            val message = "Invoice Number: #${binding.etInvNo.text}\nCustomer Name: ${binding.etCustomer.text}\nMobile Number: ${binding.etContactNo.text}\nTotal Amount: " +
                    "${if (binding.totalAmount.text.isNullOrEmpty()) "₹0" else "₹${binding.totalAmount.text}"}"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Invoice Details")
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }


        val itemsAdapter = ItemsAdapter(applicationContext, itemDetailsList)
        binding.rvItem.adapter = itemsAdapter
        binding.rvItem.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        itemsAdapter.setOnItemClickedListener { position ->
            val item = itemDetailsList[position]
            val intent = Intent(this, UpdateItemACtivity::class.java)
            intent.putExtra("item_position", position.toString())
            intent.putExtra("productId", item.product_id)
            intent.putExtra("itemName", item.details)
            intent.putExtra("unit", item.unit)
            intent.putExtra("tax", item.tax_amount2)
            intent.putExtra("gst", item.tax)
            intent.putExtra("totalPrice", item.amount)
            intent.putExtra("qnt", item.quantity)
            intent.putExtra("rate", item.sale_price)
            intent.putExtra("disAmount", item.dis_amt)
            intent.putExtra("taxType", item.tax_type2)
            intent.putExtra("dis", item.dis)
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE)
        }

        binding.tvDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                val editableTextDate = Editable.Factory.getInstance().newEditable(selectedDate)
                binding.tvDate.text = editableTextDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getCustomerDetails(Constant.customer_wirehouse_id, Constant.customer_id)
            }

            Log.i("TAG", "onCreate: "+response)
            if (response.isSuccessful) {
                val customerDetailsResponse = response.body()
                customerDetailsResponse?.let { handleCustomerResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
        val customAutoCompleteAdapter = CustomCustomerNameList(this, productNames)
        binding.etCustomer.setAdapter(customAutoCompleteAdapter)

        binding.etCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val userInput = s.toString().toLowerCase()
                val filteredNames = productNames.filter { it.toLowerCase().contains(userInput) }.toMutableList()
                filteredNames.add(0, "Add Customer")
                val customAutoCompleteAdapter = CustomCustomerNameList(this@SaleActivity, filteredNames)
                binding.etCustomer.setAdapter(customAutoCompleteAdapter)

                binding.etCustomer.setOnItemClickListener { _, _, position, _ ->
                    if (position >= 0 && position < filteredNames.size) {
                    val selectedCustomerName = filteredNames[position]
                    if (selectedCustomerName != "Add Customer") {
                        val mobileNumber = productPhNo[selectedCustomerName]
                        id = customerId[selectedCustomerName].toString()
                        binding.etCustomer.setText(selectedCustomerName)
                        binding.etContactNo.setText(mobileNumber)
                    }
                }
                }

            }
        })

    }

    private fun handleCustomerResponse(result: List<CustomerDetails>) {
        productNames.clear()
        productNames.add("Add Customer")
        productPhNo.clear()
        productPhNo.putAll(result.associate { it.customer_name to it.mobile_number })

        customerId.clear()
        customerId.putAll(result.associate { it.customer_name to it.customer_id })

        productNames.addAll(result.map { it.customer_name })
        Log.i("TAG", "Product names: $productNames")

        val nameAdapter = binding.etCustomer.adapter as CustomCustomerNameList
        nameAdapter.notifyDataSetChanged()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val productId = data?.getStringExtra("productId")
            val itemName = data?.getStringExtra("itemName")
            val unit = data?.getStringExtra("unit")
            val tax = data?.getStringExtra("tax")
            val gst = data?.getStringExtra("gst")
            val totalPrice = data?.getStringExtra("totalPrice")
            val qnt = data?.getStringExtra("qnt")
            val rate = data?.getStringExtra("rate")
            val disAmount = data?.getStringExtra("disAmount")
            val taxType = data?.getStringExtra("taxType")
            val dis = data?.getStringExtra("dis")

            itemName?.let {
                val itemDetails = ProductData(productId.toString(),it, qnt.toString(), unit.toString(),rate.toString(),dis.toString(),disAmount.toString(),gst.toString(),
                    tax.toString(), totalPrice.toString(),taxType.toString())
                itemDetailsList.add(itemDetails)
                binding.rvItem.adapter?.notifyDataSetChanged()

            }
        }
        else if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedPosition = data?.getStringExtra("item_position")
            val productId = data?.getStringExtra("productId")
            val itemName = data?.getStringExtra("itemName")
            val unit = data?.getStringExtra("unit")
            val tax = data?.getStringExtra("tax")
            val gst = data?.getStringExtra("gst")
            val totalPrice = data?.getStringExtra("totalPrice")
            val qnt = data?.getStringExtra("qnt")
            val rate = data?.getStringExtra("rate")
            val disAmount = data?.getStringExtra("disAmount")
            val taxType = data?.getStringExtra("taxType")
            val dis = data?.getStringExtra("dis")

            itemName?.let {
                val itemDetails = ProductData(productId.toString(),it, qnt.toString(), unit.toString(),rate.toString(),dis.toString(),disAmount.toString(),gst.toString(),
                    tax.toString(), totalPrice.toString(),taxType.toString())
                itemDetailsList[updatedPosition!!.toInt()] = itemDetails
                if (itemDetailsList.size > updatedPosition.toInt()) {
                    itemDetailsList[updatedPosition.toInt()] = itemDetails
                }
                binding.rvItem.adapter?.notifyItemChanged(updatedPosition.toInt())
            }
        }

        var totalAmount = 0.0
        for (item in itemDetailsList) {
            val totalPrice = item.amount.toDoubleOrNull()
            if (totalPrice != null) {
                totalAmount += totalPrice
            }
        }
        val editableAmount = Editable.Factory.getInstance().newEditable(totalAmount.toString())
        binding.totalAmount.text = editableAmount

    }
    companion object {
        private const val ADD_ITEM_REQUEST_CODE = 1
    }

    private fun saveSalesDataToApi() {
        binding.btnSave2.isClickable = false
        binding.btnSave2.isFocusable = false
        val customerName = binding.etCustomer.text.toString()
        val mobileNumber = binding.etContactNo.text.toString()
        val invoiceNumber = binding.etInvNo.text.toString()
        val taxType = ""
        val salesAmount = binding.totalAmount.text.toString()
        val paidAmount = binding.tvPaidAmt.text.toString()
        val dueAmount = binding.tvBalanceAmt.text.toString()
        val state = binding.tvSelectState.text.toString()
        val saleDate = binding.tvDate.text.toString()
        val payMethod = binding.tvCash.text.toString()
        val taxAmount = ""
        val description = binding.tvDescription.text.toString()
        val customer_id = id
        Log.i("TAG", "saveSalesDataToApi: "+ customer_id)

        val saleTableData = SaleTableData(
            warehouse_id = Constant.customer_wirehouse_id,
            sales_executive_id = Constant.customer_id,
            customer_id = customer_id,
            customer_name = customerName,
            mobile_number = mobileNumber,
            invoice_no = invoiceNumber,
            tax_type = taxType,
            sales_amount = salesAmount,
            paid_amount = paidAmount,
            due_amount = dueAmount,
            state = state,
            sale_date = saleDate,
            pay_method = payMethod,
            tax_amount = taxAmount,
            description = description,
            productArray = itemDetailsList
        )

        Log.i("TAG", "saleRequest: " + saleTableData)
        val call = RetrofitClient.api.sendSaleData(saleTableData)
        binding.progressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(
                call: Call<ProfileDetailsResponse>,
                response: Response<ProfileDetailsResponse>
            ) {
                Log.i("TAG", "successs: " + response.isSuccessful)
                Log.i("TAG", "message: " + response.body())
                if (response.isSuccessful) {
                    val addsaleResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    addsaleResponse?.let {
                        if (it.isSuccess) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Sale details added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Failed to save sale details. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    "Failed to save sale details. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun showPaymentOptionsMenu() {
        val popupMenu = PopupMenu(this, binding.tvCash)
        for (option in paymentMethod) {
            popupMenu.menu.add(option)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            binding.tvCash.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun showStateOptionsMenu() {
        val popupMenu = PopupMenu(this, binding.tvSelectState)
        for (option in stateSelect) {
            popupMenu.menu.add(option)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            binding.tvSelectState.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun updateBalance() {
        val totalAmountStr = binding.totalAmount.text.toString()
        val paidAmountStr = binding.tvPaidAmt.text.toString()

        if (totalAmountStr.isNotEmpty() && paidAmountStr.isNotEmpty()) {
            val totalAmount = totalAmountStr.toDouble()
            val paidAmount = paidAmountStr.toDouble()
            val balanceAmount = totalAmount - paidAmount
            val amountPrice = String.format("%.2f", balanceAmount)

            val dueAmount = Editable.Factory.getInstance().newEditable(amountPrice)
            binding.tvBalanceAmt.text = dueAmount
        }
    }

}

class CustomCustomerNameList(context: Context, customers: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, customers) {

    private var originalItems: List<String> = customers

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        if (position == 0) {
            view.findViewById<TextView>(android.R.id.text1).text = "Add Customer"
            view.setOnClickListener {
                val intent = Intent(context, AddCustomer::class.java)
                context.startActivity(intent)
            }
        }

        return view
    }

    override fun getFilter(): Filter {
        return customFilter
    }

    private val customFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            val filteredNames = mutableListOf<String>()

            if (constraint != null) {
                for (name in originalItems) {
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredNames.add(name)
                    }
                }
                filteredNames.add(0, "Add Customer")
            }

            results.values = filteredNames
            results.count = filteredNames.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                clear()
                addAll(results.values as List<String>)
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}