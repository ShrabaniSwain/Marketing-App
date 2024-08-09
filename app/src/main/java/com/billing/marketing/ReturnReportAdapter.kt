package com.billing.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.ItemReturnReportBinding

class ReturnReportAdapter(private val context: Context, private val returnReportItem: List<ReturnItem>) : RecyclerView.Adapter<ReturnReportAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_return_report, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = returnReportItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return returnReportItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReturnReportBinding.bind(itemView)

        fun bind(item: ReturnItem) {
            binding.tvItemName.text = item.product_name
            binding.tvQnt.text = if (item.return_qty.isNullOrEmpty()) "0" else "${item.return_qty}"
            binding.tvDate.text = item.return_date
            binding.tvAmount.text = item.return_amount
        }
    }
}