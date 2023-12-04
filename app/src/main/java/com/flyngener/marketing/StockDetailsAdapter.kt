package com.flyngener.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemStockDetailsBinding

class StockDetailsAdapter(private val context: Context, private val stockReportItem: List<StockReportItem>) : RecyclerView.Adapter<StockDetailsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = stockReportItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return stockReportItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStockDetailsBinding.bind(itemView)

        fun bind(item: StockReportItem) {
            binding.tvProductName.text = item.product_name
            binding.tvSales.text = if (item.sale_price.isNullOrEmpty()) "₹0" else "₹${item.sale_price}"
            binding.tvCredit.text = if (item.credit.isNullOrEmpty()) "0" else item.credit
            binding.tvSold.text = if (item.sold.isNullOrEmpty()) "0" else item.sold
            binding.tvReturn.text = if (item.stock_returned.isNullOrEmpty()) "0" else item.stock_returned
            binding.tvInStock.text = if (item.in_stock.isNullOrEmpty()) "0" else item.in_stock
        }
    }
}