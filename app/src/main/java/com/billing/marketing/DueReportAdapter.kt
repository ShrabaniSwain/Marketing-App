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
import com.billing.marketing.databinding.ItemDueReportBinding


class DueReportAdapter(private val context: Context, private val receivedReportItem: List<DueItem>) : RecyclerView.Adapter<DueReportAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_due_report, parent, false)
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
        private val binding = ItemDueReportBinding.bind(itemView)

        fun bind(item: DueItem) {
            binding.tvName.text = item.customer_name
            binding.tvDate.text = item.date
            binding.tvBalance.text = item.due_amount
            binding.tvInvoice.text = "#${item.invoice_no}"
            binding.tvQnt.text = item.qty
            binding.tvPaid.text = item.paid_amount
            binding.tvAmount.text = if (item.total_amount.isNullOrEmpty()) "₹0" else "₹${item.total_amount}"

            binding.ivPrint.setOnClickListener {
                onPrintButtonClick(item)
            }
        }

        private fun onPrintButtonClick(item: DueItem) {
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