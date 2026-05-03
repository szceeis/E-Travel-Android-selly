package com.example.eticketing.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eticketing.data.Ticket
import com.example.eticketing.databinding.ItemTiketUserBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TiketUserAdapter : ListAdapter<Ticket, TiketUserAdapter.ViewHolder>(DiffCallback()) {

    var destinationNames: Map<Long, String> = emptyMap()

    inner class ViewHolder(private val binding: ItemTiketUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: Ticket) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id"))

            binding.tvTiketId.text = "Tiket #${ticket.id}"
            binding.tvNamaDestinasi.text =
                "🏝️ ${destinationNames[ticket.destinationId] ?: "Destinasi #${ticket.destinationId}"}"
            binding.tvTanggalBooking.text =
                "📅 ${dateFormat.format(Date(ticket.bookingDate))}"
            binding.tvInfoTiket.text =
                "${ticket.quantity} tiket  •  ${formatter.format(ticket.totalPrice)}"

            binding.tvTiketStatus.text = ticket.status
            when (ticket.status) {
                "CONFIRMED" -> {
                    binding.tvTiketStatus.setBackgroundColor(Color.parseColor("#C8E6C9"))
                    binding.tvTiketStatus.setTextColor(Color.parseColor("#1B5E20"))
                }
                "CANCELLED" -> {
                    binding.tvTiketStatus.setBackgroundColor(Color.parseColor("#FFCDD2"))
                    binding.tvTiketStatus.setTextColor(Color.parseColor("#B71C1C"))
                }
                else -> {
                    binding.tvTiketStatus.setBackgroundColor(Color.parseColor("#FFF9C4"))
                    binding.tvTiketStatus.setTextColor(Color.parseColor("#F57F17"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTiketUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Ticket>() {
        override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket) = oldItem == newItem
    }
}