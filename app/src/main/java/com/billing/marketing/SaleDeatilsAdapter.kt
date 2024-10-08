package com.billing.marketing

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.billing.marketing.databinding.ItemSaleDetailsBinding

class SaleDeatilsAdapter(private val context: Context, private val salereportItem: List<SaleReportItem>) : RecyclerView.Adapter<SaleDeatilsAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale_details, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = salereportItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return salereportItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemSaleDetailsBinding.bind(itemView)

        fun bind(item: SaleReportItem) {
            binding.tvName.text = item.customer_name
            binding.tvDate.text = item.date
            binding.tvQnt.text = if (item.qnt.isNullOrEmpty()) "₹0" else "₹${item.qnt}"
            binding.tvAmount.text = if (item.amount.isNullOrEmpty()) "₹0" else "₹${item.amount}"
            binding.tvPaid.text = if (item.paid.isNullOrEmpty()) "₹0" else "₹${item.paid}"
            binding.tvBalance.text = if (item.balance.isNullOrEmpty()) "₹0" else "₹${item.balance}"
            binding.ivPrint.setOnClickListener {
                onPrintButtonClick(item)
            }
        }
        private fun onPrintButtonClick(item: SaleReportItem) {
            val pdfUrl = "https://flyngener.com/rr/admin/mobile/invoice.php?sale_id=${item.sale_id}"
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(pdfUrl))
            request.setTitle("PDF Download")
            request.setDescription("Downloading PDF")
            val destinationDirectory = Environment.DIRECTORY_DOWNLOADS
            request.setDestinationInExternalPublicDir(destinationDirectory, "invoice.pdf")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val downloadId = downloadManager.enqueue(request)
            Toast.makeText(context, "PDF download started", Toast.LENGTH_SHORT).show()
        }


    }
}