package com.billing.marketing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.ItemEnquiryBinding

class EnquireListingAdapter(private val context: Context,private val enquiryDetails: List<EnquiryDetails>) : RecyclerView.Adapter<EnquireListingAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enquiry, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = enquiryDetails[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return enquiryDetails.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemEnquiryBinding.bind(itemView)

        fun bind(item: EnquiryDetails) {
            binding.tvName.text = item.customer_name
            binding.tvMobileNo.text = item.mobile_number
            binding.tvLocation.text = item.location
            binding.tvEnquiryDetails.text = item.details
            binding.ivEdit.setOnClickListener {
                // Handle item click here
                val intent = Intent(context, UpdateEnquire::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("customer_name", item.customer_name)
                intent.putExtra("mobile_number", item.mobile_number)
                intent.putExtra("location", item.location)
                intent.putExtra("details", item.details)
                intent.putExtra("enquiry_id", item.enquiry_id)
                context.startActivity(intent)
            }
        }
    }
}