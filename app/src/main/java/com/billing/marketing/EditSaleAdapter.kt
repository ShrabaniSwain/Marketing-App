package com.billing.marketing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.AddItemListBinding

class EditSaleAdapter(private val context: Context, private val saleProductItem: List<SaleProductList>) : RecyclerView.Adapter<EditSaleAdapter.CardViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null
    private var onItemClickedListener: ((Int) -> Unit)? = null
    fun setOnItemClickedListener(listener: (Int) -> Unit) {
        onItemClickedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_item_list, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = saleProductItem[position]
        holder.bind(item,position)
        holder.itemView.setOnClickListener {
            onItemClickedListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return saleProductItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = AddItemListBinding.bind(itemView)

        fun bind(item: SaleProductList,position: Int) {
            binding.tvItem.text = item.product_name
            binding.tvQnt.text = if (item.quantity?.isNotEmpty() == true && item.unit?.isNotEmpty() == true) {
                "${item.quantity} ${item.unit}"
            } else {
                "0"
            }
            val amountPrice = item.amount.toDoubleOrNull() ?: 0.0
            binding.tvAmount.text = "₹${String.format("%.2f", amountPrice)}"
            binding.tvRate.text = if (item.amount.isNullOrEmpty()) "₹0" else "₹${item.sale_price}"
            binding.tvDiscountAmount.text = if (item.dis_amt.isNullOrEmpty()) "₹0" else "₹${item.dis_amt}"
            binding.tvTax.text = if (item.tax_amount.isNullOrEmpty()) "₹0" else "₹${item.tax_amount}"
            val salePrice = if (item.sale_price.isNullOrEmpty()) 0 else item.sale_price.toInt()
            val quantity = if (item.quantity.isNullOrEmpty()) 0 else item.quantity.toInt()
            val totalPrice = quantity * salePrice

            val totalPriceText = if (totalPrice == 0) "₹0" else "₹$totalPrice"
            binding.tvTotalPrice.text = totalPriceText

            val invoiceNumber = position + 1
            binding.tvInvoiceNo.text = "#$invoiceNumber"

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }



}