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
import androidx.recyclerview.widget.LinearLayoutManager
import com.billing.marketing.databinding.ActivityUpdateSaleItemBinding
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

class UpdateSaleItem : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateSaleItemBinding
    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH)
    private val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentDate: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
    private val productNames = mutableListOf<String>()
    private var productId: String? = null
    private var itemName: String? = null
    private var unit: String? = null
    private var tax: String? = null
    private var gst: String? = null
    private var totalPrice: String? = null
    private var qnt: String? = null
    private var rate: String? = null
    private var disAmount: String? = null
    private var taxType: String? = null
    private var dis: String? = null
    private val productPhNo = mutableMapOf<String, String>()
    private val customerId = mutableMapOf<String, String>()
    private val itemDetailsList = mutableListOf<SaleProductList>()
    private val editItemDetails = mutableListOf<EditProductModel>()
    private var id: String = "0"
    private val paymentMethod = listOf("Cash", "Bank")
    private val stateSelect = listOf("Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chandigarh", "Chhattisgarh", "Dadra and Nagar Haveli and Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala", "Ladakh", "Lakshadweep", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Puducherry", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal")


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateSaleItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customerName = intent.getStringExtra("customerName")
        id = intent.getStringExtra("customerId").toString()
        val amount = intent.getStringExtra("amount").toString()
        val invoiceNo = intent.getStringExtra("invoiceNo").toString()
        val date = intent.getStringExtra("date").toString()
        val paid = intent.getStringExtra("paid").toString()
        val balance = intent.getStringExtra("balance").toString()
        val mobileNo = intent.getStringExtra("mobile_number").toString()
        val description = intent.getStringExtra("description").toString()
        val paymentType = intent.getStringExtra("payment_type").toString()
        val state = intent.getStringExtra("state").toString()

        val editableText = Editable.Factory.getInstance().newEditable(date)
        val editableCustomerName = Editable.Factory.getInstance().newEditable(customerName)
        val editableMobileNo = Editable.Factory.getInstance().newEditable(mobileNo)
        val editabledescription = Editable.Factory.getInstance().newEditable(description)
        val formattedAmount = String.format("%.2f", amount.toDouble())
        val editableAmount = Editable.Factory.getInstance().newEditable(formattedAmount)
        val editableInvoice = Editable.Factory.getInstance().newEditable(invoiceNo)
        val formattedPaid = String.format("%.2f", paid.toDouble())
        val editablePaid = Editable.Factory.getInstance().newEditable(formattedPaid)
        val formattedbalance = String.format("%.2f", balance.toDouble())
        val editableBalance = Editable.Factory.getInstance().newEditable(formattedbalance)
        binding.tvDate.text = editableText
        binding.etCustomer.text = editableCustomerName
        binding.etContactNo.text = editableMobileNo
        binding.tvCash.text = paymentType
        binding.tvSelectState.text = state
        binding.description.text = editabledescription
        binding.amount.text = editableAmount
        binding.etInvNo.text = editableInvoice
        binding.paid.text = editablePaid
        binding.balance.text = editableBalance

        binding.btnAddItems2.setOnClickListener {
            val intent = Intent(this@UpdateSaleItem, AddItemSaleActivity::class.java)
            startActivityForResult(intent, 2)
        }

        binding.ibBack2.setOnClickListener {
            onBackPressed()
        }

        binding.tvCash.setOnClickListener{
            showPaymentOptionsMenu()
        }

        binding.tvSelectState.setOnClickListener {
            showStateOptionsMenu()
        }
        binding.amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateBalance()
            }
        })

        binding.paid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateBalance()
            }
        })
        binding.tvDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(year - 1900, month, day))
                val editableTextDate = Editable.Factory.getInstance().newEditable(selectedDate)
                binding.tvDate.text = editableTextDate
            }, currentYear, currentMonth, currentDay)
            datePickerDialog.show()
        }

        binding.btnSave2.setOnClickListener {
            if (binding.etCustomer.text.isEmpty() || binding.tvDate.text.isEmpty() || binding.etContactNo.text.isEmpty() || binding.amount.text.isEmpty() || binding.paid.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.tvSelectState.text == "Select State"){
                Toast.makeText(this, "Please select the state.", Toast.LENGTH_SHORT).show()
            }
            else if (binding.tvCash.text == "Select payment type"){
                Toast.makeText(this, "Please select the payment type.", Toast.LENGTH_SHORT).show()
            }
            else {
                saveSalesDataToApi()
            }
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
        val customAutoCompleteAdapter = CustomCustomerName(this, productNames)
        binding.etCustomer.setAdapter(customAutoCompleteAdapter)

        binding.etCustomer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString().toLowerCase()
                val filteredNames = productNames.filter { it.toLowerCase().contains(userInput) }.toMutableList()
                Log.i("TAG", "onTextChanged: "+filteredNames)
                filteredNames.add(0, "Add Customer")
                val customAutoCompleteAdapter = CustomCustomerName(this@UpdateSaleItem, filteredNames)
                binding.etCustomer.setAdapter(customAutoCompleteAdapter)
                binding.etCustomer.setOnItemClickListener { _, _, position, _ ->
                    val selectedCustomerName = filteredNames[position]
                    if (selectedCustomerName != "Add Customer") {
                        val mobileNumber = productPhNo[selectedCustomerName]
                        id = customerId[selectedCustomerName].toString()
                        binding.etCustomer.setText(selectedCustomerName)
                        binding.etContactNo.setText(mobileNumber)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })



        GlobalScope.launch(Dispatchers.Main) {
            val saleId = intent.getStringExtra("saleID")
            Log.i("TAG", "saleid: "+ saleId)
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getProductSaleList(saleId.toString(),Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                val lotteryResponse = response.body()
                lotteryResponse?.let { handleSaleProductResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }

    companion object {
        private const val ADD_ITEM_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val updatedPosition = data?.getStringExtra("item_position")

            productId = data?.getStringExtra("productId")
            itemName = data?.getStringExtra("itemName")
            unit = data?.getStringExtra("unit")
            tax = data?.getStringExtra("tax")
            gst = data?.getStringExtra("gst")
            totalPrice = data?.getStringExtra("totalPrice")
            qnt = data?.getStringExtra("qnt")
            rate = data?.getStringExtra("rate")
            disAmount = data?.getStringExtra("disAmount")
            taxType = data?.getStringExtra("taxType")
            dis = data?.getStringExtra("dis")

            itemName?.let {
                val itemDetails = SaleProductList(productId.toString(),it,"","","",  unit.toString(),qnt.toString(),rate.toString(),disAmount.toString(),
                    tax.toString(), totalPrice.toString(),taxType.toString(),dis.toString(), gst.toString())
                val editItem = EditProductModel(productId.toString(),it,qnt.toString(),unit.toString(),rate.toString(),dis.toString(),disAmount.toString(),gst.toString(),
                    tax.toString(),totalPrice.toString(),taxType.toString())

                itemDetailsList[updatedPosition!!.toInt()] = itemDetails
                if (editItemDetails.size > updatedPosition.toInt()) {
                    editItemDetails[updatedPosition.toInt()] = editItem
                }
                binding.rvItem.adapter?.notifyItemChanged(updatedPosition.toInt())
            }
        }
        else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val updatedPosition = data?.getStringExtra("item_position")

            productId = data?.getStringExtra("productId")
            itemName = data?.getStringExtra("itemName")
            unit = data?.getStringExtra("unit")
            tax = data?.getStringExtra("tax")
            gst = data?.getStringExtra("gst")
            totalPrice = data?.getStringExtra("totalPrice")
            qnt = data?.getStringExtra("qnt")
            rate = data?.getStringExtra("rate")
            disAmount = data?.getStringExtra("disAmount")
            taxType = data?.getStringExtra("taxType")
            dis = data?.getStringExtra("dis")

            itemName?.let {
                val itemDetails = SaleProductList(productId.toString(),it,"","","",  unit.toString(),qnt.toString(),rate.toString(),disAmount.toString(),
                    tax.toString(), totalPrice.toString(),taxType.toString(),dis.toString(), gst.toString())
                val editItem = EditProductModel(productId.toString(),it,qnt.toString(),unit.toString(),rate.toString(),dis.toString(),disAmount.toString(),gst.toString(),
                    tax.toString(),totalPrice.toString(),taxType.toString())
                itemDetailsList.add(itemDetails)
                editItemDetails.add(editItem)
                binding.rvItem.adapter?.notifyDataSetChanged()
            }
        }

        var totalAmount = 0.0

        for (item in itemDetailsList) {
            val totalPrice = item.amount.toDoubleOrNull() ?: 0.0
            totalAmount += totalPrice
        }
        binding.amount.text = Editable.Factory.getInstance().newEditable(totalAmount.toString())
    }

    private fun saveSalesDataToApi() {
        binding.btnSave2.isClickable = false
        binding.btnSave2.isFocusable = false
        val customerName = binding.etCustomer.text.toString()
        val mobileNumber = binding.etContactNo.text.toString()
        val invoiceNumber = binding.etInvNo.text.toString()
        val taxType = ""
        val salesAmount = binding.amount.text.toString()
        val paidAmount = binding.paid.text.toString()
        val dueAmount = binding.balance.text.toString()
        val state = binding.tvSelectState.text.toString()
        val saleDate = binding.tvDate.text.toString()
        val payMethod = binding.tvCash.text.toString()
        val taxAmount = ""
        val description = binding.description.text.toString()
        val saleId = intent.getStringExtra("saleID")

            val saleTableData = EditSaleModel(
                sale_id = saleId.toString(),
                sale_date = saleDate,
                warehouse_id = Constant.customer_wirehouse_id,
                sales_executive_id = Constant.customer_id,
                customer_id = id,
                customer_name = customerName,
                mobile_number = mobileNumber,
                tax_type = taxType,
                sales_amount = salesAmount,
                paid_amount = paidAmount,
                due_amount = dueAmount,
                state = state,
                pay_method = payMethod,
                tax_amount = taxAmount,
                description = description,
                productArray = itemDetailsList
            )

            Log.i("TAG", "saleRequest: " + saleTableData)
            val call = RetrofitClient.api.editSale(saleTableData)
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
                                Toast.makeText(
                                    applicationContext,
                                    "Sale details updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to update sale details. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Log.e("API", "API call failed with code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                    Log.e("API", "API call failed with exception: ${t.message}")
                    Toast.makeText(
                        applicationContext,
                        "Failed to update sale details. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
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

        val nameAdapter = binding.etCustomer.adapter as CustomCustomerName
        nameAdapter.notifyDataSetChanged()
    }
    private fun handleSaleProductResponse(result: List<SaleProductList>) {
        itemDetailsList.clear()
        itemDetailsList.addAll(result)
        val stockAdapter = EditSaleAdapter(applicationContext,itemDetailsList)
        binding.rvItem.adapter = stockAdapter
        binding.rvItem.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

        stockAdapter.setOnItemClickedListener { position ->
            val item = itemDetailsList[position]
            val intent = Intent(this, UpdateItemACtivity::class.java)
            intent.putExtra("item_position", position.toString())
            intent.putExtra("itemName", item.product_name)
            intent.putExtra("productId", item.product_id)
            intent.putExtra("unit", item.unit)
            intent.putExtra("tax", item.tax_amount)
            intent.putExtra("gst", item.tax_percentage)
            intent.putExtra("totalPrice", item.amount)
            intent.putExtra("qnt", item.quantity)
            intent.putExtra("rate", item.sale_price)
            intent.putExtra("disAmount", item.dis_amt)
            intent.putExtra("taxType", item.tax_type)
            intent.putExtra("dis", item.dis_percentage)
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE)
        }
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
        val totalAmountStr = binding.amount.text.toString()
        val paidAmountStr = binding.paid.text.toString()

        if (totalAmountStr.isNotEmpty() && paidAmountStr.isNotEmpty()) {
            val totalAmount = totalAmountStr.toDouble()
            val paidAmount = paidAmountStr.toDouble()
            val balanceAmount = totalAmount - paidAmount
            val amountPrice = String.format("%.2f", balanceAmount)

            val dueAmount = Editable.Factory.getInstance().newEditable(amountPrice)
            binding.balance.text = dueAmount
        }
    }
}

class CustomCustomerName(context: Context, customers: List<String>) :
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
                clear()
                notifyDataSetChanged()
            }
        }

    }
}