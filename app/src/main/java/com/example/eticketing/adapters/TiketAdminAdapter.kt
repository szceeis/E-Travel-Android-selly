package com.example.eticketing.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eticketing.data.Ticket
import com.example.eticketing.databinding.ItemTiketAdminBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TiketAdminAdapter(
    private val onKonfirmasi: (Ticket) -> Unit,
    private val onBatal: (Ticket) -> Unit,
    private val isReadOnly: Boolean = false  // ← admin pakai true, pengelola pakai false
) : ListAdapter<Ticket, TiketAdminAdapter.ViewHolder>(DiffCallback()) {

    var userNames: Map<Long, String> = emptyMap()
    var destinationNames: Map<Long, String> = emptyMap()

    inner class ViewHolder(private val binding: ItemTiketAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ticket: Ticket) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id"))

            binding.tvTiketId.text = "Tiket #${ticket.id}"
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

            val destName = destinationNames[ticket.destinationId] ?: "Destinasi #${ticket.destinationId}"
            val userName = userNames[ticket.userId] ?: "User #${ticket.userId}"
            val date = dateFormat.format(Date(ticket.bookingDate))

            binding.tvTiketDestinasi.text = "🏝️ $destName"
            binding.tvTiketUser.text = "👤 $userName"
            binding.tvTiketInfo.text = "📅 $date  |  ${ticket.quantity} tiket  |  ${formatter.format(ticket.totalPrice)}"

            // Sembunyikan tombol aksi jika readOnly (admin)
            if (isReadOnly) {
                binding.btnKonfirmasi.visibility = View.GONE
                binding.btnBatal.visibility = View.GONE
            } else {
                binding.btnKonfirmasi.visibility = View.VISIBLE
                binding.btnBatal.visibility = View.VISIBLE
                val isFinal = ticket.status == "CONFIRMED" || ticket.status == "CANCELLED"
                binding.btnKonfirmasi.isEnabled = !isFinal
                binding.btnBatal.isEnabled = !isFinal
                binding.btnKonfirmasi.alpha = if (isFinal) 0.4f else 1f
                binding.btnBatal.alpha = if (isFinal) 0.4f else 1f
                binding.btnKonfirmasi.setOnClickListener { onKonfirmasi(ticket) }
                binding.btnBatal.setOnClickListener { onBatal(ticket) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTiketAdminBinding.inflate(
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