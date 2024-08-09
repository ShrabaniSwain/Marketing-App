package com.billing.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.AddItemListBinding

class ItemsAdapter(private val context: Context, private val itemList: List<ProductData>) : RecyclerView.Adapter<ItemsAdapter.CardViewHolder>() {

    private var onItemClickedListener: ((Int) -> Unit)? = null
    fun setOnItemClickedListener(listener: (Int) -> Unit) {
        onItemClickedListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item,position)
        holder.itemView.setOnClickListener {
            onItemClickedListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = AddItemListBinding.bind(itemView)

        fun bind(item: ProductData,position: Int) {
            binding.tvItem.text = item.details
            binding.tvAmount.text = if (item.amount.isEmpty()) "₹0" else "₹${item.amount}"
            binding.tvTax.text = if (item.tax_amount2.isEmpty()) "₹0" else "₹${item.tax_amount2}"
            binding.tvDiscountAmount.text = if (item.dis_amt.isEmpty()) "₹0" else "₹${item.dis_amt}"
            binding.tvQnt.text = if (item.quantity.isEmpty()) "₹0" else "${item.quantity}${" "}${item.unit}"
            binding.tvRate.text = if (item.sale_price.isEmpty()) "₹0" else "₹${item.sale_price}"
            val quantity = if (item.quantity.isNotEmpty()) item.quantity.toInt() else 0
            val quantityText = if (quantity == 0) "₹0" else "₹$quantity"
            val salePrice = if (item.sale_price.isNotEmpty()) item.sale_price.toInt() else 0
            val totalPrice = quantity * salePrice

            val totalPriceText = if (totalPrice == 0) "₹0" else "₹$totalPrice"
            binding.tvTotalPrice.text = totalPriceText

            val invoiceNumber = position + 1
            binding.tvInvoiceNo.text = "#$invoiceNumber"

        }
    }
}