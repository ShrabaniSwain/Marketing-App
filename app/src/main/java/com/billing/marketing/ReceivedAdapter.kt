package com.billing.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.ItemReceivedDetailsBinding

class ReceivedAdapter(private val context: Context, private val receivedReportItem: List<ReceivedReportItem>) : RecyclerView.Adapter<ReceivedAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = receivedReportItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return receivedReportItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReceivedDetailsBinding.bind(itemView)

        fun bind(item: ReceivedReportItem) {
            binding.tvName.text = item.customer_name
            binding.tvDate.text = item.date
            binding.tvInvoice.text = "#${item.invoice_no}"
            binding.tvAmount.text = if (item.paid.isNullOrEmpty()) "₹0" else "₹${item.paid}"
        }
    }

}