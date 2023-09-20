package com.flyngener.marketing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemEnquiryBinding

class EnquireListingAdapter() : RecyclerView.Adapter<EnquireListingAdapter.CardViewHolder>() {

    private val dummyDataList: List<String> = generateDummyData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enquiry, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = dummyDataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dummyDataList.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemEnquiryBinding.bind(itemView)

        fun bind(item: String) {
            binding.tvName.text = item
        }
    }

    private fun generateDummyData(): List<String> {
        val dummyDataList = mutableListOf<String>()

        dummyDataList.add("Suchitra")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")
        dummyDataList.add("Somi")

        return dummyDataList
    }
}