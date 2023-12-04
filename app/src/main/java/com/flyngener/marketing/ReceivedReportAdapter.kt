package com.flyngener.marketing

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.flyngener.marketing.databinding.ItemReceivedDetailsBinding
import com.flyngener.marketing.databinding.ItemReceivedReportBinding
import com.flyngener.marketing.databinding.ItemReturnReportBinding


class ReceivedReportAdapter(private val context: Context, private val receivedReportItem: List<ReceivedReportItem>) : RecyclerView.Adapter<ReceivedReportAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_report, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = receivedReportItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return receivedReportItem.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemReceivedReportBinding.bind(itemView)

        fun bind(item: ReceivedReportItem) {
            binding.tvName.text = item.customer_name
            binding.tvDate.text = item.date
            binding.tvAmount.text = if (item.paid.isNullOrEmpty()) "₹0" else "₹${item.paid}"
            binding.ivPrint.setOnClickListener {
                onPrintButtonClick(item)
            }
        }

        private fun onPrintButtonClick(item: ReceivedReportItem) {
            val pdfUrl = "https://flyngener.com/rr/admin/mobile/due_invoice.php?sale_id=${item.sale_id}"
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