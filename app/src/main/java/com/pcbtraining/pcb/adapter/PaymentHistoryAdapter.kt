package com.pcbtraining.pcb.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pcbtraining.pcb.R
import com.pcbtraining.pcb.model.PaymentHistory

class PaymentHistoryAdapter(private val paymentHistoryList: List<PaymentHistory>) :
    RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        // Add other views as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_history, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentHistory = paymentHistoryList[position]
        holder.amountTextView.text = paymentHistory.price
        holder.dateTextView.text = paymentHistory.title
        // Set other views with corresponding data
    }

    override fun getItemCount(): Int {
        return paymentHistoryList.size
    }
}
