package com.billing.marketing

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.billing.marketing.databinding.ActivityCustomerDeatilsBinding
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

class CustomerDeatils : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDeatilsBinding
    lateinit var saleId : String
    lateinit var customerName : String
    lateinit var amount : String
    lateinit var invoiceNo : String
    lateinit var date : String
    lateinit var paid : String
    lateinit var balance : String
    lateinit var mobileNo : String
    lateinit var description : String
    lateinit var paymentType : String
    lateinit var state : String
    lateinit var customerID : String
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDeatilsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivEdit.setOnClickListener {
            val intent = Intent(this@CustomerDeatils, UpdateSaleItem::class.java)
            intent.putExtra("saleID", saleId)
            intent.putExtra("customerId", customerID)
            intent.putExtra("customerName", customerName)
            intent.putExtra("amount", amount)
            intent.putExtra("invoiceNo", invoiceNo)
            intent.putExtra("date", date)
            intent.putExtra("paid", paid)
            intent.putExtra("balance", balance)
            intent.putExtra("mobile_number", mobileNo)
            intent.putExtra("description", description)
            intent.putExtra("payment_type", paymentType)
            intent.putExtra("state", state)
            startActivity(intent)
        }

        binding.ivDelete.setOnClickListener {
            deleteSale(saleId,Constant.customer_wirehouse_id,Constant.customer_id)
            Log.i("TAG", "delete: "+saleId)
        }

        binding.ivPrint.setOnClickListener {
            onPrintButtonClick()
        }

        customerName = intent.getStringExtra("name").toString()
        customerID = intent.getStringExtra("customerId").toString()
        amount = intent.getStringExtra("amount").toString()
        invoiceNo = intent.getStringExtra("invoiceNo").toString()
        date = intent.getStringExtra("date").toString()
        paid = intent.getStringExtra("paid").toString()
        balance = intent.getStringExtra("balance").toString()
        mobileNo = intent.getStringExtra("mobile_number").toString()
        description = intent.getStringExtra("description").toString()
        paymentType = intent.getStringExtra("payment_type").toString()
        state = intent.getStringExtra("state").toString()
        binding.tvName.text = customerName
        binding.tvDate.text = date
        binding.tvInvoiceNumber.text = "#${invoiceNo}"
        val amountPrice = String.format("%.2f", amount.toDouble())
        binding.tvTotalAmount.text = if (amount.isNullOrEmpty()) "₹0.0" else "₹${amountPrice}"

        binding.ivShare.setOnClickListener {
            val message = "Customer Name: $customerName\nDate: $date\nInvoice Number: #$invoiceNo\nTotal Amount: ${if (amount.isNullOrEmpty()) "₹0" else "₹$balance"}"

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Invoice Details")
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        binding.btnReceive.setOnClickListener {view ->
            val context = view.context
            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context).inflate(R.layout.dialog_receive, null)
            dialogBuilder.setView(inflater)
            val etAmount = inflater.findViewById<EditText>(R.id.etAmount)
            val etDate = inflater.findViewById<TextView>(R.id.etDate)
            val btnReceiveDialog = inflater.findViewById<Button>(R.id.btnReceiveDialog)
            val editableAmount = Editable.Factory.getInstance().newEditable(balance)
            val tvClose = inflater.findViewById<ImageView>(R.id.tvClose)

            etAmount.text = editableAmount
            val dialog = dialogBuilder.create()

            tvClose.setOnClickListener {
                dialog.dismiss()
            }

            btnReceiveDialog.setOnClickListener {
                val enteredAmount = etAmount.text.toString()
                if (enteredAmount.isEmpty() || enteredAmount == "0") {
                    Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT)
                        .show()
                }
                else if (balance == "0"){
                    Toast.makeText(context, "You have already paid all due amounts.", Toast.LENGTH_SHORT).show()
                }
                else{
                        binding.progressBar.visibility = View.VISIBLE
                            val duePayment = PaymentDueData(
                                saleId,
                                Constant.customer_id,
                                "Cash",
                                etAmount.text.toString(),
                                etDate.text.toString(),
                                Constant.customer_wirehouse_id
                            )
                            Log.i("TAG", "bind: " + duePayment)
                            val call = RetrofitClient.api.paySaleDue(duePayment)
                            call.enqueue(object : Callback<ProfileDetailsResponse> {
                                override fun onResponse(
                                    call: Call<ProfileDetailsResponse>,
                                    response: Response<ProfileDetailsResponse>
                                ) {
                                    Log.i("TAG", "onResponse: " + response.body())
                                    if (response.isSuccessful) {
                                        val updateProfileResponse = response.body()
                                        updateProfileResponse?.let {
                                            if (it.isSuccess) {
                                                binding.progressBar.visibility = View.GONE
                                                Toast.makeText(
                                                    context,
                                                    "Due payment received successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dialog.dismiss()
                                                val intent = Intent(context, MainActivity::class.java)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to receive due payment. Please try again.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Log.e("API", "API call failed with code ${response.code()}")
                                    }
                                }

                                override fun onFailure(
                                    call: Call<ProfileDetailsResponse>,
                                    t: Throwable
                                ) {
                                    binding.progressBar.visibility = View.GONE
                                    Log.e("API", "API call failed with exception: ${t.message}")
                                    Toast.makeText(
                                        context,
                                        "Failed to receive due payment. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                }
            }
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            etDate.text = currentDate

            etDate.setOnClickListener {
                val datePickerDialog = DatePickerDialog(context, { _, year, month, day ->
                    val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(year - 1900, month, day))
                    etDate.text = selectedDate
                }, currentYear, currentMonth, currentDay)
                datePickerDialog.show()
            }
            dialog.show()

        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            saleId = intent.getStringExtra("saleID").toString()
            Log.i("TAG", "saleid: "+ saleId)
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.api.getProductSaleList(saleId,Constant.customer_wirehouse_id,Constant.customer_id)
            }

            if (response.isSuccessful) {
                binding.progressBar.visibility = View.GONE
                val lotteryResponse = response.body()
                lotteryResponse?.let { handleSaleProductResponse(it.result) }
            } else {
                Log.i("TAG", "API Call failed with error code: ${response.code()}")
            }
        }
    }

    private fun handleSaleProductResponse(result: List<SaleProductList>) {
        val stockAdapter = CustomerDeatilsAdapter(applicationContext,result)
        binding.rvCustomerDetails.adapter = stockAdapter
        binding.rvCustomerDetails.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteSale(saleId: String, warehouseId: String, salesExecutiveId: String) {
        binding.progressBar.visibility = View.VISIBLE
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            binding.progressBar.visibility = View.GONE
        }, 8000)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.getDeleteSaleList(saleId, warehouseId, salesExecutiveId)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.isSuccess == true) {
                        runOnUiThread {
                            Toast.makeText(this@CustomerDeatils, "Sale deleted successfully", Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility = View.GONE
                            val intent = Intent(this@CustomerDeatils, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            // Handle the case where isSuccess is not true
                            Toast.makeText(this@CustomerDeatils, "Sale deletion failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        // Handle the case where the API call was not successful (e.g., network error)
                        Toast.makeText(this@CustomerDeatils, "Failed to delete sale. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CustomerDeatils, "An error occurred while deleting the sale.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onPrintButtonClick() {
        val pdfUrl = "https://flyngener.com/rr/admin/mobile/invoice.php?sale_id=$saleId"
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(pdfUrl))
        request.setTitle("PDF Download")
        request.setDescription("Downloading PDF")
        val destinationDirectory = Environment.DIRECTORY_DOWNLOADS
        request.setDestinationInExternalPublicDir(destinationDirectory, "invoice.pdf")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val downloadId = downloadManager.enqueue(request)
        Toast.makeText(this, "PDF download started", Toast.LENGTH_SHORT).show()
    }

}