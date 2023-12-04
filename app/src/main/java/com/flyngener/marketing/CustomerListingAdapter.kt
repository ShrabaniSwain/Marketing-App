package com.flyngener.marketing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemCustomerListingBinding

class CustomerListingAdapter(private val context: Context, private val customerDetails: List<CustomerDetails>) : RecyclerView.Adapter<CustomerListingAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_listing, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = customerDetails[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return customerDetails.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCustomerListingBinding.bind(itemView)

        fun bind(item: CustomerDetails) {
            binding.tvName.text = item.customer_name
            binding.tvMobileNo.text = item.mobile_number
            binding.tvDate.text = item.join_date
            binding.tvState.text = item.state
            binding.tvCredit.text =  if (item.due_balance.isNullOrEmpty()) "₹0" else "₹${item.due_balance}"
            binding.ivEdit.setOnClickListener {
                val intent = Intent(context, EditCustomer::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("customer_name", item.customer_name)
                intent.putExtra("mobile_number", item.mobile_number)
                intent.putExtra("email_id", item.email_id)
                intent.putExtra("gender", item.gender)
                intent.putExtra("city", item.city)
                intent.putExtra("customer_id", item.customer_id)
                intent.putExtra("pin_code", item.pincode)
                intent.putExtra("address", item.full_address)
                intent.putExtra("state", item.state)
                context.startActivity(intent)
            }
        }
    }

}