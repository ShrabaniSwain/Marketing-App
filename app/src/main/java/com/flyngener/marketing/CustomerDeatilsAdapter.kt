package com.flyngener.marketing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemStockDetailsBinding


class CustomerDeatilsAdapter() : RecyclerView.Adapter<CustomerDeatilsAdapter.CardViewHolder>() {

    private val dummyDataList: List<String> = generateDummyData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock_details, parent, false)
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
        private val binding = ItemStockDetailsBinding.bind(itemView)

        fun bind(item: String) {
            binding.tvProductName.text = item
        }
    }

    private fun generateDummyData(): List<String> {
        val dummyDataList = mutableListOf<String>()

        dummyDataList.add("Face Wash")
        dummyDataList.add("Saop")
        dummyDataList.add("Shampoo")
        dummyDataList.add("Conditioner")
        dummyDataList.add("Cream")
        dummyDataList.add("Alma Hair")
        dummyDataList.add("Shampoo")
        dummyDataList.add("Shampoo")

        return dummyDataList
    }
}