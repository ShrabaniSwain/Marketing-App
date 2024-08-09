package com.billing.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.ItemCustomerDetailsBinding


class CustomerDeatilsAdapter(private val context: Context, private val saleProductItem: List<SaleProductList>) : RecyclerView.Adapter<CustomerDeatilsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = saleProductItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return saleProductItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCustomerDetailsBinding.bind(itemView)

        fun bind(item: SaleProductList) {
            binding.tvProductName.text = item.product_name
            binding.tvSales.text = if (item.sale_price.isNullOrEmpty()) "₹0" else "₹${item.sale_price}"
            binding.tvQnt.text = if (item.quantity.isNullOrEmpty()) "0" else "${item.quantity}"
            val amountPrice = item.amount.toDoubleOrNull() ?: 0.0
            binding.tvAmount.text = "₹${String.format("%.2f", amountPrice)}"
        }
    }

}