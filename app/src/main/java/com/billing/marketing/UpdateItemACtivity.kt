package com.billing.marketing

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import com.billing.marketing.databinding.ActivityUpdateItemBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateItemACtivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateItemBinding
    private val productNames = mutableListOf<String>()
    private val productUnits = mutableListOf<String>()
    private val productGst = mutableListOf<String>()
    private var selectedProductId: String? = null
    private lateinit var productId: String
    private val taxOptions = listOf("With Tax", "Without Tax")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val itemPosition = intent.getStringExtra("item_position")
        productId= intent.getStringExtra("productId").toString()
        val productName = intent.getStringExtra("itemName")
        val unit = intent.getStringExtra("unit")
        val tax = intent.getStringExtra("tax")
        val gst = intent.getStringExtra("gst")
        val totalPrice = intent.getStringExtra("totalPrice")
        val qnt = intent.getStringExtra("qnt")
        val rate = intent.getStringExtra("rate")
        val disAmount = intent.getStringExtra("disAmount")
        val taxType = intent.getStringExtra("taxType")
        val dis = intent.getStringExtra("dis")

        val editableName: Editable = productName?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")
        val editableqnt: Editable = qnt?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")
        val editableSalePrice: Editable = rate?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")
        val editableDisAmount: Editable = disAmount?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")
        val editableTaxAmount = tax?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")

        val editableAmount = totalPrice?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")

        val editableTaxPerc = gst?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")

        val editableDisPerc = dis?.let {
            Editable.Factory.getInstance().newEditable(it)
        } ?: Editable.Factory.getInstance().newEditable("")


        binding.etItemName.text = editableName
        binding.etQuantity.text = editableqnt
        binding.etUnit.text = unit
        binding.etPrice.text = editableSalePrice
        binding.tvTax.text = if (taxType.isNullOrEmpty()) "Without Tax" else taxType
        binding.tvDiscountAmount.text = editableDisAmount
        binding.tvTaxAmount.text = editableTaxAmount
        binding.tvAmount.text = editableAmount
        binding.tvDiscountPercentage.text = editableDisPerc
        binding.tvGst.text = if (editableTaxPerc.isNullOrEmpty()) "None" else editableTaxPerc


//        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, productNames)
//        binding.etItemName.setAdapter(adapter)
//        binding.etUnit.setOnClickListener { showUnitPopupMenu() }
//        binding.tvTax.setOnClickListener { showTaxOptionsMenu() }
//        binding.tvGst.setOnClickListener { showGstPopupMenu() }

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
                calculateTotalAmount()            }
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
            val updatedProductName = binding.etItemName.text.toString()
            val updatedQnt = binding.etQuantity.text.toString()
            val updatedRate = binding.etPrice.text.toString()
            val updatedDisAmount = binding.tvDiscountAmount.text.toString()
            val updatedDis = binding.tvDiscountPercentage.text.toString()
            val updatedTaxAmount = binding.tvTaxAmount.text.toString()
            val updatedUnit = binding.etUnit.text.toString()
            val updatedGst = binding.tvGst.text.toString()
            val updatedTaxType = binding.tvTax.text.toString()
            val updatedTotalAmount = binding.tvAmount.text.toString()

            val resultIntent = Intent()
            resultIntent.putExtra("item_position", itemPosition)
            resultIntent.putExtra("productId", productId)
            resultIntent.putExtra("itemName", updatedProductName)
            resultIntent.putExtra("unit", updatedUnit)
            resultIntent.putExtra("tax", updatedTaxAmount)
            resultIntent.putExtra("gst", updatedGst)
            resultIntent.putExtra("totalPrice", updatedTotalAmount)
            resultIntent.putExtra("qnt", updatedQnt)
            resultIntent.putExtra("rate", updatedRate)
            resultIntent.putExtra("disAmount", updatedDisAmount)
            resultIntent.putExtra("taxType", updatedTaxType)
            resultIntent.putExtra("dis", updatedDis)
            setResult(RESULT_OK, resultIntent)
            finish()
        }



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
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, productNames)
        binding.etItemName.setAdapter(adapter)

        binding.etItemName.setOnItemClickListener { parent, _, position, _ ->
            val selectedProductName = parent.getItemAtPosition(position) as String
            val selectedProduct = result.find { it.product_name == selectedProductName }
            selectedProduct?.let {
                selectedProductId = it.product_id
                val taxRate = it.tax_rate
                productId = it.product_id
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
//
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
        Log.i("TAG", "calculateTaxAmount: "+taxAmount)

        binding.tvTaxAmount.text = Editable.Factory.getInstance().newEditable(String.format("%.2f", taxAmount))
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
        binding.tvAmount.text = Editable.Factory.getInstance().newEditable(amountPrice)
    }
}