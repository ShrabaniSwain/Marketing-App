package com.billing.marketing

import android.R
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import com.billing.marketing.databinding.ActivityAddItemSaleBinding
import kotlinx.coroutines.*

class AddItemSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemSaleBinding
    private val productNames = mutableListOf<String>()
    private val productUnits = mutableListOf<String>()
    private val productGst = mutableListOf<String>()
    private var selectedProductId: String? = null
    private val taxOptions = listOf("With Tax", "Without Tax")
    private var invoiceNumber = 0
    private lateinit var taxRate1: String

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val itemPrice = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val itemQuantity = binding.etQuantity.text.toString().trim().toDoubleOrNull() ?: 0.0

        val itemSubTotal = itemPrice * itemQuantity
        binding.tvSubTotalPrice.text = "₹${itemSubTotal}"

        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                updateSubTotal()
                updateDiscountAmount()
                calculateTotalAmount()
            }
        })

        binding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                updateSubTotal()
                updateDiscountAmount()
                calculateTotalAmount()
            }
        })

        binding.tvDiscountPercentage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateDiscountAmount()
                calculateTaxAmount()
                calculateTotalAmount()
            }
        })

        binding.tvGst.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                calculateTaxAmount()
                calculateTotalAmount()
            }
        })

        binding.tvSubTotalPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                calculateTaxAmount()
                calculateTotalAmount()
            }
        })


        binding.btnSave2.setOnClickListener {
            val qnt = binding.etQuantity.text.toString()
            val itemName = binding.etItemName.text.toString()
            val unit = binding.etUnit.text.toString()
            val tax = binding.tvTaxAmount.text.toString()
            val gst = binding.tvGst.text.toString()
            val tvTotalPrice = binding.tvTotalPrice.text.toString()
            val etQuantity = binding.etQuantity.text.toString()
            val etPrice = binding.etPrice.text.toString()
            val tvDiscountAmount = binding.tvDiscountAmount.text.toString()
            val tvTaxType = binding.tvTax.text.toString()
            val tvDiscountPercentage = binding.tvDiscountPercentage.text.toString()

            if (itemName.isEmpty() || etQuantity.isEmpty() || unit.isEmpty() || etPrice.isEmpty() || tvTotalPrice.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT)
                    .show()
            }
            else if (taxRate1.toInt() < qnt.toInt()) {
                    Toast.makeText(
                        this,
                        "Not enough stock available for this product.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            else {
                val data = Intent()
                data.putExtra("productId", selectedProductId)
                data.putExtra("itemName", itemName)
                data.putExtra("unit", unit)
                data.putExtra("tax", tax)
                data.putExtra("gst", gst)
                data.putExtra("totalPrice", tvTotalPrice)
                data.putExtra("qnt", etQuantity)
                data.putExtra("rate", etPrice)
                data.putExtra("disAmount", tvDiscountAmount)
                data.putExtra("taxType", tvTaxType)
                data.putExtra("dis", tvDiscountPercentage)

                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }

//        val adapter = ArrayAdapter(this,R.layout.simple_list_item_1, productNames)
//        binding.etItemName.setAdapter(adapter)
//        binding.etUnit.setOnClickListener { showUnitPopupMenu() }
//        binding.tvTax.setOnClickListener { showTaxOptionsMenu() }
//        binding.tvGst.setOnClickListener { showGstPopupMenu() }


        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getProductItem(Constant.customer_wirehouse_id, Constant.customer_id)
            }

            Log.i("TAG", "onCreate: "+response)
            if (response.isSuccessful) {
                val customerDetailsResponse = response.body()
                customerDetailsResponse?.let { handleProductListResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }

    private fun handleProductListResponse(result: List<ProductDetails>) {

        val adapter = ArrayAdapter(this,R.layout.simple_list_item_1, productNames)
        binding.etItemName.setAdapter(adapter)

        binding.etItemName.setOnItemClickListener { parent, _, position, _ ->
            val selectedProductName = parent.getItemAtPosition(position) as String
            val selectedProduct = result.find { it.product_name == selectedProductName }

            selectedProduct?.let {
                selectedProductId = it.product_id
                taxRate1 = it.in_stock
                val taxRate = it.tax_rate
                binding.tvGst.text = taxRate
                binding.tvTax.text = it.sell_tax_type
                binding.etUnit.text = it.product_unit
                binding.etPrice.text = Editable.Factory.getInstance().newEditable(it.selling_price_per_unit)
            }
        }
        productNames.clear()
        productNames.addAll(result.map { it.product_name })

        productUnits.clear()
        productUnits.addAll(result.map { it.product_unit })

        productGst.clear()
        productGst.addAll(result.map { it.tax_rate})

//        val nameAdapter = binding.etItemName.adapter as ArrayAdapter<String>
//        nameAdapter.notifyDataSetChanged()
    }

    private fun showUnitPopupMenu() {
        val popupMenu = PopupMenu(this, binding.etUnit)
        for (unit in productUnits) {
            popupMenu.menu.add(unit)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            binding.etUnit.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun showGstPopupMenu() {
        val popupMenu = PopupMenu(this, binding.tvGst)
        for (gst in productGst) {
            popupMenu.menu.add(gst)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            binding.tvGst.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun showTaxOptionsMenu() {
        val popupMenu = PopupMenu(this, binding.tvTax)
        for (option in taxOptions) {
            popupMenu.menu.add(option)
        }

        popupMenu.setOnMenuItemClickListener { item ->
            binding.tvTax.text = item.title
            true
        }

        popupMenu.show()
    }

    private fun updateSubTotal() {
        val itemPrice = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val itemQuantity = binding.etQuantity.text.toString().trim().toDoubleOrNull() ?: 0.0

        val itemSubTotal = itemPrice * itemQuantity
        binding.tvSubTotalPrice.text = "₹${itemSubTotal}"
    }

    private fun updateDiscountAmount() {
        val itemPrice = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val itemQuantity = binding.etQuantity.text.toString().trim().toDoubleOrNull() ?: 0.0
        val discountPercentage = binding.tvDiscountPercentage.text.toString().trim().toDoubleOrNull() ?: 0.0

        val itemSubTotal = itemPrice * itemQuantity
        val discountAmount = (itemSubTotal * discountPercentage / 100.0)
        binding.tvDiscountAmount.text = Editable.Factory.getInstance().newEditable(String.format("%.2f", discountAmount))
    }

    private fun calculateDiscountPercentage() {
        val itemPrice = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val itemQuantity = binding.etQuantity.text.toString().trim().toDoubleOrNull() ?: 0.0
        val discountAmount = binding.tvDiscountAmount.text.toString().trim().toDoubleOrNull() ?: 0.0

        if (itemPrice != 0.0 && itemQuantity != 0.0) {
            val itemSubTotal = itemPrice * itemQuantity

            val discountPercentage = (discountAmount * 100.0) / itemSubTotal

            val discountPercentageString = String.format("%.2f", discountPercentage)
            binding.tvDiscountPercentage.setText(discountPercentageString)
        }
    }

    private fun calculateTaxAmount() {
        val priceText = binding.tvSubTotalPrice.text.toString()
        val priceWithoutCurrency = priceText.replace("₹", "").trim()
        val itemPrice = priceWithoutCurrency.toDouble()
        val discountText = binding.tvDiscountAmount.text.toString()
        val discount = if (discountText.isNotBlank()) {
            discountText.toDouble()
        } else {
            0.0
        }
        val taxRateText = binding.tvGst.text.toString()

        val taxRate = taxRateText.split(" ").lastOrNull()?.replace("%", "")?.toDoubleOrNull() ?: 0.0
        val adjustedSubtotal = itemPrice - discount
        Log.i("TAG", "calculateTaxAmount: "+ itemPrice+" " + discount)
        val taxAmount = (adjustedSubtotal * taxRate / 100.0)
        val withtax = 100 + taxRate
        val taxAmount1 = (adjustedSubtotal/withtax*taxRate)
        Log.i("TAG", "calculateTaxAmount: "+taxAmount)


        if (binding.tvTax.text == "With Tax") {
            binding.tvTaxAmount.text = Editable.Factory.getInstance().newEditable(String.format("%.2f", taxAmount1))
        }
        else{
            binding.tvTaxAmount.text = Editable.Factory.getInstance().newEditable(String.format("%.2f", taxAmount))
        }
    }

    private fun calculateTotalAmount() {
        val priceText = binding.tvSubTotalPrice.text.toString()
        val priceWithoutCurrency = priceText.replace("₹", "").trim()
        val itemPrice = priceWithoutCurrency.toDouble()
        val discountText = binding.tvDiscountAmount.text.toString()
        val discount = if (discountText.isNotBlank()) {
            discountText.toDouble()
        } else {
            0.0
        }
        val taxRateText = binding.tvGst.text.toString()
        val tvTaxAmount = binding.tvTaxAmount.text.toString().toDouble()

        val taxRate = taxRateText.split(" ").lastOrNull()?.replace("%", "")?.toDoubleOrNull() ?: 0.0
        val adjustedSubtotal = itemPrice - discount + tvTaxAmount
        Log.i("TAG", "calculateTaxAmount: "+ adjustedSubtotal)
        val amountPrice = String.format("%.2f", adjustedSubtotal)
        binding.tvTotalPrice.text =
                Editable.Factory.getInstance().newEditable(amountPrice)

    }

}