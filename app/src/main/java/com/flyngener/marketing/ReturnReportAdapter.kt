package com.flyngener.marketing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemDueReportBinding
import com.flyngener.marketing.databinding.ItemReturnReportBinding

class ReturnReportAdapter() : RecyclerView.Adapter<ReturnReportAdapter.CardViewHolder>() {

    private val dummyDataList: List<String> = generateDummyData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_return_report, parent, false)
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
        private val binding = ItemReturnReportBinding.bind(itemView)

        fun bind(item: String) {
            binding.tvItemName.text = item
        }
    }

    private fun generateDummyData(): List<String> {
        val dummyDataList = mutableListOf<String>()

        dummyDataList.add("Rice")
        dummyDataList.add("Water")

        return dummyDataList
    }
}