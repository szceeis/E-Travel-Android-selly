package com.example.eticketing.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eticketing.data.Ticket
import com.example.eticketing.databinding.ItemTicketBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketAdapter(
    private var tickets: List<Pair<Ticket, String>> // Ticket and Destination Name
) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemTicketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (ticket, destName) = tickets[position]
        holder.binding.tvTicketDestName.text = destName

        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.binding.tvTicketDate.text = sdf.format(Date(ticket.bookingDate))

        holder.binding.tvTicketStatus.text = ticket.status
    }

    override fun getItemCount() = tickets.size

    fun updateData(newList: List<Pair<Ticket, String>>) {
        tickets = newList
        notifyDataSetChanged()
    }
}