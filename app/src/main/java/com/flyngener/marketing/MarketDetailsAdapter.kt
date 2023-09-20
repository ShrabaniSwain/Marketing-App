package com.flyngener.marketing

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemMarketDetailsBinding
import java.text.SimpleDateFormat
import java.util.*


class MarketDetailsAdapter(private val context: Context) : RecyclerView.Adapter<MarketDetailsAdapter.CardViewHolder>() {

    private val dummyDataList: List<String> = generateDummyData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_market_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = dummyDataList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CustomerDeatils::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dummyDataList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMarketDetailsBinding.bind(itemView)

        fun bind(item: String) {
            binding.tvName.text = item
            binding.btnReceive.setOnClickListener { view ->
                val context = view.context
                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = LayoutInflater.from(context).inflate(R.layout.dialog_receive, null)
                dialogBuilder.setView(inflater)
                val etAmount = inflater.findViewById<EditText>(R.id.etAmount)
                val etDate = inflater.findViewById<TextView>(R.id.etDate)
                val btnReceiveDialog = inflater.findViewById<Button>(R.id.btnReceiveDialog)
                val dialog = dialogBuilder.create()

                btnReceiveDialog.setOnClickListener {
                    dialog.dismiss()
                }
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
                etDate.text = currentDate

                etDate.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(context, { _, year, month, day ->
                        val selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(year - 1900, month, day))
                        etDate.text = selectedDate
                    }, currentYear, currentMonth, currentDay)
                    datePickerDialog.show()
                }
                dialog.show()
            }
        }
    }

    private fun generateDummyData(): List<String> {
        val dummyDataList = mutableListOf<String>()

        dummyDataList.add("Shrabani")
        dummyDataList.add("Meghamala")
        dummyDataList.add("Swain")

        return dummyDataList
    }


}