package com.billing.marketing

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.ItemMarketDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MarketDetailsAdapter(private val context: Context,private val saleItem: List<SaleItem>) : RecyclerView.Adapter<MarketDetailsAdapter.CardViewHolder>() {
    private var filteredMarketItems: List<SaleItem> = saleItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_market_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredMarketItems[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CustomerDeatils::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("customerId", item.customer_id)
            intent.putExtra("saleID", item.sale_id)
            intent.putExtra("amount", item.amount)
            intent.putExtra("invoiceNo", item.invoice_no)
            intent.putExtra("name", item.customer_name)
            intent.putExtra("date", item.sale_date)
            intent.putExtra("paid", item.paid)
            intent.putExtra("balance", item.balance)
            intent.putExtra("mobile_number", item.mobile_number)
            intent.putExtra("description", item.description)
            intent.putExtra("payment_type", item.payment_type)
            intent.putExtra("state", item.state)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredMarketItems.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMarketDetailsBinding.bind(itemView)

        fun bind(item: SaleItem) {
            binding.tvName.text = item.customer_name
            binding.tvInvoice.text = "#${item.invoice_no}"
            binding.tvQnt.text = if (item.qnt.isNullOrEmpty()) "0" else "${item.qnt}"
            binding.tvAmount.text = formatAndAddCurrencySymbol(item.amount)
            binding.tvBalance.text = formatAndAddCurrencySymbol(item.balance)
            binding.tvPaid.text = formatAndAddCurrencySymbol(item.paid)

            binding.btnReceive.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.dialog_receive, null)
                dialogBuilder.setView(inflater)
                val etAmount = inflater.findViewById<EditText>(R.id.etAmount)
                val etDate = inflater.findViewById<TextView>(R.id.etDate)
                val tvClose = inflater.findViewById<ImageView>(R.id.tvClose)
                val btnReceiveDialog = inflater.findViewById<Button>(R.id.btnReceiveDialog)
                val progressBarDialog = inflater.findViewById<ProgressBar>(R.id.progressBar)
                val editableAmount = Editable.Factory.getInstance().newEditable(item.balance)
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
                    else if (item.balance == "0"){
                        Toast.makeText(context, "You have already paid all due amounts.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        progressBarDialog.visibility = View.VISIBLE
                            Handler().postDelayed({
                                val duePayment = PaymentDueData(
                                    item.sale_id,
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
                                        progressBarDialog.visibility = View.GONE
                                        Log.i("TAG", "onResponse: " + response.body())
                                        if (response.isSuccessful) {
                                            val updateProfileResponse = response.body()
                                            updateProfileResponse?.let {
                                                if (it.isSuccess) {
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
                                        progressBarDialog.visibility = View.GONE
                                        Log.e("API", "API call failed with exception: ${t.message}")
                                        Toast.makeText(
                                            context,
                                            "Failed to receive due payment. Please try again.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            }, 2000)
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
        }
    }

    private fun formatAndAddCurrencySymbol(value: String?): String {
        val amount = value?.toDoubleOrNull() ?: 0.0
        val formattedAmount = String.format("%.2f", amount)
        return "₹${if (amount == 0.0) "0" else formattedAmount}"
    }

    fun updateList(newList: List<SaleItem>) {
        filteredMarketItems = newList
        notifyDataSetChanged()
    }


}